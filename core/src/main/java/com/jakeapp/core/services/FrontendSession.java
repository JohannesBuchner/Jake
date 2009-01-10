package com.jakeapp.core.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.synchronization.SyncServiceImpl;
import com.jakeapp.jake.ics.ICService;

/**
 * Session of work that is established whenever any Frontend
 * accesses the core. The session contains references to all
 * Services needed by the frontend.
 * @author christopher
 */
public class FrontendSession implements IFrontendSession {
	
	private List<MsgService> msgServices = new LinkedList<MsgService>();
	
	private IProjectsManagingService pms;
	
	/* this is hardwired because there will always be only one sync. EVVAAR!! */
	private IFriendlySyncService sync = new SyncServiceImpl(this);
	
	private Map<Project, ICService> icss = new HashMap<Project, ICService>();

	public FrontendSession(IProjectsManagingService pms) {
		this.pms = pms;
	}
	
    public IProjectsManagingService getProjectsManagingService() throws  IllegalStateException {
        return pms;
    }

    public List<MsgService> getMsgServices() throws  IllegalStateException {
        return msgServices;
    }

	public IFriendlySyncService getSync() {
		return sync;
	}

	@Override
	public ICService getICSForProject(Project p) {
		return icss.get(p);
	}

	@Override
	public void createICSForProject(Project p) {
		// TODO Auto-generated method stub
		
	}
}
