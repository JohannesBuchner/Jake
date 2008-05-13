package com.doublesignal.sepm.jake.sync;

import java.util.List;

import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.ics.IICService;
import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.ics.exceptions.OtherUserOfflineException;
import com.doublesignal.sepm.jake.ics.exceptions.TimeoutException;
import com.doublesignal.sepm.jake.sync.exceptions.ObjectNotConfiguredException;

import java.io.IOException;

/**
 * The task of the synchronisation service (SyncService) is to 
 * implement a sharing logic for objects based on the ICService
 * 
 * Each client has a log. It has the keys (timestamp, relpath, userid), a 
 *   action and possibly more. 
 *   @see LogEntry
 * 
 * syncLogAndGetChanges() synchronises the local index with another user using 
 *   the ICService 
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
	 * The log is requested from the given user.
	 * Then, the log is merged with (added to) the local log.
	 * 
	 * @param    userid @see IICService
	 * @return   the list of objects that have changed/are new/were deleted or 
	 *           touched in some other way
	 * @throws ObjectNotConfiguredException 
	 * @throws NetworkException
	 * @throws NotLoggedInException
	 * @throws TimeoutException
	 */
	public List<JakeObject> syncLogAndGetChanges(String userid) 
		throws NetworkException, NotLoggedInException, TimeoutException, 
			ObjectNotConfiguredException;
	
	/**
	 * The object is requested from the last editor (found in the log) and 
	 * its content returned.
	 * @param jo
	 * @return the object content
	 * @throws ObjectNotConfiguredException 
	 * @throws OtherUserOfflineException
	 * @throws NetworkException
	 * @throws NotLoggedInException
	 * @throws TimeoutException
	 * 
	 * TODO: write the object content to the jakeobject or return a bytearray or
	 * something similar (Strings look too human-readable). 
	 */
	public String pull(JakeObject jo) 
		throws NetworkException, NotLoggedInException, TimeoutException, 
			OtherUserOfflineException, ObjectNotConfiguredException;
	
	/**
	 * Adds a log entry that the object has been modified/created/...
	 * Then, asks each project member one after another to start a synclog. 
	 * These requests are best-effort only.   
	 * 
	 * @param  jo       Object to be treated.
	 * @return members  whom the request reached; empty list if no one could be 
	 *                  reached
	 * @throws ObjectNotConfiguredException
	 */
	public List<ProjectMember> push(JakeObject jo) 
		throws ObjectNotConfiguredException;
	
	/* (Only for implementing the SyncService, not for the interface)
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
	 *
	public String handleFetchRequest(JakeObject jo)	
		throws NetworkException, NotLoggedInException, TimeoutException;
	 */
	
	/* The following methods ought to be set on initialization e.g. via 
	 * dependency-injection. The parameters ought to be references, not copies.
	 * If the following are not set, a ObjectNotConfiguredException is thrown 
	 * from the other operations.
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
	 */
	public void setLogEntries(List<LogEntry> le);
	
	/**
	 * set the project members list to be used.
	 * This must be set before operations can be used.
	 * @see IICService
	 */	
	public void setProjectMembers(List<ProjectMember> pm);
	
	
}
