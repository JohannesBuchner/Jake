package com.jakeapp.core.synchronization;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
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
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.services.ICServicesManager;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.util.ApplicationContextFactory;
import com.jakeapp.jake.fss.FSService;
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
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

/**
 * This class should be active whenever you want to use files
 * <p/>
 * On Project->pause/start call
 * {@link #startServing(Project, RequestHandlePolicy, ChangeListener)} and
 * {@link #stopServing(Project)}
 * <p/>
 * Even when you are offline, this is to be used.
 * 
 * @author johannes
 */
public class SyncServiceImpl extends FriendlySyncService {

	private static final Logger log = Logger.getLogger(SyncServiceImpl.class);

	private static final String POKE_MESSAGE = "<poke/>";

	/**
	 * key is the UUID
	 */
	private Map<String, IFSService> projectsFssMap;

	/**
	 * key is the UUID
	 */
	private Map<String, ChangeListener> projectChangeListeners;

	private RequestHandlePolicy rhp;

	private ApplicationContextFactory db;

	private ICServicesManager icServicesManager;

	private UserTranslator userTranslator;

	private ICService getICS(Project p) {
		try {
			return icServicesManager.getICService(p);
		} catch (ProtocolNotSupportedException e) {
			e.printStackTrace(); // todo
			return null;
		}
	}

	private IFSService getFSS(Project p) {
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
	private Boolean isNoteObject(JakeObject jo) {
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
		LogEntry<? extends ILogable> newestLe;
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


	@Transactional
	private ProjectMember getMyProjectMember(Project p) {
		try {
			return db.getProjectMemberDao(p).get(p.getUserId().getUuid());
		} catch (NoSuchProjectMemberException e) {
			log.fatal("can't find myself in project", e);
			return null;
		}
	}


	private com.jakeapp.jake.ics.UserId getICSUseridFromDomainUserId(Project p) {
		return null; // TODO
	}

	/**
	 * returns all JakeObjects that still exist
	 * 
	 * @return
	 */
	private Iterable<JakeObject> getJakeObjectsWhereLastActionIsNotDelete() {
		// TODO Auto-generated method stub
		return null;
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
	}

	@Override
	public Iterable<JakeObject> getPullableFileObjects(Project project) {
		List<JakeObject> missing = new LinkedList<JakeObject>();
		Iterable<JakeObject> allJakeObjects = getJakeObjectsWhereLastActionIsNotDelete();
		for (JakeObject jo : allJakeObjects) {
			if (!isNoteObject(jo)) {
				FileObject fo = (FileObject) jo;
				if (localIsNewest(fo))
					missing.add(jo);
			}
		}
		return missing;
	}

	@Override
	public Iterable<JakeObject> getObjectsInConflict(Project project)
			throws IllegalArgumentException {
		// find existing locally
		// find existing&modified locally that have a unprocessed NEW_VERSION
		Iterable<JakeObject> pullableFO = getPullableFileObjects(project);
		
		
		return null;
	}

	@Override
	@Transactional
	public boolean isLocked(JakeObject jo) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		// TODO: iterate through logs backwards to find a lock
		// if none found or last is unlocked -> unlocked
		// otherwise -> locked
		//db.getLogEntryDao(jo).getMostRecentFor(jakeObject)
		return false;
	}

	@Override
	public void poke(Project project, UserId userId) {
		// TODO Auto-generated method stub
		// TODO: send userId a message to start a logsync
		try {
			getICS(project).getMsgService().sendMessage(getBackendUserIdFromDomainUserId(userId) , POKE_MESSAGE);
		} catch (NotLoggedInException e) {
		} catch (TimeoutException e) {
		} catch (NoSuchUseridException e) {
		} catch (NetworkException e) {
		} catch (OtherUserOfflineException e) {
		}
	}

	@Override
	@Transactional
	public void pullObject(JakeObject jo) throws NoSuchLogEntryException {
		LogEntry le = db.getLogEntryDao(jo).getMostRecentFor(jo);
		String userid = getMyUserid(jo.getProject());
		rhp.getPotentialJakeObjectProviders(jo);

		// TODO: getPotentialProviders
		// if(le.getUserId().equals(userid))
		// throw new com.jakeapp.core.dao.exceptions.NoSuchLogEntryException();

		// if(!isLoggedIn(userid))
		// throw new
		// com.jakeapp.jake.ics.exceptions.OtherUserOfflineException();
		// TODO: fetch
	}

	@Override
	public Iterable<LogEntry> startLogSync(Project project, UserId userId)
			throws IllegalArgumentException, IllegalProtocolException {
		// TODO Auto-generated method stub
		// TODO: request log & fetch answer
		// TODO: make this an async operation (e.g. with an
		// AvailableLaterObject)
		return null;
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

	public Boolean existsLocally(FileObject fo) throws IOException {
		IFSService fss = getFSS(fo.getProject());
		try {
			return fss.fileExists(fo.getRelPath());
		} catch (InvalidFilenameException e) {
			log.fatal("db corrupted: contains invalid filenames");
			return false;
		}
	}

	public boolean isObjectInConflict(JakeObject jo) {
		// TODO Auto-generated method stub
		return false;
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

		projectsFssMap.put(p.getProjectId(), fs);
		projectChangeListeners.put(p.getProjectId(), cl);
		// TODO: add ics hooks

	}

	@Override
	public void stopServing(Project p) {
		// TODO Auto-generated method stub
		// TODO: remove ics hooks
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

}
