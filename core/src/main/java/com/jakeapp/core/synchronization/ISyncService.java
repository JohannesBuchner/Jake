package com.jakeapp.core.synchronization;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.synchronization.exceptions.NoSuchObjectException;
import com.jakeapp.core.synchronization.exceptions.NotAProjectMemberException;
import com.jakeapp.core.synchronization.exceptions.ObjectNotConfiguredException;
import com.jakeapp.core.synchronization.exceptions.SyncException;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.msgservice.IMsgService;

/**
 * The task of the synchronisation service (SyncService) is to implement a
 * sharing logic for objects based on the ICService
 * 
 * <p>
 * Each client has a log (see <code>LogEntry</code>). It has the keys
 * (timestamp, relpath, userid), a action and possibly more.
 * </p>
 * 
 * <p>
 * syncLogAndGetChanges() synchronises the local index with another user using
 * the <code>ICService</code>
 * </p>
 * 
 * <p>
 * Then, a pull operation can be issued for a file. This downloads (fetches) the
 * file and puts it in the file system.
 * </p>
 * 
 * <p>
 * If the local client receives such a download operation, the registered fetch
 * callback can still decide wether the download is allowed or not.
 * </p>
 * 
 * <p>
 * A announce operation is adding a log entry and requesting a synclog from each
 * project member one after another.
 * </p>
 * 
 * <p>
 * All methods are best-effort and might fail (in a safe way). Communication is
 * performed with project members only.
 * </p>
 * 
 * Also see the sequential diagrams and technical description.
 * 
 * @see LogEntry
 * @see IMsgService
 * @author johannes
 **/
/*
 * Warning: The Exceptions will likely change (new ones will be added, like
 * NetworkExceptions
 */
public interface ISyncService {

	/**
	 * retrieves and integrates the log from the specified user of the supplied
	 * <code>Project</code>.
	 * 
	 * @param project
	 * @param userId
	 * @return the new LogEntries
	 * @throws IllegalArgumentException
	 *             if the supplied project or userId is null
	 * @throws IllegalProtocolException
	 *             if the supplied UserId is of the wrong protocol-type
	 */
	public Collection<? extends LogEntry> startLogSync(Project project, UserId userId)
			throws IllegalArgumentException, IllegalProtocolException;

	/**
	 * Tells the user userId to do a logSync. It is the way of telling
	 * "hey, we have something new".
	 * 
	 * @param project
	 * @param userId
	 */
	public void poke(Project project, UserId userId);

	/**
	 * The object is requested (found in the log) and its content stored. The
	 * RequestHandlePolicy is asked for users having the object.
	 * 
	 * @param objects
	 *            the objects to be pulled
	 */
	public void pullObject(JakeObject jo);

	/**
	 * Adds a log entry that the object has been modified/created/... Unless you
	 * are in a loop, you probably want to do a poke afterwards.
	 */
	public void announce(JakeObject jo, LogEntry<ILogable> action);

	/* Project member changes: just do a poke */

	/**
	 * Gets a list of all objects that are currently in conflict
	 * 
	 * @param project
	 *            the Project from which the changed objects should be shown
	 * @return a list of all out-of-sync JakeObjects
	 * @throws IllegalArgumentException
	 *             if the supplied Project is null or invalid
	 */
	public Iterable<JakeObject> getObjectsInConflict(Project project)
			throws IllegalArgumentException;


	/**
	 * Tries to find out if the supplied object is softlocked or not
	 * 
	 * @param object
	 *            the JakeObject to query
	 * @return true if locked, false if not or unknown
	 * @throws IllegalArgumentException
	 *             if the supplied JakeObject is null or invalid
	 */
	public boolean isObjectLocked(JakeObject object) throws IllegalArgumentException;


	/**
	 * Tries to set the (soft-)lock of the supplied JakeObject
	 * 
	 * @param object
	 *            the JakeObject the lock should be set
	 * @param message
	 *            the lock-message
	 * @throws IllegalArgumentException
	 *             if the supplied JakeObject is null or invalid
	 * @throws ProjectNotLoadedException
	 *             if the project corresponding to this object is not loaded
	 *             currently
	 */
	public void setObjectLocked(JakeObject object, String message)
			throws IllegalArgumentException, ProjectNotLoadedException;


}
