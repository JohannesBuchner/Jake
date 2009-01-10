package com.jakeapp.core.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
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
	
	private static Logger log = Logger.getLogger(FrontendSession.class);
	
	private List<MsgService> msgServices = new LinkedList<MsgService>();
	
	private IProjectsManagingService pms;
	
	private ICServicesManager icsManager = new ICServicesManager();
	
	private void setIcsManager(ICServicesManager icsManager) {
		this.icsManager = icsManager;
	}

	private ICServicesManager getIcsManager() {
		return icsManager;
	}
	
	/* this is hardwired because there will always be only one sync. EVVAAR!! */
	private IFriendlySyncService sync = new SyncServiceImpl(this);

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
		ICService result = null;
		try {
			result = this.getIcsManager().getICService(p);
		} catch (ProtocolNotSupportedException e) {
			log.error("Retrieving an ICService for a Project failed: ",e);
		}
		return result;
	}
}
