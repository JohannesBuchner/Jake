package com.jakeapp.core.synchronization;

import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.dao.exceptions.NoSuchUserException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.services.IProjectsFileServices;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAFileException;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
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

/**
 * This class should be active whenever you want to use files <p/> On
 * Project->pause/start call
 * {@link #startServing(Project, RequestHandlePolicy, ChangeListener)} and
 * {@link #stopServing(Project)} <p/> Even when you are offline, this is to be
 * used.
 * 
 * @author johannes
 */
public class SyncServiceImpl extends FriendlySyncService implements
		IMessageReceiveListener {

	static final Logger log = Logger.getLogger(SyncServiceImpl.class);

	private static final String POKE_MESSAGE = "<poke/>";

	private static final String NEW_FILE = "<newfile/>";

	private static final String NEW_NOTE = "<newnote/>";

	private IProjectsFileServices projectsFileServices;


	/**
	 * key is the UUID
	 */
	private Map<String, ChangeListener> projectChangeListeners = new HashMap<String, ChangeListener>();

	private RequestHandlePolicy rhp;

	private ProjectApplicationContextFactory db;

	private Map<String, Project> runningProjects = new HashMap<String, Project>();

	ICService getICS(Project p) {
		return p.getMessageService().getIcsManager().getICService(p);
	}

	IFSService getFSS(Project p) {
		return this.getProjectsFileServices().startProject(p);
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
	 */
	public Boolean isNoteObject(JakeObject jo) {
		return jo instanceof NoteObject;
	}

	@Transactional
	private FileObject getFileObjectByRelpath(Project p, String relpath)
			throws NoSuchJakeObjectException {
		return db.getFileObjectDao(p).complete(new FileObject(p, relpath));
	}

	@Override
	protected List<UserId> getProjectMembers(Project project)
			throws NoSuchProjectException {
		return new LinkedList(db.getLogEntryDao(project).getCurrentProjectMembers());
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
			com.jakeapp.jake.ics.UserId userid = getBackendUserIdFromDomainProjectMember(
					project, member);
			try {
				getICS(project).getUsersService().addUser(userid, userid.getUserId());
			} catch (NoSuchUseridException e) { // shit happens
			} catch (NotLoggedInException e) {
			} catch (IOException e) {
			}
			// TODO: fixup ProjectMember table.
			// if(db.getProjectMemberDao(project).getAll(project))
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
		IFSService fss = getFSS(jo.getProject());
		log.debug("announcing " + jo + " : " + action);

		LogEntry<JakeObject> le;
		switch (action) {
			case JAKE_OBJECT_NEW_VERSION:
			case JAKE_OBJECT_DELETE:
				// what we do is always processed
				le = new JakeObjectLogEntry(action, jo,
						getMyProjectMember(jo.getProject()), commitMsg, null, true);
				break;
			default:
				throw new IllegalArgumentException("invalid logaction");
		}
		log.debug("prepared logentry");
		if (isNoteObject(jo)) {
			log.debug("storing note ...");
			NoteObject note;
			note = completeIncomingObjectOrNew((NoteObject) jo);
			db.getNoteObjectDao(jo.getProject()).persist(note);
			db.getLogEntryDao(jo).create(le);
			log.debug("storing note done.");
		} else {
			log.debug("getting file hash ....");
			FileObject fo;
			fo = completeIncomingObjectOrNew((FileObject) jo);
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
			return new FileObject(jo.getProject(), jo.getRelPath());
		}
	}

	@Override
	@Transactional
	public LogEntry<JakeObject> getLock(JakeObject jo) throws IllegalArgumentException {
		return db.getLogEntryDao(jo).getLock(jo);
	}

	@Override
	public void poke(Project project, UserId pm) {
		// TODO
	}


	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public <T extends JakeObject> T pullObject(T jo) throws NoSuchLogEntryException,
			NotLoggedInException, IllegalArgumentException {
		LogEntry<JakeObject> le = db.getLogEntryDao(jo).getLastVersion(jo);
		log.debug("got logentry: " + le);
		this.getRequestHandelPolicy().getPotentialJakeObjectProviders(jo);
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
		if (isNoteObject(le.getBelongsTo()))
			return (T) pullNoteObject(jo.getProject(), le);
		else
			return (T) pullFileObject(jo.getProject(), le);
	}

	@Transactional
	public FileObject pullFileObject(Project p, LogEntry<JakeObject> le)
			throws NotLoggedInException {
		FileObject fo = (FileObject) le.getBelongsTo();
		String relpath = fo.getRelPath();
		//
		//FailoverCapableFileTransferService ts = getTransferService(p);
		//log.debug("pulling file from $randomguy");
		throw new IllegalStateException("pulling files not implemented yet");
		// ts.request(jo.ge, nsl);
		// TODO: getPotentialProviders
		// if(le.getUser().equals(userid))
		// throw new com.jakeapp.core.dao.exceptions.NoSuchLogEntryException();

		// if(!isLoggedIn(userid))
		// throw new
		// com.jakeapp.jake.ics.exceptions.OtherUserOfflineException();
		// TODO: fetch
		// return fo;
	}

	@Transactional
	public NoteObject pullNoteObject(Project p, LogEntry<JakeObject> le) {
		log.debug("pulling note out of log");
		NoteObject no = (NoteObject) le.getBelongsTo();
		return db.getNoteObjectDao(no.getProject()).persist(no);
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
		// TODO Auto-generated method stub
		// TODO: request log & fetch answer
		// TODO: make this an async operation (e.g. with an
		// AvailableLaterObject)


		return null;
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
		long size;
		LogEntry<FileObject> pulledle;
		try {
			pulledle = led.getLastVersionOfJakeObject(fo, false);
			if (objectExistsLocally) {
				try {
					size = fss.getFileSize(fo.getRelPath());
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
			}
		} catch (NoSuchLogEntryException e1) {
			size = 0;
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

		return new Attributed<FileObject>(fo, lastle,
				locklog,
				objectExistsLocally, !checksumEqualToLastNewVersionLogEntry,
				hasUnprocessedLogEntries, lastProcessedLogAction, lastModificationDate,
				size);
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

		return new Attributed<NoteObject>(no, lastle,
				led.getLock(no),
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
	public void startServing(Project p, RequestHandlePolicy rhp, ChangeListener cl)
			throws ProjectException {

		setRequestHandelPolicy(rhp);
		runningProjects.put(p.getProjectId(), p);
		// projectsFssMap.put(p.getProjectId(), fs);
		projectChangeListeners.put(p.getProjectId(), cl);
		// this creates the ics
		log.debug("adding receive hooks");
		try {
			p.getMessageService().loginICS(getICS(p));
		} catch (TimeoutException e) {
			log.error("logging in for starting project failed", e);
		} catch (NetworkException e) {
			log.error("logging in for starting project failed", e);
		}
		getICS(p).getMsgService().registerReceiveMessageListener(this);
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
			} catch (IOException e) {
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
		// TODO Auto-generated method stub
		// TODO: remove ics hooks
		try {
			getICS(p).getStatusService().logout();
		} catch (TimeoutException e) {
			log.debug("logout failed", e);
		} catch (NetworkException e) {
			log.debug("logout failed", e);
		}
	}


	public void setApplicationContextFactory(ProjectApplicationContextFactory projectApplicationContextFactory) {
		this.db = projectApplicationContextFactory;
	}


	public RequestHandlePolicy getRequestHandelPolicy() {
		if (this.rhp == null) {
			this.rhp = new TrustAllRequestHandlePolicy(db, projectsFileServices);
		}
		return this.rhp;
	}


	public void setRequestHandelPolicy(RequestHandlePolicy rhp) {
		this.rhp = rhp;
	}

	public com.jakeapp.jake.ics.UserId getBackendUserIdFromDomainProjectMember(Project p,
			UserId member) {
		return null; // TODO
	}

	public com.jakeapp.jake.ics.UserId getBackendUserIdFromDomainUserId(UserId userid) {
		return null; // TODO
	}

	public UserId getProjectMemberFromUserId(Project project, UserId userid)
			throws NoSuchUserException {
		return null; // TODO
	}

	public UserId getUserIdFromProjectMember(Project project, UserId member) {
		return null; // TODO
	}

	@Override
	public void invite(Project project, UserId userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyInvitationAccepted(Project project, UserId inviter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyInvitationRejected(Project project, UserId inviter) {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public void receivedMessage(com.jakeapp.jake.ics.UserId from_userid, String content) {
		int uuidlen = UUID.randomUUID().toString().length();
		String projectid = content.substring(0, uuidlen);
		content = content.substring(uuidlen);
		Project p = getProjectByUserId(projectid);
		ChangeListener cl = projectChangeListeners.get(projectid);
		if (content.startsWith(NEW_NOTE)) {
			log.debug("requesting note");
			UUID uuid = UUID.fromString(content.substring(NEW_NOTE.length()));
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
		if (content.startsWith(NEW_FILE)) {
			log.debug("requesting file");
			String relpath = content.substring(NEW_FILE.length());
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

	private IFileTransferService getTransferService(Project p) throws NotLoggedInException {
		return p.getMessageService().getIcsManager().getTransferService(p);
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
	public List<FileObject> getFiles(Project p) throws IOException {
		log.debug("get files for project " + p);
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
}