package com.jakeapp.core.synchronization;

import com.jakeapp.core.Injected;
import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.domain.logentries.JakeObjectNewVersionLogEntry;
import com.jakeapp.core.domain.logentries.JakeObjectDeleteLogEntry;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.services.ICSManager;
import com.jakeapp.core.services.IProjectsFileServices;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.synchronization.helpers.MessageMarshaller;
import com.jakeapp.core.util.AvailableLaterWaiter;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAFileException;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

/**
 * This class should be active whenever you want to use files <p/> On
 * Project->pause/start call
 * {@link #startServing(Project, ChangeListener)} and
 * {@link #stopServing(Project)} <p/> Even when you are offline, this is to be
 * used.
 *
 * @author johannes
 */
public class SyncServiceImpl extends FriendlySyncService implements IInternalSyncService {

	private static final Logger log = Logger.getLogger(SyncServiceImpl.class);


	private IProjectsFileServices projectsFileServices;


	/**
	 * key is the UUID
	 */
	private Map<String, ChangeListener> projectChangeListeners = new HashMap<String, ChangeListener>();

	/**
	 * key is the UUID
	 */
	private Map<String, ProjectRequestListener> projectRequestListeners = new HashMap<String, ProjectRequestListener>();

	private ChangeListener getProjectChangeListener(Project p) {
		return (p == null) ? null :
				projectChangeListeners.get(p.getProjectId());
	}

	@Injected
	private RequestHandlePolicy rhp;

	@Injected
	private ProjectApplicationContextFactory db;

	@Injected
	private ICSManager icsManager;

	private Map<String, Project> runningProjects = new HashMap<String, Project>();


	// if this component gets more complex,
	// inject it with spring
	@Injected
	private MessageMarshaller messageMarshaller;

	public MessageMarshaller getMessageMarshaller() {
		return messageMarshaller;
	}

	public void setMessageMarshaller(MessageMarshaller messageMarshaller) {
		this.messageMarshaller = messageMarshaller;
	}

	/* DAO stuff */

	@Override
	protected Iterable<UserId> getProjectMembers(Project project)
			throws NoSuchProjectException {
		return new LinkedList(db.getLogEntryDao(project).getCurrentProjectMembers());
	}


	public SyncServiceImpl() {
	}

	@Override
	@Transactional
	public void announce(JakeObject jo, LogAction action, String commitMsg)
			throws FileNotFoundException, InvalidFilenameException,
			NotAReadableFileException {
		NoteObject note = null;
		FileObject fo = null;
		LogEntry<JakeObject> le;

		IFSService fss = getFSS(jo.getProject());
		log.debug("announcing " + jo + " : " + action);

		jo = completeIncomingObjectSafe(jo);
		/*
		if (isNoteObject(jo)) {
			note = completeIncomingObjectOrNew((NoteObject) jo);
		} else {
			fo = completeIncomingObjectOrNew((FileObject) jo);
		}
		*/

		switch (action) {
			case JAKE_OBJECT_NEW_VERSION:
				le = new JakeObjectNewVersionLogEntry(jo, getMyProjectMember(jo
						.getProject()), commitMsg, null, true);
				break;

			case JAKE_OBJECT_DELETE:
				// what we do is always processed
				le = new JakeObjectDeleteLogEntry(jo, getMyProjectMember(jo
						.getProject()), commitMsg, null, true);
				break;
			default:
				throw new IllegalArgumentException("invalid logaction");
		}
		log.debug("prepared logentry");
		if (isNoteObject(jo)) {
			log.debug("storing note ...");
			note = (NoteObject) jo;
			db.getNoteObjectDao(jo.getProject()).persist(note);
			db.getLogEntryDao(jo).create(le);
			log.debug("storing note done.");
		} else {
			log.debug("getting file hash ....");
			fo = (FileObject) jo;
			if (action == LogAction.JAKE_OBJECT_NEW_VERSION)
				le.setChecksum(fss.calculateHashOverFile(fo.getRelPath()));
			log.debug("getting file hash done. storing ...");
			db.getFileObjectDao(fo.getProject()).persist(fo);
			db.getLogEntryDao(jo).create(le);
			log.debug("getting file hash done. storing done.");
		}
	}


	@Override
	@Transactional
	public LogEntry<JakeObject> getLock(JakeObject jo) throws IllegalArgumentException {
		return db.getLogEntryDao(jo).getLock(jo);
	}

	@Override
	public void poke(Project project, UserId pm) {
		log.info("Poking user " + pm.getUserId());
		ICService ics = getICS(project);
		log.debug("ICS is logged in? " + ics.getStatusService().isLoggedIn());
		com.jakeapp.jake.ics.UserId uid = getICSManager().getBackendUserId(project, pm);
		try {
			String message;
			message = messageMarshaller.pokeProject(project.getProjectId());
			log.debug("Sending message: \"" + message + "\"");
			ics.getMsgService().sendMessage(uid, message);
		} catch (NetworkException e) {
			log.debug("Could not poke user " + pm.getUserId(), e);
		} catch (OtherUserOfflineException e) {
			log.debug("Could not poke user " + pm.getUserId(), e);
		}
	}


	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public <T extends JakeObject> T pullObject(T jo) throws NoSuchLogEntryException,
			NotLoggedInException, IllegalArgumentException {

		LogEntry<JakeObject> le = db.getLogEntryDao(jo).getLastVersion(jo);
		log.debug("got logentry: " + le);
		if (le == null) { // delete
			log.debug("lets delete it");
			try {
				deleteBecauseRemoteSaidSo(jo);
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException(e);
			} catch (NoSuchJakeObjectException e) {
				throw new IllegalArgumentException(e);
			}
			return jo;
		} else if (isNoteObject(le.getBelongsTo()))
			return (T) pullNoteObject(jo.getProject(), le);
		else if (isFileObject(le.getBelongsTo()))
			return (T) pullFileObject(jo.getProject(), (FileObject) le.getBelongsTo());

		return jo; //TODO null? throw exception?

		//TODO call ChangeListener, PullWatcher, PullListener
	}


	@Override
	public Iterable<LogEntry<ILogable>> startLogSync(Project project, UserId pm)
			throws IllegalArgumentException, IllegalProtocolException {
		log.info("Requesting log sync from user " + pm.getUserId());

		ICService ics = getICS(project);
		com.jakeapp.jake.ics.UserId uid = getICSManager().getBackendUserId(project, pm);
		try {
			String msg = messageMarshaller.requestLogs(project.getProjectId());
			ics.getMsgService().sendMessage(uid, msg);
		} catch (NetworkException e) {
			log.debug("Could not request logs from user " + pm.getUserId(), e);
		} catch (OtherUserOfflineException e) {
			log.debug("Could not request logs from user " + pm.getUserId(), e);
		}

		// TODO: request log & fetch answer
		// TODO: make this an async operation (e.g. with an
		// AvailableLaterObject)


		return null;
	}


	@Transactional
	@Override
	public <T extends JakeObject> Attributed<T> getJakeObjectSyncStatus(T jo)
			throws InvalidFilenameException, NotAReadableFileException, IOException {
		if (isNoteObject(jo))
			return (Attributed<T>) getJakeObjectSyncStatus((NoteObject) jo);
		else
			return (Attributed<T>) getJakeObjectSyncStatus((FileObject) jo);
	}

	/**
	 * This is a expensive operation as it recalculates all hashes <br>
	 * Do it once on start, and then use a listener
	 */
	@Override
	@Transactional
	public List<NoteObject> getNotes(Project p) {
		return this.db.getNoteObjectDao(p).getAll();
	}

	@Override
	public File getFile(FileObject fo) throws IOException {
		IFSService fss = getFSS(fo.getProject());
		try {
			return new File(fss.getFullpath(fo.getRelPath()));
		} catch (InvalidFilenameException e) {
			log.fatal("db corrupted: contains invalid filenames");
			return null;
		}
	}

	@Override
	public void startServing(Project p, ChangeListener cl) throws ProjectException {
		log.debug("starting Project " + p);
		// this creates the ics


		ProjectRequestListener prl = projectRequestListeners.get(p.getProjectId());
		if (prl == null) {
			prl = new ProjectRequestListener(p, icsManager, db, (IInternalSyncService) this, messageMarshaller);
			projectRequestListeners.put(p.getProjectId(), prl);
		}
		try {
			p.getMessageService().activateSubsystem(getICS(p), prl, prl, prl, p.getProjectId());
		} catch (Exception e) {
			throw new ProjectException(e);
		}
		log.debug("Project " + p + " activated");

		//TODO activate icsManager.getITransferServiceic

		projectChangeListeners.put(p.getProjectId(), cl);
		runningProjects.put(p.getProjectId(), p);
	}


	@Override
	public void stopServing(Project p) {
		log.debug("stopping project " + p);

		runningProjects.remove(p.getProjectId());
		projectChangeListeners.remove(p.getProjectId());

		try {
			p.getMessageService().deactivateSubsystem(getICS(p));
		} catch (TimeoutException e) {
			log.debug("logout failed", e);
		} catch (NetworkException e) {
			log.debug("logout failed", e);
		}
	}


	@Override
	@Transactional
	public void getTags(JakeObject jo) {
		db.getLogEntryDao(jo).getCurrentTags(jo);
	}

	/**
	 * This is a expensive operation as it recalculates all hashes <br>
	 * Do it once on start, and then use a listener
	 */
	@Override
	@Transactional
	public AvailableLaterObject<List<FileObject>> getFiles(final Project p)
			throws IOException {
		log.debug("get files for project " + p);

		return new AvailableLaterObject<List<FileObject>>() {

			@Override
			public List<FileObject> calculate() throws Exception {
				IFSService fss = getFSS(p);

				List<String> files = fss.recursiveListFiles();

				//contains method should only check the relpath
				SortedSet<FileObject> sortedFiles = new TreeSet<FileObject>(FileObject.getRelpathComparator());

				for (FileObject fo : db.getFileObjectDao(p).getAll()) {
					sortedFiles.add(fo);
				}

				for (String relpath : files) {
					FileObject fo = new FileObject(p, relpath);
					if (!sortedFiles.contains(fo))
						sortedFiles.add(fo);
				}

				// TODO: add deleted (from logEntries)

				return new LinkedList<FileObject>(sortedFiles);
			}
		};
	}


	public Project getProjectById(String projectid) {
		// for(i : getProjectsManagingService().getProjectList()
		// return new Project(null, projectid, null, null);
		// TODO: mocked for the demo
		return this.runningProjects.get(projectid);
	}

	public ChangeListener getProjectChangeListener(String projectId) {
		return projectChangeListeners.get(projectId);
	}


	//////////// PUT ALL LOCAL HELPERS BEYOND THIS LINE


	public void setICSManager(ICSManager ics) {
		this.icsManager = ics;
	}

	public ICSManager getICSManager() {
		return icsManager;
	}


	/**
	 * Avoiding stub objects (without ID)
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	private JakeObject completeIncomingObjectSafe(JakeObject join) {
		if (isNoteObject(join)) {
			return this.completeIncomingObjectOrNew((NoteObject) join);
		} else {
			return this.completeIncomingObjectOrNew((FileObject) join);
		}
	}

	/**
	 * Avoiding stub objects (without ID)
	 *
	 * @param <T>
	 * @param join
	 * @return the FileObject or NoteObject from the Database
	 * @throws NoSuchJakeObjectException
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	private <T extends JakeObject> T completeIncomingObject(T join)
			throws IllegalArgumentException, NoSuchJakeObjectException {
		if (isNoteObject(join)) {
			return (T) db.getNoteObjectDao(join.getProject()).get(join.getUuid());
		} else {
			return (T) getFileObjectByRelpath(join.getProject(), ((FileObject) join)
					.getRelPath());
		}
	}

	private LogAction getLogActionNullSafe(LogEntry<? extends ILogable> lock) {
		if (lock == null)
			return null;
		else
			return lock.getLogAction();
	}




	@Transactional
	public Attributed<FileObject> getJakeObjectSyncStatus(FileObject foin)
			throws InvalidFilenameException, IOException {
		log.debug("get JakeObjectStatus for " + foin);
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
					checksumEqualToLastNewVersionLogEntry = pulledle.getChecksum()
							.equals(fss.calculateHashOverFile(fo.getRelPath()));
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
			//size = 0;
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

		return new Attributed<FileObject>(fo, lastle, locklog, objectExistsLocally,
				!checksumEqualToLastNewVersionLogEntry, hasUnprocessedLogEntries,
				lastProcessedLogAction, lastModificationDate, size);
	}

	@Transactional
	public Attributed<NoteObject> getJakeObjectSyncStatus(NoteObject noin) {
		Project p = noin.getProject();
		ILogEntryDao led = db.getUnprocessedAwareLogEntryDao(p);

		// this is very similar, but slightly different to the FileObject code
		// compare and edit them side-by-side

		// 0 complete + 1 exists?
		NoteObject no;
		boolean objectExistsLocally;
		try {
			no = completeIncomingObject(noin);
			objectExistsLocally = true;
		} catch (NoSuchJakeObjectException e) {
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
			checksumEqualToLastNewVersionLogEntry = pulledle.getBelongsTo().getContent()
					.equals(content);
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

		return new Attributed<NoteObject>(no, lastle, led.getLock(no),
				objectExistsLocally, !checksumEqualToLastNewVersionLogEntry,
				hasUnprocessedLogEntries, lastProcessedLogAction, lastModificationDate,
				size);
	}




	@Transactional
	private UserId getMyProjectMember(Project p) {
		return p.getUserId();
	}


	ICService getICS(Project p) {
		return getICSManager().getICService(p);
	}



	IFSService getFSS(Project p) {
		return this.getProjectsFileServices().getProjectFSService(p);
	}

	public IProjectsFileServices getProjectsFileServices() {
		return this.projectsFileServices;
	}

	public void setProjectsFileServices(IProjectsFileServices projectsFileServices) {
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
			return new FileObject(UUID.randomUUID(), jo.getProject(), jo.getRelPath());
		}
	}

	/**
	 * Pulls a Fileobject from any available source and - if the pull
	 * has been successful - sets all LogEntries that became obsoleted by the pull
	 * to processed.
	 *
	 * @param p  The Project the fileobject should belong to
	 * @param fo The File to pull
	 * @return fo
	 * @throws NotLoggedInException	 if there is not logged in MsgService for p
	 * @throws NoSuchLogEntryException  if there are no LogEntries for the JakeObject.
	 * @throws IllegalArgumentException if p or fo were null.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public FileObject pullFileObject(Project p, FileObject fo)
			throws NotLoggedInException, NoSuchLogEntryException, IllegalArgumentException {
		//preconditions-check
		if (p == null) throw new IllegalArgumentException("Project may not be null");
		if (fo == null) throw new IllegalArgumentException("FileObject may not be null");
		if (p.getMessageService() == null) throw new NotLoggedInException();

		IFileTransferService ts;
		IFileTransfer result = null;
		FileRequest fr;
		String relpath = fo.getRelPath();
		ICService ics = this.icsManager.getICService(p);
		Iterable<LogEntry> potentialProviders =
				this.getRequestHandlePolicy().getPotentialJakeObjectProviders(fo);
		IFileTransferService fts = p.getMessageService().getIcsManager().getTransferService(p);

		UserId remotePeer;
		com.jakeapp.jake.ics.UserId remoteBackendPeer;
		ChangeListener cl = this.getProjectChangeListener(p);
		LogEntry realProvider = null;

		if (potentialProviders == null) throw new NoSuchLogEntryException();

		for (LogEntry potentialProvider : potentialProviders)
			if (potentialProvider != null && potentialProvider.getMember() != null) {
				remoteBackendPeer =
						this.icsManager.getBackendUserId(p, potentialProvider.getMember());

				ts = icsManager.getTransferService(p);

				/*
				WRONG!
				ics.getTransferMethodFactory().getTransferMethod(
					ics.getMsgService(),
					remoteBackendPeer
				);
				*/

				//TODO write to tempfile rather than to relpath??
				fr = new FileRequest(relpath, false, remoteBackendPeer);

				try {
					//this also reports to the corresponding ChangeListener and
					//watches the Filetransfer and returns after the filetransfer has
					//either returned successfully or not successfully
					result =
							AvailableLaterWaiter.await(new FileRequestObject(fo, ts, fr, cl, db));
					//Save potentialProvider for later usage.
					realProvider = potentialProvider;
					break;
				}
				catch (Exception ignored) {
					//ignore Exception, continue with next potentialProvider
				}
			}

		//handle result
		if (result != null && result.isDone() && realProvider != null) //second part must be true after await returned
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

	@Transactional
	private void deleteBecauseRemoteSaidSo(JakeObject jo)
			throws IllegalArgumentException, NoSuchJakeObjectException,
			FileNotFoundException {
		if (jo instanceof NoteObject) {
			db.getNoteObjectDao(jo.getProject()).delete((NoteObject) jo);
		}
		if (jo instanceof FileObject) {
			db.getFileObjectDao(jo.getProject()).delete((FileObject) jo);
			try {
				getFSS(jo.getProject()).deleteFile(((FileObject) jo).getRelPath());
			} catch (NotAFileException e) {
				log.fatal("database corrupted: tried to delete a file that isn't a file",
						e);
			} catch (InvalidFilenameException e) {
				log.fatal("database corrupted: tried to delete a file that isn't "
						+ "a valid file", e);
			}
		}
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

	public boolean isFileObject(JakeObject jo) {
		return jo instanceof NoteObject;
	}

	/**
	 * Sets a LogEntry and all previously happened
	 * LogEntries, that have the same 'belongsTo' to processed.
	 *
	 * @param le
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	private void setLogentryProcessed(JakeObject jo, LogEntry<? extends JakeObject> le) {
		if (jo == null || le == null) return;

		//TODO there MUST be a better way to do this
		db.getUnprocessedAwareLogEntryDao(jo).setProcessed((LogEntry<JakeObject>) le);
		List<LogEntry<JakeObject>> versions = db.getLogEntryDao(jo).
				getAllVersionsOfJakeObject(jo);
		for (LogEntry<JakeObject> version : versions)
			if (!version.isProcessed() && version.getTimestamp().before(le.getTimestamp()))
				db.getUnprocessedAwareLogEntryDao(jo).setProcessed(version);
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




	// UNUSED STUFF COMES HERE.. PLEASE REMVOE AS SOON AS YOU KNOW WE DON'T NEED IT


	/**
	 * NullSave getter for {@link LogEntry#getMember()}
	 *
	 * @param lastle
	 * @return ProjectMember or null, if LogEntry is null.
	 */
	private UserId getProjectMemberNullSafe(LogEntry<? extends ILogable> lastle) {
		if (lastle != null)
			return lastle.getMember();
		else
			return null;
	}

	 	/**
	 * We keep track of the Project members
	 * <ul>
	 * <li>in the DB</li>
	 * <li>in log</li>
	 * <li>in the ics</li>
	 * </ul>
	 * We should only trust the logentries and fix everything else by that.
	 *
	 * @param project
	 */
	@Transactional
	private void syncProjectMembers(Project project) {
		Collection<UserId> members = db.getLogEntryDao(project)
				.getCurrentProjectMembers();
		for (UserId member : members) {
			// TODO!!
			com.jakeapp.jake.ics.UserId userid = new XmppUserId("foobar");
			// TODO
			try {
				getICS(project).getUsersService().addUser(userid, userid.getUserId());
			} catch (NoSuchUseridException e) { // shit happens
			} catch (NotLoggedInException e) {
			} catch (IOException e) {
			}
		}
	}


	private boolean isReachable(Project p, String userid) {
		ICService ics = getICS(p);
		if (ics == null)
			return false;
		try {
			return ics.getStatusService().isLoggedIn(new XmppUserId(userid));
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
