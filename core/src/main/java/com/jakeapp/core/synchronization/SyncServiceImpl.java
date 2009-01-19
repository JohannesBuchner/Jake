package com.jakeapp.core.synchronization;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hsqldb.lib.StringInputStream;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.FileObjectLogEntry;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.NoteObjectLogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.services.ICServicesManager;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.util.ApplicationContextFactory;
import com.jakeapp.jake.fss.FSService;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.CreatingSubDirectoriesFailedException;
import com.jakeapp.jake.fss.exceptions.FileTooLargeException;
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
import com.jakeapp.jake.ics.filetransfer.FailoverCapableFileTransferService;
import com.jakeapp.jake.ics.filetransfer.ITransferListener;
import com.jakeapp.jake.ics.filetransfer.TransferWatcherThread;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;
import com.jakeapp.jake.ics.status.IStatusService;
import com.jakeapp.jake.ics.users.IUsersService;

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

	/**
	 * key is the UUID
	 */
	private Map<String, IFSService> projectsFssMap;

	/**
	 * key is the UUID
	 */
	private Map<String, ChangeListener> projectChangeListeners;

	private RequestHandlePolicy rhp;

	ApplicationContextFactory db;

	private ICServicesManager icServicesManager;

	private UserTranslator userTranslator;

	private Map<String, Project> runningProjects = new HashMap<String, Project>();

	/* for the demo, to be removed and replaced by delegate methods */
	private List<JakeObject> news = new LinkedList<JakeObject>();

	ICService getICS(Project p) {
		return icServicesManager.getICService(p);
	}

	IFSService getFSS(Project p) {
		return projectsFssMap.get(p.getProjectId());
	}

	public ICServicesManager getIcServicesManager() {
		return icServicesManager;
	}

	public void setIcServicesManager(ICServicesManager icServicesManager) {
		this.icServicesManager = icServicesManager;
	}

	/* DAO stuff */

	/**
	 * returns true if NoteObject <br>
	 * returns false if FileObject
	 */
	Boolean isNoteObject(JakeObject jo) {
		return jo instanceof NoteObject;
	}

	@Transactional
	private LogEntry<JakeObject> getMostRecentForLogEntry(JakeObject jo)
			throws NoSuchLogEntryException {
		return (LogEntry<JakeObject>) db.getLogEntryDao(jo).getMostRecentFor(jo);
	}

	@Transactional
	private FileObject getFileObjectByRelpath(Project p, String relpath)
			throws NoSuchJakeObjectException {
		return db.getFileObjectDao(p).complete(new FileObject(null, p, relpath));
	}

	@Override
	protected Iterable<UserId> getProjectMembers(Project project) {
		syncProjectMembers(project);
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
		Collection<ProjectMember> members = db.getLogEntryDao(project)
				.getCurrentProjectMembers();
		for (ProjectMember member : members) {
			com.jakeapp.jake.ics.UserId userid = getBackendUserIdFromDomainProjectMember(
					project, member);
			try {
				getICS(project).getUsersService().addUser(userid, member.getNickname());
			} catch (NoSuchUseridException e) { // shit happens
			} catch (NotLoggedInException e) {
			} catch (IOException e) {
			}
		}
	}

	@Transactional
	private LogEntry<JakeObject> getLogEntryOfLocal(JakeObject jo) {
		return (LogEntry<JakeObject>) db.getLogEntryDao(jo).findLastMatching(
				new LogEntry<ILogable>(null, LogAction.JAKE_OBJECT_NEW_VERSION, null,
						null, null, null, null, null, true));
	}

	@Override
	@Transactional
	public boolean localIsNewest(JakeObject jo) {
		LogEntry<JakeObject> localLe = getLogEntryOfLocal(jo);
		if (localLe == null) {
			return false;
		}
		LogEntry<JakeObject> newestLe;
		try {
			newestLe = db.getLogEntryDao(jo).getMostRecentFor(jo);
		} catch (NoSuchLogEntryException e) {
			return true; // not announced yet
		}
		if (localLe.getUuid().equals(newestLe.getUuid()))
			return true;
		else
			return false;
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

	private com.jakeapp.jake.ics.UserId getMyBackendUserid(Project p) {
		return userTranslator.getBackendUserIdFromDomainUserId(p.getUserId());
	}

	@Transactional
	private ProjectMember getMyProjectMember(Project p) {
		try {
			return db.getProjectMemberDao(p).get(p.getUserId().getUuid());
		} catch (NoSuchProjectMemberException e) {
			log.fatal("can't find myself in project", e);
			return null;
		}
	}

	public SyncServiceImpl() {
	}

	@Override
	@Transactional
	public void announce(JakeObject jo, LogEntry<JakeObject> inaction, String commitMsg)
			throws FileNotFoundException, InvalidFilenameException,
			NotAReadableFileException {
		log.debug("announcing " + jo + " : " + inaction);
		IFSService fss = getFSS(jo.getProject());
		LogEntry<JakeObject> le = new LogEntry<JakeObject>(UUID.randomUUID(), inaction
				.getLogAction());
		LogAction action = inaction.getLogAction();
		// set those that shouldn't be set by caller
		le.setBelongsTo(jo);
		le.setTimestamp(new Date());
		le.setComment(commitMsg);
		le.setMember(getMyProjectMember(jo.getProject()));
		log.debug("prepared logentry");

		if (!(action == LogAction.JAKE_OBJECT_NEW_VERSION
				|| action == LogAction.JAKE_OBJECT_DELETE || action == LogAction.TAG_ADD
				|| action == LogAction.TAG_REMOVE || action == LogAction.JAKE_OBJECT_LOCK || action == LogAction.JAKE_OBJECT_UNLOCK)) {
			throw new IllegalArgumentException(
					"announce can not be used with this action");
		}
		if (isNoteObject(jo)) {
			log.debug("is a note. storing.");
			NoteObject note = (NoteObject) jo;
			db.getLogEntryDao(jo).create(new NoteObjectLogEntry(le));
			log.debug("is a note. done.");
		} else {
			log.debug("is a file. getting hash.");
			FileObject fo = (FileObject) jo;
			if (action == LogAction.JAKE_OBJECT_NEW_VERSION)
				le.setChecksum(fss.calculateHashOverFile(fo.getRelPath()));
			log.debug("is a file. getting hash done.");
			db.getLogEntryDao(jo).create(new FileObjectLogEntry(le));
			log.debug("is a file. log entry written.");
		}
		news.add(jo);
	}

	@Override
	public Iterable<FileObject> getPullableFileObjects(Project project) {
		List<FileObject> missing = new LinkedList<FileObject>();
		Iterable<FileObject> allJakeObjects = db.getLogEntryDao(project)
				.getExistingFileObjects(project);
		for (JakeObject jo : allJakeObjects) {
			if (!isNoteObject(jo)) {
				FileObject fo = (FileObject) jo;
				if (localIsNewest(fo))
					missing.add(fo);
			}
		}
		return missing;
	}

	@Override
	public Iterable<FileObject> getFileObjectsInConflict(Project project)
			throws IllegalArgumentException {
		List<FileObject> conflicting = new LinkedList<FileObject>();
		// find existing locally
		// find existing&modified locally that have a unprocessed NEW_VERSION
		Iterable<FileObject> pullableFO = getPullableFileObjects(project);
		for (FileObject jo : pullableFO) {
			if (!isNoteObject(jo)) {
				FileObject fo = (FileObject) jo;
				try {
					if (isLocallyModified(fo))
						conflicting.add(jo);
				} catch (InvalidFilenameException e) {
					log.fatal("Database corrupted", e);
				} catch (IOException e) {
					log.fatal("IO error", e);
				}
			}
		}
		return conflicting;
	}

	@Override
	@Transactional
	public LogEntry<JakeObject> getLock(JakeObject jo) throws IllegalArgumentException {
		return db.getLogEntryDao(jo).getLock(jo);
	}

	@Override
	public void poke(Project project, UserId userId) {
		try {
			for (JakeObject jo : this.news) {
				if (isNoteObject(jo)) {
					getICS(project).getMsgService().sendMessage(
							getBackendUserIdFromDomainUserId(userId),
							NEW_NOTE + jo.getUuid().toString());
				} else {
					getICS(project).getMsgService().sendMessage(
							getBackendUserIdFromDomainUserId(userId),
							NEW_FILE + ((FileObject) jo).getRelPath());
				}
			}
		} catch (NotLoggedInException e) {
		} catch (TimeoutException e) {
		} catch (NoSuchUseridException e) {
		} catch (NetworkException e) {
		} catch (OtherUserOfflineException e) {
		}
	}

	@Override
	@Transactional
	public void pullObject(JakeObject jo) throws NoSuchLogEntryException,
			NotLoggedInException, IllegalArgumentException {
		LogEntry<JakeObject> le = (LogEntry<JakeObject>) db.getLogEntryDao(jo).getExists(
				jo);
		log.debug("got logentry: " + le);
		rhp.getPotentialJakeObjectProviders(jo);
		if (le == null) { // delete
			log.debug("lets delete it");
			try {
				deleteBecauseRemoteSaidSo(jo);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchJakeObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if (le.getBelongsTo() instanceof NoteObject) {


		}

		FailoverCapableFileTransferService ts = getTransferService(jo.getProject());
		// ts.request(jo.ge, nsl);
		// TODO: getPotentialProviders
		// if(le.getUserId().equals(userid))
		// throw new com.jakeapp.core.dao.exceptions.NoSuchLogEntryException();

		// if(!isLoggedIn(userid))
		// throw new
		// com.jakeapp.jake.ics.exceptions.OtherUserOfflineException();
		// TODO: fetch
	}

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
	public Iterable<LogEntry<ILogable>> startLogSync(Project project, UserId userId)
			throws IllegalArgumentException, IllegalProtocolException {
		// TODO Auto-generated method stub
		// TODO: request log & fetch answer
		// TODO: make this an async operation (e.g. with an
		// AvailableLaterObject)


		return null;
	}

	private FailoverCapableFileTransferService getTransferService(Project p)
			throws NotLoggedInException {
		// TODO: Use FailoverCapableFileTransferService
		return getIcServicesManager().getTransferService(p, getMyBackendUserid(p));
	}

	/**
	 * This is a expensive operation as it recalculates all hashes <br>
	 * Do it once on start, and then use a listener
	 */
	@Override
	public Iterable<JakeObjectSyncStatus> getFiles(Project p) throws IOException {
		IFSService fss = getFSS(p);

		List<String> files = fss.recursiveListFiles();

		for (JakeObject jo : getPullableFileObjects(p)) {
			if (!isNoteObject(jo)) {
				FileObject fo = (FileObject) jo;
				fo.getRelPath();
			}
		}
		List<JakeObjectSyncStatus> stat = new LinkedList<JakeObjectSyncStatus>();
		for (String f : files) {
			boolean existsLocal;
			FileObject fo;
			try {
				fo = getFileObjectByRelpath(p, f);
				existsLocal = existsLocally(fo);
			} catch (NoSuchJakeObjectException e1) {
				// local file, not in project
				existsLocal = true;
			}
			boolean existsRemote = false;
			boolean inConflict = false;
			boolean locallyModified = false; // TODO: isLocallyModified(fo);
			try {
				// FIXME: FIX ME FIX ME FIX ME FIX ME FIX ME FIX ME FIX ME FIX
				// ME FIX ME
				// This should contain the correct values
				stat.add(new JakeObjectSyncStatus(f, fss.getLastModified(f),
						locallyModified, false, false, false));
			} catch (NotAFileException e) {
				log.debug("should never happen", e);
			} catch (InvalidFilenameException e) {
				log.debug("should never happen", e);
			}
		}
		return stat;
	}

	@Transactional
	@Override
	public Boolean isDeleted(JakeObject jo) {
		// LogEntry<? extends ILogable> le = new LogEntry<ILogable>(null,
		// LogAction.)
		// LogEntry<? extends ILogable> le =
		// db.getLogEntryDao(jo).getMostRecentFor(jo);
		// if(le.getLogAction())
		return null;
	}

	@Transactional
	public Boolean isLocallyModified(FileObject fo) throws InvalidFilenameException,
			IOException {
		if (!existsLocally(fo))
			return false;
		IFSService fss = getFSS(fo.getProject());
		String rhash;
		try {
			rhash = db.getLogEntryDao(fo).getMostRecentFor(fo).getChecksum();
		} catch (NoSuchLogEntryException e1) {
			rhash = null;
		}
		String lhash = null;
		try {
			lhash = fss.calculateHashOverFile(fo.getRelPath());
		} catch (FileNotFoundException e) {
		} catch (InvalidFilenameException e) {
		} catch (NotAReadableFileException e) {
		}
		if (lhash == null) {
			return false; // doesn't exist locally
		}
		if (rhash == null)
			return true; // doesn't exist remote
		else
			return rhash.equals(lhash);
	}

	@Override
	public Boolean isLocallyModified(JakeObject jo) throws InvalidFilenameException,
			IOException {
		if (isNoteObject(jo))
			return isLocallyModified((NoteObject) jo);
		else
			return isLocallyModified((FileObject) jo);
	}

	@Transactional
	@Override
	public Boolean isLocallyModified(NoteObject noin) {
		NoteObject no = null;
		try {
			no = db.getNoteObjectDao(no.getProject()).get(no.getUuid());
		} catch (NoSuchJakeObjectException e) {
			return false;
		}

		return null;

		/*
		 * if (!existsLocally(fo)) return false; IFSService fss =
		 * getFSS(fo.getProject()); String rhash; try { rhash =
		 * db.getLogEntryDao(fo).getMostRecentFor(fo).getChecksum(); } catch
		 * (NoSuchLogEntryException e1) { rhash = null; } String lhash = null;
		 * try { lhash = fss.calculateHashOverFile(fo.getRelPath()); } catch
		 * (FileNotFoundException e) { } catch (InvalidFilenameException e) { }
		 * catch (NotAReadableFileException e) { } if (lhash == null) { return
		 * false; // doesn't exist locally } if (rhash == null) return true; //
		 * doesn't exist remote else return rhash.equals(lhash);
		 */
	}

	@Override
	public Boolean existsLocally(FileObject fo) throws IOException {
		IFSService fss = getFSS(fo.getProject());
		try {
			return fss.fileExists(fo.getRelPath());
		} catch (InvalidFilenameException e) {
			log.fatal("db corrupted: contains invalid filenames");
			return false;
		}
	}

	@Override
	public boolean isObjectInConflict(JakeObject jo) {
		if (!isPullable(jo))
			return false;
		try {
			if (isLocallyModified(jo))
				return true;
		} catch (InvalidFilenameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isPullable(JakeObject jo) {
		LogEntry<JakeObject> newestLe;
		try {
			// TODO: trust
			newestLe = db.getLogEntryDao(jo).getMostRecentFor(jo);
		} catch (NoSuchLogEntryException e) {
			return false;
		}
		LogEntry<JakeObject> myLe;
		try {
			myLe = db.getLogEntryDao(jo).getLastPulledFor(jo);
		} catch (NoSuchLogEntryException e) {
			return true;
		}
		if (newestLe.getUuid() == myLe.getUuid())
			return false;
		else
			return true;
	}

	@Override
	public void startServing(Project p, RequestHandlePolicy rhp, ChangeListener cl)
			throws ProjectException {
		FSService fs;
		try {
			fs = new FSService();
		} catch (NoSuchAlgorithmException e) {
			throw new ProjectException(e);
		}
		if (rhp == null) {
			rhp = new TrustAllRequestHandlePolicy(db, projectsFssMap, userTranslator);
		}
		runningProjects.put(p.getProjectId(), p);
		projectsFssMap.put(p.getProjectId(), fs);
		projectChangeListeners.put(p.getProjectId(), cl);
		// this creates the ics
		getICS(p);
		log.debug("adding receive hooks");
		try {
			getICS(p).getStatusService().login(
					getMyBackendUserid(p),
					p.getCredentials().getPlainTextPassword());
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
					content = bis.readLine();
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


	public ApplicationContextFactory getDb() {
		return db;
	}


	public void setDb(ApplicationContextFactory applicationContextFactory) {
		this.db = applicationContextFactory;
	}

	public void setUserTranslator(UserTranslator userTranslator) {
		this.userTranslator = userTranslator;
	}

	public UserTranslator getUserTranslator() {
		return userTranslator;
	}

	public com.jakeapp.jake.ics.UserId getBackendUserIdFromDomainProjectMember(Project p,
			ProjectMember member) {
		return userTranslator.getBackendUserIdFromDomainProjectMember(p, member);
	}

	public com.jakeapp.jake.ics.UserId getBackendUserIdFromDomainUserId(UserId userid) {
		return userTranslator.getBackendUserIdFromDomainUserId(userid);
	}

	public ProjectMember getProjectMemberFromUserId(Project project, UserId userid)
			throws NoSuchProjectMemberException {
		return userTranslator.getProjectMemberFromUserId(project, userid);
	}

	public UserId getUserIdFromProjectMember(Project project, ProjectMember member) {
		return userTranslator.getUserIdFromProjectMember(project, member);
	}

	/* for the demo, to be removed and replaced by delegate methods */
	@Override
	public IMsgService getBackendMsgService(Project p) {
		return getICS(p).getMsgService().getFriendMsgService();
	}

	@Override
	public IStatusService getBackendStatusService(Project p) {
		return getICS(p).getStatusService();
	}

	@Override
	public IUsersService getBackendUsersService(Project p) {
		return getICS(p).getUsersService();
	}

	@Override
	public IFSService getBackendFSService(Project p) {
		// TODO Auto-generated method stub
		return null;
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
	public void receivedMessage(com.jakeapp.jake.ics.UserId from_userid, String content) {
		int uuidlen = UUID.randomUUID().toString().length();
		UUID projectid = UUID.fromString(content.substring(0, uuidlen));
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
			FileObject fo = new FileObject(UUID.randomUUID(), p, relpath);
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

	private Project getProjectByUserId(UUID projectid) {
		// for(i : getProjectsManagingService().getProjectList()
		// return new Project(null, projectid, null, null);
		// TODO: mocked for the demo
		return runningProjects.get(projectid);
	}

	@Override
	public void getTags(JakeObject jo) {
		db.getLogEntryDao(jo).getCurrentTags(jo);
	}

}
