package com.jakeapp.core.synchronization;

import java.util.List;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.synchronization.exceptions.NoSuchObjectException;
import com.jakeapp.core.synchronization.exceptions.NotAProjectMemberException;
import com.jakeapp.core.synchronization.exceptions.ObjectNotConfiguredException;
import com.jakeapp.core.synchronization.exceptions.SyncException;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.ics.IICService;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;

/**
 * The task of the synchronisation service (SyncService) is to 
 * implement a sharing logic for objects based on the ICService
 * 
 * <p>Each client has a log (see <code>LogEntry</code>). It has the keys (timestamp, relpath, userid), a 
 *   action and possibly more.</p> 
 * 
 * <p>syncLogAndGetChanges() synchronises the local index with another user using 
 *   the <code>ICService</code></p>
 * 
 * <p>Then, a pull operation can be issued for a file. This downloads (fetches) the 
 *   file and puts it in the file system.</p> 
 * 
 * <p>If the local client receives such a download operation, the registered fetch 
 * callback can still decide wether the download is allowed or not.</p>  
 * 
 * <p>A push operation is adding a log entry and requesting a synclog from each 
 *   project member one after another.</p>  
 * 
 * <p>All methods are best-effort and might fail (in a safe way).
 * Communication is performed with project members only.</p>
 * 
 * Also see the sequential diagrams. 
 * 
 * @see LogEntry
 * @see IICService
 * @author johannes 
 **/


public interface ISyncService {
	/**
	 * The log is requested from the given user.
	 * Then, the log is merged with (added to) the local log.
	 * Nonexisting JakeObjects are created.
	 * 
	 * @param    userid (see IICService)
	 * @return   the list of objects that have changed/are new/were deleted or 
	 *           touched in some other way
	 * @throws ObjectNotConfiguredException 
	 * @throws NetworkException
	 * @throws NotLoggedInException
	 * @throws TimeoutException
	 * @throws OtherUserOfflineException 
	 * @throws NotAProjectMemberException 
	 * @see IICService
	 */
	public List<JakeObject> syncLogAndGetChanges(String userid) 
		throws NetworkException, NotLoggedInException, TimeoutException, 
			ObjectNotConfiguredException, OtherUserOfflineException, 
			NotAProjectMemberException;
	
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
	 * @throws NoSuchObjectException The JakeObject has no logentry
	 * 
	 * <p>NOTE: We don't have pullAndWriteToFile because JakeObjects don't have to 
	 * be files</p>
	 * <p>NOTE: You probably want to set "is_last_pulled" after pulling</p>
	 * @throws NoSuchLogEntryException 
	 */
	public byte[] pull(JakeObject jo) 
		throws NetworkException, NotLoggedInException, TimeoutException, 
			OtherUserOfflineException, ObjectNotConfiguredException, 
			NoSuchObjectException, NoSuchLogEntryException;
	
	/**
	 * Adds a log entry that the object has been modified/created/...
	 * Then, asks each project member one after another to start a synclog. 
	 * These requests are best-effort only.   
	 * 
	 * @param  jo        JakeObject to be treated.
	 * @param  commitmsg 
	 * @param  userid    Your userId
	 * @return members   whom the request reached; empty list if no one could be 
	 *                   reached
	 * @throws ObjectNotConfiguredException
	 * @throws SyncException
	 */
	public List<ProjectMember> push(JakeObject jo, String userid, 
			String commitmsg) 
		throws ObjectNotConfiguredException, SyncException;



    
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
	 * Set the database to be used.
	 * This must be set before operations can be used.
	 */
	//public void setDatabase(IJakeDatabase db);
	
	/**
	 * set the filesystem service to be used.
	 * This must be set before operations can be used.
	 * @see IFSService
	 */
	public void setFSService(IFSService fss);
	
	/**
	 * @return true iff setLogEntries, setProjectMembers and setFSService were
	 *         called before
	 */
	public boolean isConfigured();
}