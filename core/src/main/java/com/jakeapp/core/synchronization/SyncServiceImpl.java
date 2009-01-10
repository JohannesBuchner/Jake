package com.jakeapp.core.synchronization;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.services.FrontendSession;
import com.jakeapp.core.services.IFrontendSession;
import com.jakeapp.core.synchronization.exceptions.NoSuchObjectException;
import com.jakeapp.core.synchronization.exceptions.NotAProjectMemberException;
import com.jakeapp.core.synchronization.exceptions.ObjectNotConfiguredException;
import com.jakeapp.core.synchronization.exceptions.SyncException;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.msgservice.IMsgService;


public class SyncServiceImpl extends FriendlySyncServiceImpl {

	private Map<Project, IFSService> fssMap;

	private RequestHandlePolicy rhp;

	private IFrontendSession pk;

	public SyncServiceImpl(IFrontendSession pk) {
		this.pk = pk;
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
	public Collection<? extends LogEntry> startLogSync(Project project, UserId userId)
			throws IllegalArgumentException, IllegalProtocolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startServing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopServing() {
		// TODO Auto-generated method stub
		
	}


}
