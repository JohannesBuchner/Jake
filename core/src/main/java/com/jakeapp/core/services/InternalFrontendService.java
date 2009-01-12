package com.jakeapp.core.services;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.jake.ics.ICService;

/**
 * Interface for classes that connect various core-services
 * @author christopher
 */
public interface InternalFrontendService {
	ICService getICSForProject(Project p);
	
	IFriendlySyncService getSync();
}
