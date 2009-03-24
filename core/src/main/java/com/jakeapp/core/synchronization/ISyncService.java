package com.jakeapp.core.synchronization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.msgservice.IMsgService;

/**
 * The task of the synchronisation service (SyncService) is to implement a
 * sharing logic for objects based on the ICService
 * <p/>
 * <p>
 * Each client has a log (see <code>LogEntry</code>). It has the keys
 * (timestamp, relpath, userid), a action and possibly more.
 * </p>
 * <p/>
 * <p>
 * syncLogAndGetChanges() synchronises the local index with another user using
 * the <code>ICService</code>
 * </p>
 * <p/>
 * <p>
 * Then, a pull operation can be issued for a file. This downloads (fetches) the
 * file and puts it in the file system.
 * </p>
 * <p/>
 * <p>
 * If the local client receives such a download operation, the registered fetch
 * callback can still decide wether the download is allowed or not.
 * </p>
 * <p/>
 * <p>
 * A announce operation is adding a log entry and requesting a synclog from each
 * project member one after another.
 * </p>
 * <p/>
 * <p>
 * All methods are best-effort and might fail (in a safe way). Communication is
 * performed with project members only.
 * </p>
 * <p/>
 * Also see the sequential diagrams and technical description.
 *
 * @author johannes
 * @see com.jakeapp.core.domain.logentries.LogEntry
 * @see IMsgService
 */
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
	 * @param pm
	 * @return the new LogEntries
	 * @throws IllegalArgumentException if the supplied project or userId is null
	 * @throws IllegalProtocolException if the supplied UserId is of the wrong protocol-type
	 */
	public Iterable<LogEntry<ILogable>> startLogSync(Project project, User pm)
			throws IllegalArgumentException, IllegalProtocolException;

	/**
	 * Tells the user userId to do a logSync. It is the way of telling
	 * "hey, we have something new". This makes no guarantees and fails silently.
	 *
	 * @param project   the <code>Project</code> whichs should be synced
	 * @param otherUser the <code>User</code> to be poked
	 */
	public void poke(Project project, User otherUser);

	/**
	 * The object is requested (found in the log) and its content stored. The
	 * RequestHandlePolicy is asked for users having the object.
	 *
	 * @param object the object to be pulled
	 * @return the requested <code>JakeObject</code>
	 * @throws NoSuchLogEntryException  the object does not exist (no one announced it)
	 * @throws IllegalArgumentException if the supplied object is currently not supported to be handled
	 * @throws NotLoggedInException	 gets thrown if we're not logged in on the <code>ICService</code>
	 */
	public <T extends JakeObject> T pullObject(T object)
			throws NoSuchLogEntryException, NotLoggedInException, IllegalArgumentException;

	/**
	 * Adds a <code>LogEntry</code> that the object has been modified, created, deleted, ...<br>
	 * Performs the action on the corresponding instance of the <code>JakeObject</code> too. <br>
	 * Unless you are in a loop, you probably want to do a poke afterwards.
	 *
	 * @param jakeObject the <code>JakeObject</code> to be announced.
	 * @param action	 one of LogAction.JAKE_OBJECT_NEW_VERSION
	 *                   LogAction.JAKE_OBJECT_DELETE LogAction.TAG_ADD
	 *                   LogAction.TAG_REMOVE action LogAction.JAKE_OBJECT_LOCK LogAction.JAKE_OBJECT_UNLOCK
	 * @param commitMsg  or tag content
	 * @throws NotAReadableFileException if creating a hash over the <code>FileObject</code> fails
	 * @throws InvalidFilenameException  if creating a hash over the <code>FileObject</code> fails
	 * @throws FileNotFoundException	 if creating a hash over the <code>FileObject</code> fails
	 * @throws IllegalArgumentException  if the given <code>JakeObject</code> does not
	 *                                   contain a <code>Project</code> object
	 * @see LogAction for what to set
	 */
	public void announce(JakeObject jakeObject, LogAction action, String commitMsg)
			throws FileNotFoundException, InvalidFilenameException, NotAReadableFileException;

	/**
	 * Tries to find out if the supplied <code>JakeObject</code> is softlocked or not
	 *
	 * @param jakeObject the {@link JakeObject} to query
	 * @return null if not locked, the {@link LogEntry} otherwise
	 * @throws IllegalArgumentException if the supplied JakeObject is null or invalid
	 */
	public LogEntry<JakeObject> getLock(JakeObject jakeObject)
			throws IllegalArgumentException;


	/**
	 * start offering files to others, etc.
	 *
	 * @param project the <code>Project</code> to be started
	 * @param changeListener the <code>ChangeListener</code> listening for changes regarding this <code>Project</code>
	 * @throws ProjectException if starting of one of the subsystems failed
	 */
	public void startServing(Project project, ChangeListener changeListener)
			throws ProjectException;

	/**
	 * stop offering files to others, etc.
	 *
	 * @param project the <code>Project</code> which should stop serving its services.
	 */
	public void stopServing(Project project);

	/**
	 * Gets the <code>Tag</code>s for the <code>JakeObject</code>.
	 *
	 * @param jakeObject The JakeObject to retrieve all Tags for. Since not
	 * 	all JakeObjects can have associated Tags, this method may do nothing.
	 */
	void getTags(JakeObject jakeObject); //TODO bug #100

	/**
	 * gets the SyncStatus for a specific <code>JakeObject</code>
	 * @param fo The <code>JakeObject</code> to get an Attributed for.
	 * @return A Representation of <code>fo</code> that supports methods for requesting the status.
	 * @throws IllegalArgumentException fo is null.
	 * @throws InvalidFilenameException if fo is a FileObject and its relpath is not valid
	 * @throws IOException if fo is a FileObject and it is impossible to determine
	 * 	if its relpath points to an ordinary file or a directory, or if a general I/O error occurs.
	 */
	public <T extends JakeObject> Attributed<T> getJakeObjectSyncStatus(T fo)
			throws InvalidFilenameException, NotAReadableFileException, IOException;

	/**
	 * returns the local file represented by the given FileObject
	 *
	 * @param fileObject The FileObject to extract the path for the current OS from.
	 * @return null, if the relpath of <code>fileObject</code> is not valid. A <code>File</code>
	 * 	pointing to the correct Path otherwise.
	 * @throws IOException TODO never thrown, please remove
	 */
	public File getFile(FileObject fileObject) throws IOException;
}
