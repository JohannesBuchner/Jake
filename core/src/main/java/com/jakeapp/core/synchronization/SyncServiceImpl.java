package com.jakeapp.core.synchronization;

import com.jakeapp.core.Injected;
import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.services.ICSManager;
import com.jakeapp.core.services.IProjectsFileServices;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.synchronization.exceptions.PullFailedException;
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
import com.jakeapp.jake.ics.filetransfer.AdditionalFileTransferData;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.jake.ics.filetransfer.ITransferListener;
import com.jakeapp.jake.ics.filetransfer.TransferWatcherThread;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.status.ILoginStateListener;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;

/**
 * This class should be active whenever you want to use files <p/> On
 * Project->pause/start call
 * {@link #startServing(Project, ChangeListener)} and
 * {@link #stopServing(Project)} <p/> Even when you are offline, this is to be
 * used.
 *
 * @author johannes
 */
public class SyncServiceImpl extends FriendlySyncService {

	static final Logger log = Logger.getLogger(SyncServiceImpl.class);

	private static final String BEGIN_PROJECT_UUID = "<project>";

	private static final String END_PROJECT_UUID = "</project>";

	private static final String POKE_MESSAGE = "<poke/>";

	private static final String REQUEST_LOGS_MESSAGE = "<requestlogs/>";

	private static final String NEW_FILE = "<newfile/>";

	private static final String NEW_NOTE = "<newnote/>";

	private IProjectsFileServices projectsFileServices;

	/**
	 * key is the UUID
	 */
	private Map<String, ChangeListener> projectChangeListeners = new HashMap<String, ChangeListener>();
	
	private ChangeListener getProjectChangeListener(Project p) {
		return (p==null)?null:
			projectChangeListeners.get(p.getProjectId());
	}

	@Injected
	private RequestHandlePolicy rhp;

	@Injected
	private ProjectApplicationContextFactory db;

	private Map<String, Project> runningProjects = new HashMap<String, Project>();

	@Injected
	private ICSManager icsManager;

	ICService getICS(Project p) {
		return getICSManager().getICService(p);
	}
	private IFileTransferService getTransferService(Project p)
			throws NotLoggedInException {
		return p.getMessageService().getIcsManager().getTransferService(p);
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

	/* DAO stuff */

	/**
	 * returns true if NoteObject <br>
	 * returns false if FileObject
	 * @param jo
	 * @return
	 */
	public boolean isNoteObject(JakeObject jo) {
		return jo instanceof NoteObject;
	}
	
	public boolean isFileObject(JakeObject jo) {
		return jo instanceof NoteObject;
	}

	@Transactional
	private FileObject getFileObjectByRelpath(Project p, String relpath)
			throws NoSuchJakeObjectException {
		return db.getFileObjectDao(p).complete(new FileObject(p, relpath));
	}

	@Override
	protected Iterable<UserId> getProjectMembers(Project project)
			throws NoSuchProjectException {
		return new LinkedList(db.getLogEntryDao(project).getCurrentProjectMembers());
	}

	private static String getUUIDStringForProject(Project project) {
		return BEGIN_PROJECT_UUID + project.getProjectId() + END_PROJECT_UUID;
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

	@Transactional
	private UserId getMyProjectMember(Project p) {
		return p.getUserId();
	}

	public SyncServiceImpl() {
	}

	@Override
	@Transactional
	public void announce(JakeObject jo, LogAction action, String commitMsg)
			throws FileNotFoundException, InvalidFilenameException,
			NotAReadableFileException {
		NoteObject note=null;
		FileObject fo=null;
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
			note = (NoteObject)jo;
			db.getNoteObjectDao(jo.getProject()).persist(note);
			db.getLogEntryDao(jo).create(le);
			log.debug("storing note done.");
		} else {
			log.debug("getting file hash ....");
			fo = (FileObject)jo;
			if (action == LogAction.JAKE_OBJECT_NEW_VERSION)
				le.setChecksum(fss.calculateHashOverFile(fo.getRelPath()));
			log.debug("getting file hash done. storing ...");
			db.getFileObjectDao(fo.getProject()).persist(fo);
			db.getLogEntryDao(jo).create(le);
			log.debug("getting file hash done. storing done.");
		}
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
			return new FileObject(UUID.randomUUID(),jo.getProject(), jo.getRelPath());
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
			String message = getUUIDStringForProject(project) + POKE_MESSAGE;
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
		}
		else if (isNoteObject(le.getBelongsTo()))
			return (T) pullNoteObject(jo.getProject(), le);
		else if (isFileObject(le.getBelongsTo()))
			return (T) pullFileObject(jo.getProject(), (FileObject)le.getBelongsTo());
		
		return jo; //TODO null? throw exception?
		
		//TODO call ChangeListener, PullWatcher, PullListener
	}
	
	/**
	 * Sets a LogEntry and all previously happened
	 * LogEntries, that have the same 'belongsTo' to processed.
	 * @param le
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	private void setLogentryProcessed(JakeObject jo,LogEntry<? extends JakeObject> le) {
		if (jo==null || le==null) return;
		
		//TODO there MUST be a better way to do this
		db.getUnprocessedAwareLogEntryDao(jo).setProcessed((LogEntry<JakeObject>) le);
		List<LogEntry<JakeObject>> versions = db.getLogEntryDao(jo).
			getAllVersionsOfJakeObject(jo);
		for (LogEntry<JakeObject> version : versions)
			if (!version.isProcessed() && version.getTimestamp().before(le.getTimestamp()))
				db.getUnprocessedAwareLogEntryDao(jo).setProcessed(version);
	}
	
	protected class FileRequestObject extends AvailableLaterObject<IFileTransfer> implements INegotiationSuccessListener {
		private IFileTransferService ts;
		private FileRequest request;
		private Semaphore sem;
		private ChangeListener cl;
		private JakeObject jo;
		private Throwable innerException;
		
		protected class FileProgressChangeListener extends ChangeListenerWrapper {
			public FileProgressChangeListener(ChangeListener cl) {
				super(cl);
			}
			
			@Override
			public void pullDone(JakeObject jo) {
				sem.release();
			}
			
			@Override
			public void pullFailed(JakeObject jo, Exception reason) {
				sem.release();
				innerException = reason;
			}
		}
		
		public FileRequestObject(JakeObject jo,IFileTransferService ts, FileRequest request, ChangeListener cl) {
			super();
			this.ts = ts;
			this.request = request;
			this.cl = new FileProgressChangeListener(cl);
			this.jo = jo;
			sem = new Semaphore(0);
			innerException = null;
		}

		@Override
		public IFileTransfer calculate() throws Exception {
			INegotiationSuccessListener listener = new PullListener(this.jo, cl);
			ts.request(request, this);
			//wait for negotiation-success-listener
			sem.acquire();
			
			if (this.innerException!=null && innerException instanceof Exception) {
				listener.failed(this.innerException);
				throw (Exception)innerException;
			}
			
			listener.succeeded(innercontent);
			
			//wait for FileProressChangeListener
			sem.acquire();
			if (this.innerException!=null && innerException instanceof Exception) {
				throw (Exception)innerException;
				//this exception came from the listener!
			}
			
			return this.innercontent;
		}

		@Override
		public void failed(Throwable reason) {
			this.innerException = reason;
			sem.release();
		}

		@Override
		public void succeeded(IFileTransfer ft) {
			this.innercontent = ft;
			sem.release();
		}
	}
	
	
	/** 
	 * Pulls a Fileobject from any available source and - if the pull
	 * has been successful - sets all LogEntries that became obsoleted by the pull
	 * to processed.
	 * @param p The Project the fileobject should belong to
	 * @param fo The File to pull
	 * @return fo
	 * @throws NotLoggedInException if there is not logged in MsgService for p
	 * @throws NoSuchLogEntryException if there are no LogEntries for the JakeObject.
	 * @throws IllegalArgumentException if p or fo were null.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public FileObject pullFileObject(Project p, FileObject fo)
			throws NotLoggedInException, NoSuchLogEntryException, IllegalArgumentException {
		//preconditions-check
		if (p==null) throw new IllegalArgumentException("Project may not be null");
		if (fo==null) throw new IllegalArgumentException("FileObject may not be null");
		if (p.getMessageService()==null) throw new NotLoggedInException();
		
		IFileTransferService ts;
		IFileTransfer result = null;
		FileRequest fr;
		String relpath = fo.getRelPath();
		ICService ics = this.icsManager.getICService(p);
		Iterable<LogEntry> potentialProviders =
			this.getRequestHandlePolicy().getPotentialJakeObjectProviders(fo);
		IFileTransferService fts = this.getTransferService(p);
		UserId remotePeer;
		com.jakeapp.jake.ics.UserId remoteBackendPeer;
		ChangeListener cl = this.getProjectChangeListener(p);
		LogEntry realProvider = null;
		
		if (potentialProviders == null) throw new NoSuchLogEntryException();
		
		for (LogEntry potentialProvider : potentialProviders)
			if (potentialProvider!=null && potentialProvider.getMember()!=null)
			{
				remoteBackendPeer = 
					this.icsManager.getBackendUserId(p,potentialProvider.getMember());
				
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
						AvailableLaterWaiter.await(new FileRequestObject(fo,ts,fr,cl));
					//Save potentialProvider for later usage.
					realProvider = potentialProvider;
					break;
				}
				catch (Exception ignored) {
					//ignore Exception, continue with next potentialProvider
				}
			}
		
		//handle result
		if (result!=null && result.isDone() && realProvider!=null) //second part must be true after await returned
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

	@Override
	public Iterable<LogEntry<ILogable>> startLogSync(Project project, UserId pm)
			throws IllegalArgumentException, IllegalProtocolException {
		log.info("Requesting log sync from user " + pm.getUserId());

		ICService ics = getICS(project);
		com.jakeapp.jake.ics.UserId uid = getICSManager().getBackendUserId(project, pm);
		try {
			ics.getMsgService().sendMessage(uid, getUUIDStringForProject(project) + REQUEST_LOGS_MESSAGE);
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
	
	/**
	 * Avoiding stub objects (without ID)
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	private JakeObject completeIncomingObjectSafe(JakeObject join) {
		if (isNoteObject(join)) {
			return this.completeIncomingObjectOrNew((NoteObject)join);
		} else {
			return this.completeIncomingObjectOrNew((FileObject)join);
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


	@Transactional
	public Attributed<FileObject> getJakeObjectSyncStatus(FileObject foin)
			throws InvalidFilenameException, IOException {
		log.debug("get JakeObjectStatus for "+ foin);
		if(foin == null) {
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
	public List<Attributed<NoteObject>> getNotes(Project p) {
		List<Attributed<NoteObject>> stat = new LinkedList<Attributed<NoteObject>>();

		// TODO: add deleted
		for (NoteObject no : this.db.getNoteObjectDao(p).getAll()) {
			stat.add(getJakeObjectSyncStatus(no));
		}
		return stat;
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
		ProjectRequestListener prl = new ProjectRequestListener(p);
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

	private class ProjectRequestListener implements IMessageReceiveListener,
			IOnlineStatusListener, ILoginStateListener {

		private Project p;

		public ProjectRequestListener(Project p) {
			this.p = p;
		}

		private String getProjectUUID(String content) {
			int begin = content.indexOf(BEGIN_PROJECT_UUID) + BEGIN_PROJECT_UUID.length();
			int end = content.indexOf(END_PROJECT_UUID);

			return content.substring(begin, end);
		}

		@Override
		@Transactional
		public void receivedMessage(com.jakeapp.jake.ics.UserId from_userid, String content) {
			String projectUUID = getProjectUUID(content);
			log.debug("Received a message for project " + projectUUID);

			if(!projectUUID.equals(p.getProjectId())) {
				log.debug("Discarding message because it's not for this project");
				return;
			}

			log.debug("Message is for this project!");

			String message = content.substring(BEGIN_PROJECT_UUID.length() + projectUUID.length() + END_PROJECT_UUID.length());
			log.debug("Message content: \"" + message + "\"");

			if (message.startsWith(POKE_MESSAGE)) {
				log.info("Received poke from " + from_userid.getUserId());
				log.debug("This means we should sync logs!");

				// Eventually, this should consider things such as trust
				UserId user = getICSManager().getFrontendUserId(p, from_userid);
				try {
					SyncServiceImpl.this.startLogSync(p, user);
				} catch (IllegalProtocolException e) {
					// This should neeeeeeeeever happen
					log.fatal("Received an unexpected IllegalProtocolException while trying to perform logsync",
									e);
				}
				return;
			}

			if (message.startsWith(REQUEST_LOGS_MESSAGE)) {
				log.info("Received logs request from " + from_userid.getUserId());

				sendLogs(p, from_userid);

				// TODO: Send our logs to this user

				return;
			}

			// TODO: The stuff below here could use some refactoring 
			// (e.g. redeclaring parameter content)
			int uuidlen = UUID.randomUUID().toString().length();
			String projectid = message.substring(0, uuidlen);
			message = message.substring(uuidlen);
			Project p = getProjectByUserId(projectid);
			ChangeListener cl = projectChangeListeners.get(projectid);
			if (message.startsWith(NEW_NOTE)) {
				log.debug("requesting note");
				UUID uuid = UUID.fromString(message.substring(NEW_NOTE.length()));
				log.debug("persisting object");
				NoteObject no = new NoteObject(uuid, p, "loading ...");
				db.getNoteObjectDao(p).persist(no);
				log.debug("calling other user: " + from_userid);
				try {
					getTransferService(p).request(
							new FileRequest("N" + uuid, false, from_userid),
							cl.beganRequest(no));
				} catch (NotLoggedInException e) {
					log.error("Not logged in");
				}
			}
			if (message.startsWith(NEW_FILE)) {
				log.debug("requesting file");
				String relpath = message.substring(NEW_FILE.length());
				FileObject fo = new FileObject(p, relpath);
				log.debug("persisting object");
				db.getFileObjectDao(p).persist(fo);
				log.debug("calling other user: " + from_userid);
				try {
					getTransferService(p).request(
							new FileRequest("F" + relpath, false, from_userid),
							cl.beganRequest(fo));
				} catch (NotLoggedInException e) {
					log.error("Not logged in");
				}
			}
		}

		@Override
		public void onlineStatusChanged(com.jakeapp.jake.ics.UserId userid) {
			// TODO Auto-generated method stub
			log.info("Online status of " + userid.getUserId() + " changed...");
		}

		@Override
		public void loginHappened() {
			// TODO Auto-generated method stub
			log.info("We logged in!");
		}

		@Override
		public void logoutHappened() {
			// TODO Auto-generated method stub
		   log.info("We logged out!");
		}
	}

	private void sendLogs(Project project, com.jakeapp.jake.ics.UserId user) {
		ICService ics = getICS(project);

		try {
			List<LogEntry<? extends ILogable>> logs = db.getLogEntryDao(project).getAll();

			LogEntrySerializer robot = new LogEntrySerializer();

			System.err.println("========= LOGS ==========");
			for(LogEntry<? extends ILogable> l: logs) {
				System.err.println("--- START");
				System.err.println(robot.serialize(l, project));
				System.err.println("--- END");
			}
			System.err.println("========= /LOGS =========");

			ics.getMsgService().sendMessage(user, "THIS IS A LOG SYNC! SERIOUSLY!");
		} catch (NetworkException e) {
			log.warn("Could not sync logs", e);
		} catch (OtherUserOfflineException e) {
			log.warn("Could not sync logs", e);
		}
	}

	private class PullListener implements INegotiationSuccessListener {

		private ChangeListener cl;

		private JakeObject jo;

		private PullListener(JakeObject jo, ChangeListener cl) {
			this.cl = cl;
			this.jo = jo;
		}

		@Override
		public void failed(Throwable reason) {
			log.error("pulling failed.");
		}

		@Override
		public void succeeded(IFileTransfer ft) {
			log.info("pulling negotiation succeeded");
			cl.pullNegotiationDone(jo);
			new TransferWatcherThread(ft, new PullWatcher(jo, cl, ft));
		}

	}

	private class PullWatcher implements ITransferListener {

		private ChangeListener cl;

		@SuppressWarnings("unused")
		private IFileTransfer ft;

		private JakeObject jo;

		public PullWatcher(JakeObject jo, ChangeListener cl, IFileTransfer ft) {
			this.cl = cl;
			this.ft = ft;
			this.jo = jo;
		}

		@Override
		public void onFailure(AdditionalFileTransferData transfer, String error) {
			cl.pullFailed(jo, new PullFailedException(error));
			log.error("transfer for " + jo + " failed: " + error);
		}

		@Override
		@Transactional
		public void onSuccess(AdditionalFileTransferData transfer) {
			log.info("transfer for " + jo + " succeeded");
			FileInputStream data;
			try {
				data = new FileInputStream(transfer.getDataFile());
			} catch (FileNotFoundException e2) {
				log.error("opening file failed:", e2);
				return;
			}
			if (jo instanceof NoteObject) {
				NoteObject no;
				try {
					no = db.getNoteObjectDao(jo.getProject()).get(jo.getUuid());
				} catch (Exception e1) {
					log.error("404", e1);
					return;
				}

				BufferedReader bis = new BufferedReader(new InputStreamReader(data));
				String content;
				try {
					content = bis.readLine(); // TODO: read whole thing
					bis.close();
				} catch (IOException e) {
					content = "foo";
				}
				no.setContent(content);
				cl.pullDone(jo);
			}
			if (jo instanceof FileObject) {
				String target = ((FileObject) jo).getRelPath();
				try {
					getFSS(jo.getProject()).writeFileStream(target, data);
				} catch (Exception e) {
					log.error("writing file failed:", e);
					return;
				}
				cl.pullDone(jo);
			}
			try {
				data.close();
			} catch (IOException ignored) {
				log.debug("We don't care 'bout this exception");
			}
		}

		@Override
		public void onUpdate(AdditionalFileTransferData transfer, Status status,
		                     double progress) {
			log.info("progress for " + jo + " : " + status + " - " + progress);
			cl.pullProgressUpdate(jo, status, progress);
		}

	}

	@Override
	public void stopServing(Project p) {
		runningProjects.remove(p.getProjectId());
		projectChangeListeners.remove(p.getProjectId());

		try {
			getICS(p).getStatusService().logout();
		} catch (TimeoutException e) {
			log.debug("logout failed", e);
		} catch (NetworkException e) {
			log.debug("logout failed", e);
		}
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

	private Project getProjectByUserId(String projectid) {
		// for(i : getProjectsManagingService().getProjectList()
		// return new Project(null, projectid, null, null);
		// TODO: mocked for the demo
		return this.runningProjects.get(projectid);
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

				List<FileObject> fileObjects = new LinkedList<FileObject>();

				// TODO: add deleted (from logEntries)
				for (FileObject fo : db.getFileObjectDao(p).getAll()) {
					fileObjects.add(fo);
				}

				for (String relpath : files) {
					FileObject fo = new FileObject(p, relpath);
					fileObjects.add(fo);
				}
				return fileObjects;
			}
		};
	}

	public void setICSManager(ICSManager ics) {
		this.icsManager = ics;
	}

	public ICSManager getICSManager() {
		return icsManager;
	}
}
