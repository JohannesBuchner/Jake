package com.jakeapp.core.synchronization;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.services.FrontendServiceImpl;
import com.jakeapp.core.services.IFrontendSession;
import com.jakeapp.core.services.InternalFrontendService;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.jake.fss.FSService;
import com.jakeapp.jake.fss.IFSService;


public class SyncServiceImpl extends FriendlySyncServiceImpl {

	private Map<Project, IFSService> fssMap;

	private Map<Project, ChangeListener> clMap;

	private RequestHandlePolicy rhp;

	private InternalFrontendService pk;

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
		return false;
	}

	@Override
	public void poke(Project project, UserId userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pullObject(JakeObject jo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setObjectLocked(JakeObject object, String message)
			throws IllegalArgumentException, ProjectNotLoadedException {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterable<LogEntry> startLogSync(Project project, UserId userId)
			throws IllegalArgumentException, IllegalProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<FileStatus> getFiles(Project p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startServing(Project p, RequestHandlePolicy rhp, ChangeListener cl) throws ProjectException {
		FSService fs;
		try {
			fs = new FSService();
		} catch (NoSuchAlgorithmException e) {
			throw new ProjectException(e);
		}
		
		fssMap.put(p, fs);
		clMap.put(p, cl);

	}

	@Override
	public void stopServing(Project p) {
		// TODO Auto-generated method stub

	}

}
