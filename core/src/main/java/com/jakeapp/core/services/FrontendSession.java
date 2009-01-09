package com.jakeapp.core.services;

import java.util.LinkedList;
import java.util.List;

import com.jakeapp.jake.ics.ICService;

/**
 * Session of work that is established whenever any Frontend
 * accesses the core. The session contains references to all
 * Services needed by the frontend.
 * @author christopher
 */
public class FrontendSession {
	
	private List<MsgService> msgServices = new LinkedList<MsgService>();
	
	private IProjectsManagingService pms = new ProjectsManagingServiceImpl();
	
	public FrontendSession() {
		//TODO create and store ics-Service-Implementations
	}
	
    public IProjectsManagingService getProjectsManagingService() throws  IllegalStateException {
        return pms;
    }

    public List<MsgService> getMsgServices() throws  IllegalStateException {
        return msgServices;
    }
}
