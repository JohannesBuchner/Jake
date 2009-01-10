package com.jakeapp.core.services;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.jake.ics.ICService;


public interface IFrontendSession {
	
	public ICService getICSForProject(Project p);

	public IFriendlySyncService getSync();
	
}
