package com.jakeapp.violet.synchronization;

import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.RequestLogsMessage;
import com.jakeapp.violet.synchronization.request.MessageMarshaller;
import com.jakeapp.violet.synchronization.request.ProjectRequestListener;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class should be active whenever you want to use files
 * <p/>
 * On Project->pause/start call {@link #startServing(Project, ChangeListener)}
 * and {@link #stopServing(Project)}
 * <p/>
 * Even when you are offline, this is to be used.
 * 
 * @author johannes
 */
public class SyncServiceImpl {
	private static final Logger log = Logger.getLogger(SyncServiceImpl.class);

	private MessageMarshaller messageMarshaller = DI
			.getImpl(MessageMarshaller.class);

	private ProjectModel model;

	public void sendLogs(UserId user) throws TimeoutException,
			NoSuchUseridException, NetworkException, OtherUserOfflineException,
			IOException, SQLException {
		ICService ics = model.getIcs();
		List<LogEntry> logs = model.getLog().getAll(false);
		String message = messageMarshaller.packLogEntries(model.getProjectid(),
				logs);
		ics.getMsgService().sendMessage(user, message);
	}

	public void pullObject(JakeObject jo) {
		LogEntry le = model.getLog().getLastOfJakeObject(jo, true);
		log.debug("got logentry: " + le);
		if (le == null || le.getHow().isEmpty()) { // delete
			log.debug("lets delete it");
			model.getFss().deleteFile(jo.getRelPath());
			log.debug("deleted.");
		} else {
			log.debug("Pulling a fileobject...");
			pullFileObject(jo);
		}
	}

	public void startLogSync(User pm) throws TimeoutException,
			NoSuchUseridException, NetworkException, OtherUserOfflineException {
		log.info("Requesting log sync from user " + pm.getUserId());
		ICService ics = model.getIcs();
		RequestLogsMessage msg = new RequestLogsMessage();
		msg.setProjectId(model.getProjectid());
		msg.setUser(DI.getUserId(pm.getUserId()));
		String message = messageMarshaller.serialize(msg);
		ics.getMsgService().sendMessage(msg.getUser(), message);
	}

	@Transactional
	public Attributed<FileObject> getJakeObjectSyncStatus(FileObject foin)
			throws InvalidFilenameException, IOException {
		log.trace("get JakeObjectStatus for " + foin);
		if (foin == null) {
			throw new IllegalArgumentException("FileObject is null!");
		}

		Project p = foin.getProject();
		IFSService fss = getFSS(p);
		ILogEntryDao led = db.getUnprocessedAwareLogEntryDao(p);

		// this is very similar, but slightly different to the NoteObject code
		// compare and edit them side-by-side

		// 0 complete
		FileObject fo;
		try {
			fo = completeIncomingObject(foin);
		} catch (NoSuchJakeObjectException e) {
			log.trace("FileObject is not in database. nulling uuid");
			fo = new FileObject(p, foin.getRelPath());
		}
		// 1 exists?
		boolean objectExistsLocally = fss.fileExists(fo.getRelPath());

		// 2 last logAction
		LogEntry<FileObject> lastle;
		try {
			lastle = led.getLastVersionOfJakeObject(fo, true);
		} catch (NoSuchLogEntryException e1) {
			lastle = null;
		}
		LogAction lastVersionLogAction = getLogActionNullSafe(lastle);

		// 3 locally modified?
		boolean checksumEqualToLastNewVersionLogEntry;
		// 3.5 size
		long size = 0;
		LogEntry<FileObject> pulledle = null;
		try {
			if (objectExistsLocally) {
				try {
					size = fss.getFileSize(fo.getRelPath());
					pulledle = led.getLastVersionOfJakeObject(fo, false);
					checksumEqualToLastNewVersionLogEntry = pulledle
							.getChecksum().equals(
									fss.calculateHashOverFile(fo.getRelPath()));
				} catch (NotAReadableFileException e) {
					size = 0;
					checksumEqualToLastNewVersionLogEntry = false;
				} catch (FileNotFoundException e) {
					// we checked above.
					throw new IllegalStateException("should not occur", e);
				}
			} else {
				checksumEqualToLastNewVersionLogEntry = false;
				size = 0;
				// TODO: We don't know the file size when it's remote.
			}
		} catch (NoSuchLogEntryException e1) {
			// size = 0;
			pulledle = null;
			checksumEqualToLastNewVersionLogEntry = false;
		}

		// 4 timestamp
		long lastModificationDate = 0;

		if (lastVersionLogAction == null) {
			if (objectExistsLocally) {
				try {
					lastModificationDate = fss.getLastModified(fo.getRelPath());
				} catch (NotAFileException e) {
				}
			}
		} else {
			// do we have a newer file?
			// TODO: remember to set the modification time on pull, otherwise
			// this is always true
			if (lastle.getTimestamp().getTime() > lastModificationDate)
				lastModificationDate = lastle.getTimestamp().getTime();
		}

		LogEntry<JakeObject> locklog = led.getLock(fo);

		// 5 unprocessed
		boolean hasUnprocessedLogEntries = led.hasUnprocessed(fo);
		// 6 lastprocessed
		LogAction lastProcessedLogAction = getLogActionNullSafe(pulledle);

		return new Attributed<FileObject>(fo, lastle, locklog,
				objectExistsLocally, !checksumEqualToLastNewVersionLogEntry,
				hasUnprocessedLogEntries, lastProcessedLogAction,
				lastModificationDate, size);
	}

	@Transactional
	public Attributed<NoteObject> getJakeObjectSyncStatus(NoteObject noin) {
		Project p = noin.getProject();
		ILogEntryDao led = db.getUnprocessedAwareLogEntryDao(p);

		log.trace("getting attributed note for " + noin + " in " + p);

		// this is very similar, but slightly different to the FileObject code
		// compare and edit them side-by-side

		// 0 complete + 1 exists?
		NoteObject no;
		boolean objectExistsLocally;
		try {
			no = completeIncomingObject(noin);
			objectExistsLocally = true;
		} catch (NoSuchJakeObjectException e) {
			log.debug("NoteObject is not in database");
			no = noin;
			objectExistsLocally = false;
		}
		String content = no.getContent();

		long size;
		if (content == null)
			size = 0;
		else
			size = content.length();

		// 2 last logAction
		LogEntry<NoteObject> lastle;
		try {
			lastle = led.getLastVersionOfJakeObject(no, true);
		} catch (NoSuchLogEntryException e1) {
			lastle = null;
		}

		// 3 locally modified?
		boolean checksumEqualToLastNewVersionLogEntry;
		LogEntry<NoteObject> pulledle;
		try {
			pulledle = led.getLastVersionOfJakeObject(no, false);
			checksumEqualToLastNewVersionLogEntry = pulledle.getBelongsTo()
					.getContent().equals(content);
		} catch (NoSuchLogEntryException e1) {
			pulledle = null;
			checksumEqualToLastNewVersionLogEntry = false;
		}

		// 4 timestamp
		long lastModificationDate = 0;
		if (lastle != null)
			lastModificationDate = lastle.getTimestamp().getTime();

		// 5 unprocessed
		boolean hasUnprocessedLogEntries = led.hasUnprocessed(no);
		// 6 lastprocessed
		LogAction lastProcessedLogAction = getLogActionNullSafe(pulledle);

		no.setProject(p);

		log.trace("got attributed note for " + noin + " in " + no.getProject());
		return new Attributed<NoteObject>(no, lastle, led.getLock(no),
				objectExistsLocally, !checksumEqualToLastNewVersionLogEntry,
				hasUnprocessedLogEntries, lastProcessedLogAction,
				lastModificationDate, size);
	}

	@Transactional
	private User getMyProjectMember(Project p) {
		return p.getUserId();
	}

	private ICService getICS(Project p) {
		return getICSManager().getICService(p);
	}

	private IFSService getFSS(Project p) {
		try {
			return this.getProjectsFileServices().getProjectFSService(p);
		} catch (com.jakeapp.core.domain.exceptions.ProjectNotLoadedException e) {
			log.warn("Project not loaded: ", e);
			return null;
		}
	}

	public IProjectsFileServices getProjectsFileServices() {
		return this.projectsFileServices;
	}

	public void setProjectsFileServices(
			IProjectsFileServices projectsFileServices) {
		this.projectsFileServices = projectsFileServices;
	}

	@Transactional
	private FileObject getFileObjectByRelpath(Project p, String relpath)
			throws NoSuchJakeObjectException {
		return db.getFileObjectDao(p).complete(new FileObject(p, relpath));
	}

	private NoteObject completeIncomingObjectOrNew(NoteObject no) {
		try {
			return completeIncomingObject(no);
		} catch (NoSuchJakeObjectException e) {
			log.debug("completing object failed, not in database yet.");
			return no; // we accept the UUID
		}
	}

	private FileObject completeIncomingObjectOrNew(FileObject jo) {
		try {
			return completeIncomingObject(jo);
		} catch (NoSuchJakeObjectException e) {
			return new FileObject(UUID.randomUUID(), jo.getProject(),
					jo.getRelPath());
		}
	}

	/**
	 * Pulls a Fileobject from any available source and - if the pull has been
	 * successful - sets all LogEntries that became obsoleted by the pull to
	 * processed.
	 * 
	 * @param p
	 *            The Project the fileobject should belong to
	 * @param fo
	 *            The File to pull
	 * @return fo null if pull was not successful
	 * @throws NotLoggedInException
	 *             if there is not logged in MsgService for p
	 * @throws NoSuchLogEntryException
	 *             if there are no LogEntries for the JakeObject.
	 * @throws IllegalArgumentException
	 *             if p or fo were null.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public FileObject pullFileObject(Project p, FileObject fo)
			throws NotLoggedInException, NoSuchLogEntryException,
			IllegalArgumentException {
		// preconditions-check
		if (p == null)
			throw new IllegalArgumentException("Project may not be null");
		if (fo == null)
			throw new IllegalArgumentException("FileObject may not be null");
		if (p.getMessageService() == null
				|| p.getMessageService().getVisibilityStatus() != VisibilityStatus.ONLINE)
			throw new NotLoggedInException();
		if (!getICS(p).getStatusService().isLoggedIn()) {
			log.warn("Project not online");
			throw new NotLoggedInException();
		}

		IFileTransferService ts;
		IFileTransfer result = null;
		FileRequest fr;
		Iterable<LogEntry> potentialProviders = this.getRequestHandlePolicy()
				.getPotentialJakeObjectProviders(fo);

		UserId remoteBackendPeer;
		ChangeListener cl = projectChangeListener;
		LogEntry realProvider = null;

		if (potentialProviders == null)
			throw new NoSuchLogEntryException("No Providers found!");

		for (LogEntry potentialProvider : potentialProviders) {
			log.debug("looking at possible provider "
					+ potentialProvider.getMember());
			if (potentialProvider == null
					|| potentialProvider.getMember() == null) {
				throw new IllegalStateException(
						"getPotentialJakeObjectProviders "
								+ "returned invalid Logentries");
			}
			remoteBackendPeer = this.icsManager.getBackendUserId(p,
					potentialProvider.getMember());
			log.debug("remoteBackendPeer: " + remoteBackendPeer);

			log.debug("getting transferService");
			ts = this.icsManager.getTransferService(p);

			String contentname = this.messageMarshaller.requestFile(
					fo.getProject(), potentialProvider);
			log.debug("content addressed with: " + contentname);
			fr = new FileRequest(contentname, false, remoteBackendPeer);

			try {
				// this also reports to the corresponding ChangeListener and
				// watches the FileTransfer and returns after the
				// FileTransfer has
				// either returned successfully or not successfully
				log.debug("requesting " + fr);
				result = AvailableLaterWaiter.await(new FileRequestFuture(fo,
						ts, fr, cl, getProjectsFileServices()));
				// Save potentialProvider for later usage.
				realProvider = potentialProvider;
				break;
			} catch (Exception ignored) {
				log.warn("pull from " + potentialProvider + " failed"
						+ ignored.getMessage());
				log.info("trying next provider");
				continue;
			}
		}

		if (realProvider == null) {
			log.debug("no provider found. ");
			return null;
		}

		log.debug("pull was tried.");

		// handle result
		// second part must be true after await returned
		if (result != null && result.isDone() && realProvider != null)
			this.setLogentryProcessed(fo, realProvider);

		return fo;
	}

	@Transactional
	public NoteObject pullNoteObject(Project p, LogEntry<JakeObject> le) {
		log.debug("pulling note out of log");
		NoteObject no = (NoteObject) le.getBelongsTo();

		no = db.getNoteObjectDao(no.getProject()).persist(no);
		this.setLogentryProcessed(no, le);
		return no;
	}

	/**
	 * returns true if NoteObject <br>
	 * returns false if FileObject
	 * 
	 * @param jo
	 * @return
	 */
	public boolean isNoteObject(JakeObject jo) {
		return jo instanceof NoteObject;
	}

	/**
	 * Sets a LogEntry and all previously happened LogEntries, that have the
	 * same 'belongsTo' to processed.
	 * 
	 * @param le
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	private void setLogentryProcessed(JakeObject jo,
			LogEntry<? extends JakeObject> le) {
		if (jo == null || le == null)
			return;

		try {
			db.getUnprocessedAwareLogEntryDao(jo).setProcessed(
					(LogEntry<JakeObject>) le);
		} catch (NoSuchLogEntryException e) {
			throw new IllegalArgumentException(e);
		}
		db.getUnprocessedAwareLogEntryDao(jo).setAllPreviousProcessed(le);
	}

	public void setApplicationContextFactory(
			ProjectApplicationContextFactory projectApplicationContextFactory) {
		this.db = projectApplicationContextFactory;
	}

	public RequestHandlePolicy getRequestHandlePolicy() {
		return this.rhp;
	}

	@Injected
	public void setRequestHandlePolicy(RequestHandlePolicy rhp) {
		this.rhp = rhp;
	}

	private boolean isReachable(User u) {
		ICService ics = model.getIcs();
		try {
			return ics.getStatusService().isLoggedIn(
					getICSManager().getBackendUserId(p, u));
		} catch (NoSuchUseridException e) {
			return false;
		} catch (NotLoggedInException e) {
			return false;
		} catch (TimeoutException e) {
			return false;
		} catch (NetworkException e) {
			return false;
		}
	}

	private String getMyUserid(Project p) {
		return p.getUserId().getUserId();
	}

	private IFileTransferService getTransferService(Project p)
			throws NotLoggedInException {
		return p.getMessageService().getIcsManager().getTransferService(p);
	}
}
