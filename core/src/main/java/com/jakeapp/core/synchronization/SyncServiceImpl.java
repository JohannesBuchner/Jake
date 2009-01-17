package com.jakeapp.core.synchronization;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.services.InternalFrontendService;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
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

	private InternalFrontendService pk;

	private ICService getICS(Project p) {
		return pk.getICSForProject(p);
	}

	/* DAO stuff */
	private Boolean isNoteObject(JakeObject jo) {
		// TODO check if object is notes or file otherwise
		return jo.getClass().equals(NoteObject.class);
	}

	private LogEntry getMostRecentForLogEntry(JakeObject jo) {
		// TODO get from DAO
		return null;
	}

	private FileObject getFileObjectByRelpath(String f) {
		// TODO get from DAO
		return null;
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


	public SyncServiceImpl(InternalFrontendService frontendService) {
		this.pk = frontendService;
	}

	@Override
	protected Iterable<JakeObject> getMissingJakeObjects(Project project) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Iterable<UserId> getProjectMembers(Project project) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void announce(JakeObject jo, LogEntry<ILogable> action) {
		// TODO Auto-generated method stub
		// TODO: fetch hash
		// TODO: create logentry
		String hash;
		if (isNoteObject(jo)) {
			NoteObject note = (NoteObject) jo;
			// hash = fss.calculateHash(note.getContent().getBytes());
			// db.getJakeObjectDao().save(note);
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
	public Iterable<FileStatus> getFiles(Project p) throws IOException {
		IFSService fss = projectsFssMap.get(p.getProjectId());
		List<String> files;
		try {
			// TODO: should this really throw an exception if _one_ file doesnt work? I don't think so. -- dominik
			files = fss.recursiveListFiles();
		}
//        catch(InvalidFilenameException ifne)
//        {
//             TODO @ johannes: is this ok?
//            throw new IOException("InvalidFilenameException: " + ifne.getMessage());
//        }
		catch (IOException e) {
			throw e;
		}
		for (JakeObject jo : getMissingJakeObjects(p)) {
			if (!isNoteObject(jo)) {
				FileObject fo = (FileObject) jo;
				fo.getRelPath();
			}
		}
		List<FileStatus> stat = new LinkedList<FileStatus>();
		for (String f : files) {
			FileObject fo = getFileObjectByRelpath(f);
			boolean inConflict = isObjectInConflict(fo); // TODO: get
			boolean locallyModified = isLocallyModified(fo);
			try {
				// FIXME: FIX ME FIX ME FIX ME FIX ME FIX ME FIX ME FIX ME FIX ME FIX ME
				// This should contain the correct values
				stat.add(new FileStatus(f, fss.getLastModified(f),
					 locallyModified, false, false, false));
			} catch (NotAFileException e) {
				log.debug("should never happen", e);
			} catch (InvalidFilenameException e) {
				log.debug("should never happen", e);
			}
		}
		return stat;
	}

	public Boolean isLocallyModified(FileObject fo) {
		IFSService fss = projectsFssMap.get(fo.getProject());
		String rhash = getMostRecentForLogEntry(fo).getChecksum();
		String lhash = null;
		try {
			lhash = fss.calculateHashOverFile(fo.getRelPath());
		} catch (FileNotFoundException e) {
		} catch (InvalidFilenameException e) {
		} catch (NotAReadableFileException e) {
		}
		if (lhash == null) {
			return false;
		}
		if (rhash == null)
			return true;
		else
			return rhash.equals(lhash);
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

}
