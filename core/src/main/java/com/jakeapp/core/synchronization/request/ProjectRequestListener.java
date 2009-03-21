package com.jakeapp.core.synchronization.request;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.DarkMagic;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.services.ICSManager;
import com.jakeapp.core.services.futures.AllJakeObjectsFuture;
import com.jakeapp.core.synchronization.IInternalSyncService;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.core.synchronization.helpers.MessageMarshaller;
import com.jakeapp.core.util.AvailableLaterWaiter;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.jake.fss.FSService;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.AdditionalFileTransferData;
import com.jakeapp.jake.ics.filetransfer.FileRequestFileMapper;
import com.jakeapp.jake.ics.filetransfer.ITransferListener;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.TransferWatcherThread;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.status.ILoginStateListener;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;

public class ProjectRequestListener
				implements IMessageReceiveListener, IOnlineStatusListener,
				ILoginStateListener, IncomingTransferListener, FileRequestFileMapper {

	private static Logger log = Logger.getLogger(ProjectRequestListener.class);

	private static final String BEGIN_LOGENTRY = "<le>";

	private static final String END_LOGENTRY = "</le>";

	private static final String BEGIN_PROJECT_UUID = "<project>";

	private static final String END_PROJECT_UUID = "</project>";

	private static final String LOGENTRIES_MESSAGE = "<logentries/>";

	private static final String REQUEST_LOGS_MESSAGE = "<requestlogs/>";

	private static final String NEW_FILE = "<newfile/>";

	private static final String POKE_MESSAGE = "<poke/>"; // dup

	private static final String NEW_NOTE = "<newnote/>";

	private Project p;

	private ICSManager ICSManager; // fixme: bogus name

	private ProjectApplicationContextFactory db;

	private IInternalSyncService syncService;

	private MessageMarshaller messageMarshaller;

	public ProjectRequestListener(Project p, ICSManager icsManager,
					ProjectApplicationContextFactory db, IInternalSyncService syncService,
					MessageMarshaller messageMarshaller) {
		this.p = p;
		this.ICSManager = icsManager;
		this.db = db;
		this.syncService = syncService;
		this.messageMarshaller = messageMarshaller;
	}


	public ICSManager getICSManager() {
		return ICSManager;
	}


	private String getProjectUUID(String content) {
		int begin = content.indexOf(BEGIN_PROJECT_UUID) + BEGIN_PROJECT_UUID.length();
		int end = content.indexOf(END_PROJECT_UUID);
		if(end == -1 || begin == -1)
			return null;
		return content.substring(begin, end);
	}

	@Override @Transactional
	public void receivedMessage(com.jakeapp.jake.ics.UserId from_userid,
					String content) {

		try{
			String projectUUID = getProjectUUID(content);
			if(content == null)
				return;
			
			log.debug("Received a message for project " + projectUUID);
	
			if (projectUUID == null || !projectUUID.equals(p.getProjectId())) {
				log.debug("Discarding message because it's not for this project");
				return;
			}
	
			log.debug("Message is for this project!");
	
			String message = content.substring(
							BEGIN_PROJECT_UUID.length() + projectUUID.length() + END_PROJECT_UUID
											.length());
			log.debug("Message content: \"" + message + "\"");
	
			if (message.startsWith(POKE_MESSAGE)) {
				log.info("Received poke from " + from_userid.getUserId());
				log.debug("This means we should sync logs!");
	
				// Eventually, this should consider things such as trust
				User user = getICSManager().getFrontendUserId(p, from_userid);
				try {
					syncService.startLogSync(p, user);
				} catch (IllegalProtocolException e) {
					// This should neeeeeeeeever happen
					log.fatal(
									"Received an unexpected IllegalProtocolException while trying to perform logsync",
									e);
				}
	
				// inform the gui that we have new data
				this.syncService.getProjectChangeListener()
								.syncStateChanged(this.p, ChangeListener.SyncState.SYNCING);
				
				return;
			}
	
			if (message.startsWith(REQUEST_LOGS_MESSAGE)) {
				log.info("Received logs request from " + from_userid.getUserId());
	
				syncService.sendLogs(p, from_userid);
				return;
			}
	
			if (message.startsWith(LOGENTRIES_MESSAGE)) {
				log.info("Received serialized logentries from " + from_userid.getUserId());
	
				String les = message.substring(
								LOGENTRIES_MESSAGE.length() + BEGIN_LOGENTRY.length(),
								message.length() - END_LOGENTRY.length());
	
				List<LogEntry<? extends ILogable>> logEntries =
								this.messageMarshaller.unpackLogEntries(les);
	
				Map<String, FileObject> fileObjects = getAllFileObjects();
				
				
				for (LogEntry<? extends ILogable> entry : logEntries) {
					try {
						if (entry.getBelongsTo() instanceof FileObject) {
							FileObject fo = (FileObject) entry.getBelongsTo();
							adjustUUID(fileObjects, entry, fo);
						}
						if (entry.getBelongsTo() instanceof Tag) {
							FileObject fo = (FileObject) ((Tag) entry
									.getBelongsTo()).getObject();
							adjustUUID(fileObjects, entry, fo);
						}
						// TODO: do the same with tags.
						log.debug("Deserialized successfully, it is a " + entry
										.getLogAction() + " for object UUID " + entry.getObjectuuid());
						try {
							db.getLogEntryDao(p).create(entry);
							
							/*
							//TODO do it differently - implement conflict management!
							if (entry.getBelongsTo() instanceof NoteObject) {
								log.warn("persisting noteobject");
								db.getNoteObjectDao(p).persist(
									this.syncService.pullObject((NoteObject)(entry.getBelongsTo()))
								);
								log.warn("persisting noteobject done");
								//TODO notify gui
							}
							*/
						} catch (IllegalArgumentException ignored) {
							//duplicate entry: we already have this entry
						}
					} catch (Throwable t) {
						log.debug("Failed to deserialize and/or save", t);
					}
				}
				// inform the gui that we have new data
				this.syncService.getProjectChangeListener()
								.syncStateChanged(this.p, ChangeListener.SyncState.DONE);
	
				return;
			}
			log.warn("We got a unknown/unhandled Message: " + message);
		}catch(Exception e) {
			log.error("handling message failed: " + content, e);
		}
	}

	/**
	 * We do not want local files and remote files with the same relpath to have
	 * different UUIDs. So we check if we already have a file with that relpath
	 * and adjust incoming LogEntries accordingly.
	 * 
	 * @param fileObjects
	 * @param entry
	 * @param fo
	 */
	@DarkMagic
	private void adjustUUID(Map<String, FileObject> fileObjects,
			LogEntry<? extends ILogable> entry, FileObject fo) {
		if (entry.getBelongsTo() instanceof FileObject) {
			if (fileObjects.containsKey(fo.getRelPath())) {
				UUID localuuid = fileObjects.get(fo.getRelPath()).getUuid();
				log.info("adjusting uuid of incoming fileObject Entry: "
						+ fo.getUuid() + " --> " + localuuid);
				fo = new FileObject(localuuid, fo.getProject(), fo.getRelPath());
				((LogEntry<FileObject>) entry).setBelongsTo(fo);
			}
			if (entry.getObjectuuid() == null || ((LogEntry<FileObject>)entry).getBelongsTo().getUuid() == null)
				throw new IllegalArgumentException("logentry has null uuid for FileObject");
		}
		if (entry.getBelongsTo() instanceof Tag) {
			if (fileObjects.containsKey(fo.getRelPath())) {
				UUID localuuid = fileObjects.get(fo.getRelPath()).getUuid();
				log.info("adjusting uuid of incoming fileObject Entry: "
						+ fo.getUuid() + " --> " + localuuid);
				fo = new FileObject(localuuid, fo.getProject(), fo.getRelPath());
				((Tag)entry.getBelongsTo()).setObject(fo);
				((LogEntry<Tag>) entry).setBelongsTo((Tag)entry.getBelongsTo());
			}
			if (entry.getObjectuuid() == null || ((LogEntry<FileObject>)entry).getBelongsTo().getUuid() == null)
				throw new IllegalArgumentException("logentry has null uuid for FileObject");
		}
	}


	private Map<String, FileObject> getAllFileObjects() throws Exception {
		Collection<JakeObject> allObjects = AvailableLaterWaiter.await(new AllJakeObjectsFuture(db, p));
		Map<String, FileObject> fileObjects = new HashMap<String, FileObject>();
		for(JakeObject jo : allObjects) {
			if(jo instanceof FileObject) {
				FileObject fo = (FileObject) jo;
				fileObjects.put(fo.getRelPath(), fo);
			}
		}
		return fileObjects;
	}

	@Override
	public void onlineStatusChanged(com.jakeapp.jake.ics.UserId userid) {
		// log.trace("Online status of " + userid
		// 				.getUserId() + " possibly changed... (Project " + p + ")");
		// fixme: send this event up to gui!

		// fixme: causes infinite loop - only send events up if there's really a change!!
		//this.syncService.getProjectChangeListener().onlineStatusChanged(p);
	}

	public void loginHappened() {
		log.info("We logged in with project " + this.p);
		try {
			getICSManager().getTransferService(p).startServing(this, this);
		} catch (NotLoggedInException e) {
			log.error("error starting file serving", e);
		}
	}

	public void logoutHappened() {
		log.info("We logged out with project " + this.p);

		try {
			// only stop the transfer service if it exists.
			if (getICSManager().hasTransferService(p)) {
				getICSManager().getTransferService(p).stopServing();
			}
		} catch (NotLoggedInException e) {
			// ignore
		}
	}

	@Override public void connectionStateChanged(ConnectionState le, Exception ex) {
		if (ConnectionState.LOGGED_IN == le) {
			loginHappened();
		} else if (ConnectionState.LOGGED_OUT == le) {
			logoutHappened();
		}
	}

	private FileObject getFileObjectForRequest(String filerequest) {
		if (!p.getProjectId()
						.equals(this.messageMarshaller.getProjectUUIDFromRequestMessage(
										filerequest).toString())) {
			log.debug("got request for a different project");
			return null; // not our project
		}
		UUID leuuid =
						this.messageMarshaller.getLogEntryUUIDFromRequestMessage(filerequest);
		LogEntry<? extends ILogable> le;
		try {
			le = db.getLogEntryDao(p).get(leuuid);
		} catch (NoSuchLogEntryException e) {
			log.debug("we don't know about this version");
			return null;
		}

		if (le.getLogAction() != LogAction.JAKE_OBJECT_NEW_VERSION) {
			log.debug("the requested logentry is not a version");
			return null;
		}

		log.debug("got request for file belonging to entry " + leuuid);

		FileObject fo = (FileObject) le.getBelongsTo();

		LogEntry<JakeObject> version;
		try {
			version = db.getLogEntryDao(p).getLastVersionOfJakeObject(fo);
		} catch (NoSuchLogEntryException e1) {
			log.debug("we don't have a version");
			return null;
		}
		if (!version.getUuid().equals(leuuid)) {
			log.debug("we have a other last version");
			return null;
		}
		Attributed<FileObject> status;
		try {
			status = syncService.getJakeObjectSyncStatus(fo);
		} catch (Exception e) {
			log.debug("status of the requested object is weird", e);
			return null;
		}
		if (status.isModifiedLocally()) {
			log.debug("can't distribute tainted object");
			return null;
		}
		return fo;
	}

	@Override
	public boolean accept(FileRequest req) {
		try {
			log.info("incoming request: " + req);
			FileObject fo = getFileObjectForRequest(req.getFileName());
			if (fo == null) {
				// reason has already been logged.
				return false;
			}
			log.info("we accept the request");
			return true;
		} catch (Exception e) {
			log.warn("unexpected Exception", e);
			return false;
		}
	}


	@Override
	public void started(IFileTransfer t) {
		log.debug("we are transmitting." + t);
		new TransferWatcherThread(t, new ITransferListener() {

			@Override
			public void onFailure(AdditionalFileTransferData transfer,
					String error) {
				log.warn("transmitting failed: " + error);
			}

			@Override
			public void onSuccess(AdditionalFileTransferData transfer) {
				log.info("transmitting was successful");
			}

			@Override
			public void onUpdate(AdditionalFileTransferData transfer,
					Status status, double progress) {
				log.debug("transmitting update: " + progress + " - " + status);
			}
			
		});
	}

	private File getDeliveryDirectory() {
		String systmpdir = System.getProperty("java.io.tmpdir", "");
		if (!systmpdir.endsWith(File.separator))
			systmpdir = systmpdir + File.separator;

		File f = new File(systmpdir, "jakeDelivery");
		f.mkdir();
		return f;
	}

	@Override
	public File getFileForRequest(FileRequest req) {
		// this is a interesting function. watch this:
		try {
			log.info("incoming request: " + req);
			FileObject fo = getFileObjectForRequest(req.getFileName());
			if (fo == null) {
				// reason has already been logged.
				return null;
			}
			File origfile = syncService.getFile(fo);
			log.info("original file at " + origfile);
			File tempfile = new File(getDeliveryDirectory(), req.getFileName());

			FSService.writeFileStreamAbs(tempfile.getAbsolutePath(),
							FSService.readFileStreamAbs(origfile.getAbsolutePath()));

			log.info("we accept the request and provided the file at " + tempfile);

			return tempfile;
		} catch (Exception e) {
			log.warn("unexpected Exception", e);
			return null;
		}
	}
}