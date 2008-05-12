package com.doublesignal.sepm.jake.sync;

import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.ics.IICService;

import java.io.IOException;

/**
 * The task of the synchronisation service (SyncService) is to 
 * implement a sharing logic for objects based on the ICService
 * 
 * Each client has a log index with the keys (timestamp, relpath, userid), a 
 *   action and possibly more.
 * 
 * synclog() synchronises the local index with another user using the ICService
 *   @see IICService
 * 
 * Then, a pull operation can be issued for a file. This downloads (fetches) the 
 *   file and puts it in the file system. 
 * 
 * If the local client receives such a download operation, the registered fetch 
 * callback can still decide wether the download is allowed or not.  
 * 
 * A push operation is adding a log entry and requesting a synclog from each 
 *   project member one after another.  
 * 
 * All methods are best-effort and might fail (in a safe way).
 * Communication is performed with project members only.
 * 
 * @see sequential diagrams 
 * @author johannes 
 **/

public interface ISyncService {
	/**
	 * Connects to the given user and request their log.
	 * Then, the log is merged with (added to) the local log.
	 * 
	 * @param userid @see IICService
	 * @return the list of objects that have changed/are new/were deleted or 
	 *   touched in some other way
	 */
	public JakeObject[] synclog(String userid) 
		throws IOException;
	
	/**
	 * The object is requested from the last editor (found in the log) and 
	 * its content returned.
	 * @param jo
	 * @return The Object content if successful, null else
	 */
	public String pull(JakeObject jo) 
		throws IOException;
	
	/**
	 * Adds a log entry that the object has been modified/created/...
	 * Then, asks each project member one after another to start a synclog.   
	 * 
	 * @param  jo    Object to be treated.
	 * @return 
	 * @throws IOException
	 */
	public Boolean push(JakeObject jo)
		throws IOException;
	
	/**
	 * This has to be registered in the used ICService to be called on pull 
	 * requests. It loads the object content and sends it back to the requester.
	 * 
	 * It should always send the content back if possible, because the newest 
	 * file version can only be retrieved from the last editor. 
	 * 
	 * @param  jo     Object requested
	 * @return the object content, or null if not available.
	 * @throws IOException
	 * 
	 * TODO: maybe set protected?
	 */
	public String handleFetchRequest(JakeObject jo)	
		throws IOException;
	
	/** The following methods ought to be set on initialization e.g. via 
	 * dependency-injection. The parameters ought to be references, not copies.
	 */
	
	/**
	 * Set the ICService to be used.
	 * This must be set before operations can be used.
	 * @see IICService
	 */
	public void setICService(IICService ics);
	
	/**
	 * Set the Log to be used.
	 * This must be set before operations can be used.
	 * @see IICService
	 * 
	 * TODO: Can we add logentries using this model?
	 */
	public void setLogEntries(LogEntry[] le);
	
	/**
	 * set the project members list to be used.
	 * This must be set before operations can be used.
	 * @see IICService
	 */	
	public void setProjectMembers(ProjectMember[] pm);
	
	
}
