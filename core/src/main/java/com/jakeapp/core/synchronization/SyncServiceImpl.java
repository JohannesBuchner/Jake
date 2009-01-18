package com.jakeapp.core.synchronization;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.NoteObject;
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
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

/**
 * This class should be active whenever you want to use files <p/> On
 * Project->pause/start call
 * {@link #startServing(Project, RequestHandlePolicy, ChangeListener)} and
 * {@link #stopServing(Project)} <p/> Even when you are offline, this is to be
 * used.
 * 
 * @author johannes
 */
public class SyncServiceImpl extends FriendlySyncServiceImpl {

	private static Logger log = Logger.getLogger(SyncServiceImpl.class);

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

	private ICService getICS(Project p) {
		try {
			return icServicesManager.getICService(p);
		} catch (ProtocolNotSupportedException e) {
			e.printStackTrace(); // todo
			return null;
		}
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

	private LogEntry getMostRecentForLogEntry(JakeObject jo) {
		// TODO get from DAO
		return null;
	}

	private FileObject getFileObjectByRelpath(String f) {
		// TODO get from DAO
		return null;
	}

	@Override
	protected Iterable<UserId> getProjectMembers(Project project) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean haveNewest(FileObject fo) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isReachable(Project p, String userid) {
		// TODO Auto-generated method stub
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

	public SyncServiceImpl() {
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

	@Override
	public Iterable<JakeObject> getPullableFileObjects(Project project) {
		List<JakeObject> missing = new LinkedList<JakeObject>();
		Iterable<JakeObject> allJakeObjects = getJakeObjectsWhereLastActionIsNotDelete();
		for (JakeObject jo : allJakeObjects) {
			if (!isNoteObject(jo)) {
				FileObject fo = (FileObject) jo;
				if (haveNewest(fo))
					missing.add(jo);
			}
		}
		return missing;
	}

	@Override
	public void announce(JakeObject jo, LogEntry<ILogable> action, String commitMsg) {
		// TODO Auto-generated method stub
		// TODO: fetch hash
		// TODO: create logentry
		String hash;
		action.setBelongsTo(jo);
		// TODO: set others that shouldn't be set by caller
		action.setTimestamp(new Date());
		action.setComment(commitMsg);
		action.setMember(getMyProjectMember(jo.getProject()));

		if (isNoteObject(jo)) {
			NoteObject note = (NoteObject) jo;
			// hash = fss.calculateHash(note.getContent().getBytes());
			// db.getJakeObjectDao().save(note);
			db.getLogEntryDao(jo.getProject()).create(action);
			// TODO: save note entry
		} else {
			FileObject file = (FileObject) jo;
			// log.debug("File: " + file.getName());
			// db.getJakeObjectDao().save(file);
			// TODO: save file entry
			// try {
			// hash = fss.calculateHashOverFile(jo.getName());
			// TODO: get Hash
			/*
			 * } catch (FileNotFoundException e) { throw new SyncException(e); }
			 * catch (InvalidFilenameException e) { throw new SyncException(e);
			 * } catch (NotAReadableFileException e) { throw new
			 * SyncException(e); }
			 */
		}

		// LogEntry le = new LogEntry(UUID.randomUUID(),
		// LogAction.FILE_NEW_VERSION, new Date(),
		// jo.getName(), hash, userid, commitmsg);
		// TODO: create logentry & save it & set that this version is has been
		// pulled in DB

		// db.getLogEntryDao().create(le);
		// db.getLogEntryDao().setIsLastPulled(le);
		// return new ArrayList<ProjectMember>();

	}

	@Override
	public Iterable<JakeObject> getObjectsInConflict(Project project)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isObjectLocked(JakeObject object) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		// TODO: iterate through logs backwards to find a lock
		// if none found or last is unlocked -> unlocked
		// otherwise -> locked
		return false;
	}

	@Override
	public void poke(Project project, UserId userId) {
		// TODO Auto-generated method stub
		// TODO: send userId a message to start a logsync
		// getICS(project).getMsgService().sendMessage(userId, content)
	}

	@Override
	public void pullObject(JakeObject jo) {
		LogEntry le = getMostRecentForLogEntry(jo);

		String userid = getMyUserid(jo.getProject());
		// TODO: getPotentialProviders
		// if(le.getUserId().equals(userid))
		// throw new com.jakeapp.core.dao.exceptions.NoSuchLogEntryException();

		// if(!isLoggedIn(userid))
		// throw new
		// com.jakeapp.jake.ics.exceptions.OtherUserOfflineException();
		// TODO: fetch
	}

	@Override
	public void setObjectLocked(JakeObject object, String message)
			throws IllegalArgumentException, ProjectNotLoadedException {
		// TODO: free for taking: create & add logentry
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
	 * This is a expensive operation as it recalculates all hashes Do it once on
	 * start, and then use a listener
	 */
	@Override
	public Iterable<JakeObjectSyncStatus> getFiles(Project p) throws IOException {
		IFSService fss = projectsFssMap.get(p.getProjectId());
		List<String> files;
		try {
			// TODO: should this really throw an exception if _one_ file doesnt
			// work? I don't think so. -- dominik
			files = fss.recursiveListFiles();
		}
		// catch(InvalidFilenameException ifne)
		// {
		// TODO @ johannes: is this ok?
		// throw new IOException("InvalidFilenameException: " +
		// ifne.getMessage());
		// }
		catch (IOException e) {
			throw e;
		}
		for (JakeObject jo : getPullableFileObjects(p)) {
			if (!isNoteObject(jo)) {
				FileObject fo = (FileObject) jo;
				fo.getRelPath();
			}
		}
		List<JakeObjectSyncStatus> stat = new LinkedList<JakeObjectSyncStatus>();
		for (String f : files) {
			FileObject fo = getFileObjectByRelpath(f);
			boolean inConflict = isObjectInConflict(fo); // TODO: get
			boolean locallyModified = isLocallyModified(fo);
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

	public Boolean isDeleted(JakeObject fo) {

	}

	public Boolean isLocallyModified(FileObject fo) throws InvalidFilenameException,
			IOException {
		if (!existsLocally(fo))
			return false;
		IFSService fss = projectsFssMap.get(fo.getProject());
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

	public Boolean existsLocally(FileObject fo) throws InvalidFilenameException,
			IOException {
		IFSService fss = projectsFssMap.get(fo.getProject());
		return fss.fileExists(fo.getRelPath());
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

}
