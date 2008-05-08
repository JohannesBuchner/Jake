package com.doublesignal.sepm.jake.sync;

/* TODO: import com.doublesignal.sepm.jake.core.domain: 
 * how do we do that? Broke at the moment */

/**
 * The task of the synchronisation service (SyncService) is to 
 * implement a sharing logic for objects based on the ICService
 * 
 * @author johannes 
 **/

public interface ISyncService {
	
	public JakeObject[] synclog(String file);
	
	public Boolean pull(JakeObject jo);
	
	public Boolean push(JakeObject jo);
	
	/* these ought to be set on initialization 
	 * via dependency-injection and are references, not copies
	 * */
	public void setICService(IICService ics);
	
	public void setLogEntries(LogEntry[] le);
	
	public void setProjectMembers(ProjectMember[] pm);
}
