package com.jakeapp.core.synchronization.request;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.services.ICSManager;
import com.jakeapp.core.synchronization.IInternalSyncService;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.core.synchronization.helpers.MessageMarshaller;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.jake.fss.FSService;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.filetransfer.FileRequestFileMapper;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.status.ILoginStateListener;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class ProjectRequestListener
				implements IMessageReceiveListener, IOnlineStatusListener,
				ILoginStateListener, IncomingTransferListener, FileRequestFileMapper {

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

	private ICSManager ICSManager;

	private ProjectApplicationContextFactory db;

	private IInternalSyncService syncService;

	private MessageMarshaller messageMarshaller;

	private static Logger log = Logger.getLogger(ProjectRequestListener.class);


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


	private void sendLogs(Project project, com.jakeapp.jake.ics.UserId user) {
		ICService ics = ICSManager.getICService(project);

		try {
			List<LogEntry<? extends ILogable>> logs = db.getLogEntryDao(project).getAll();
			String message = messageMarshaller.packLogEntries(project, logs);
			ics.getMsgService().sendMessage(user, message);
		} catch (NetworkException e) {
			log.warn("Could not sync logs", e);
		} catch (OtherUserOfflineException e) {
			log.warn("Could not sync logs", e);
		}
	}


	private String getProjectUUID(String content) {
		int begin = content.indexOf(BEGIN_PROJECT_UUID) + BEGIN_PROJECT_UUID.length();
		int end = content.indexOf(END_PROJECT_UUID);

		return content.substring(begin, end);
	}

	@Override 
	@Transactional
	public void receivedMessage(com.jakeapp.jake.ics.UserId from_userid,
					String content) {


		String projectUUID = getProjectUUID(content);
		log.debug("Received a message for project " + projectUUID);

		if (!projectUUID.equals(p.getProjectId())) {
			log.debug("Discarding message because it's not for this project");
			return;
		}

		log.debug("Message is for this project!");

		String message = content.substring(BEGIN_PROJECT_UUID.length() + projectUUID
						.length() + END_PROJECT_UUID.length());
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
			return;
		}

		if (message.startsWith(REQUEST_LOGS_MESSAGE)) {
			log.info("Received logs request from " + from_userid.getUserId());

			sendLogs(p, from_userid);
			return;
		}

		if (message.startsWith(LOGENTRIES_MESSAGE)) {
			log.info("Received serialized logentries from " + from_userid.getUserId());

			String les = message.substring(LOGENTRIES_MESSAGE.length() + BEGIN_LOGENTRY
							.length(), message.length() - END_LOGENTRY.length());

			List<LogEntry<? extends ILogable>> logEntries =
							this.messageMarshaller.unpackLogEntries(les);

			for (LogEntry<? extends ILogable> entry : logEntries) {
				try {
					log.debug("Deserialized successfully, it is a " + entry
									.getLogAction() + " for object UUID " + entry.getObjectuuid());
					db.getLogEntryDao(p).create(entry);
				} catch (Throwable t) {
					log.debug("Failed to deserialize and/or save", t);
				}
			}
			return;
		}
		log.warn("We got a unknown/unhandled Message: " + message);
	}

	@Override
	public void onlineStatusChanged(com.jakeapp.jake.ics.UserId userid) {

		log.info("Online status of " + userid
						.getUserId() + " changed... (Project " + p + ")");
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
										filerequest))) {
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
		// TODO: maybe we want to watch. maybe we don't.
	}

	private File getDeliveryDirectory() {
		String systmpdir = System.getProperty("java.io.tmpdir", "");
		if (!systmpdir.endsWith(File.separator))
			systmpdir = systmpdir + File.separator;

		File f = new File(systmpdir);
		Assert.assertEquals("tmpdir", systmpdir, f.getAbsolutePath() + File.separator);

		return new File(systmpdir + File.separator + "jakeDelivery");
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