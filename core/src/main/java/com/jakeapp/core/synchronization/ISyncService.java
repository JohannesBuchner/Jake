package com.jakeapp.core.synchronization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
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
	public Iterable<LogEntry<ILogable>> startLogSync(Project project, UserId userId)
			throws IllegalArgumentException, IllegalProtocolException;

	/**
	 * Tells the user userId to do a logSync. It is the way of telling
	 * "hey, we have something new". This makes no guarantees and fails silently
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
	 * @throws NoSuchLogEntryException
	 *             the object does not exist (no one announced it)
	 * @throws IllegalArgumentException 
	 * @throws NotLoggedInException 
	 */
	public void pullObject(JakeObject jo) throws NoSuchLogEntryException, NotLoggedInException, IllegalArgumentException;

	/**
	 * Adds a log entry that the object has been modified, created, deleted,
	 * tagged, untagged <br>
	 * Unless you are in a loop, you probably want to do a poke afterwards.
	 * 
	 * @param jo
	 * @param action
	 *            a prepared logentry
	 * @param commitMsg
	 * @throws NotAReadableFileException 
	 * @throws InvalidFilenameException 
	 * @throws FileNotFoundException 
	 * @throws IllegalArgumentException
	 *             if you are doing it wrong
	 * @see LogAction for what to set
	 */
	public void announce(JakeObject jo, LogEntry<JakeObject> action, String commitMsg) throws FileNotFoundException, InvalidFilenameException, NotAReadableFileException;

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
	public boolean isLocked(JakeObject object) throws IllegalArgumentException;


	/**
	 * start offering files to others, etc. TODO
	 * 
	 * @throws ProjectException
	 */
	public void startServing(Project p, RequestHandlePolicy rhp, ChangeListener cl)
			throws ProjectException;

	/**
	 * start offering files to others, etc. TODO
	 */
	public void stopServing(Project p);

	/**
	 * gets the files and their information
	 * 
	 * @return
	 * @throws IOException
	 */
	public Iterable<JakeObjectSyncStatus> getFiles(Project p) throws IOException;

	/**
	 * 
	 * @param jo
	 * @return
	 */
	public boolean isObjectInConflict(JakeObject jo);

	/**
	 * 
	 * @param project
	 * @return
	 */
	public Iterable<JakeObject> getPullableFileObjects(Project project);

	/**
	 * 
	 * @param fo
	 * @return
	 */
	public boolean localIsNewest(JakeObject fo);


	/**
	 * Invites a User to a project.
	 * 
	 * @param project
	 *            The project to invite the user to.
	 * @param userId
	 *            The userId of the User. There is already a corresponding
	 *            ProjectMember-Object stored in the project-local database.
	 */
	void invite(Project project, UserId userId);

	/**
	 * Informs the person who invited us to a project that we accept the
	 * invitation.
	 */
	void notifyInvitationAccepted(Project project, UserId inviter);

	/**
	 * Informs the person who invited us to a project that we reject the
	 * invitation.
	 */
	void notifyInvitationRejected(Project project, UserId inviter);

	/**
	 * 
	 * @param jo
	 */
	void getTags(JakeObject jo);

}
