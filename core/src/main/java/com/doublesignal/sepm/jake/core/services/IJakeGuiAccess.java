package com.doublesignal.sepm.jake.core.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.doublesignal.sepm.jake.core.InvalidApplicationState;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchConfigOptionException;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchProjectMemberException;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchLogEntryException;
import com.doublesignal.sepm.jake.core.domain.*;
import com.doublesignal.sepm.jake.core.services.exceptions.*;
import com.doublesignal.sepm.jake.fss.*;
import com.doublesignal.sepm.jake.ics.IMessageReceiveListener;
import com.doublesignal.sepm.jake.ics.exceptions.*;
import com.doublesignal.sepm.jake.sync.exceptions.*;

/**
 * @author domdorn
 * @author johannes
 * @author philipp
 */
public interface IJakeGuiAccess {
	/**
	 * file was imported into project, it is represented in the database
	 */
    public static final int SYNC_IS_IN_PROJECT = 1;
	/**
	 * is distributed
	 */
    public static final int SYNC_HAS_LOGENTRIES = 2;
	/**
	 * we have this file
	 */
    public static final int SYNC_EXISTS_LOCALLY = 4;
	/**
	 * it is from the latest version
	 */
    public static final int SYNC_LOCAL_IS_LATEST = 8;
	/**
	 * we modified it
	 */
    public static final int SYNC_LOCALLY_CHANGED = 16;
	/**
	 * Did someone delete it?
	 */
    public static final int SYNC_EXISTS_REMOTELY = 32;
	/**
	 * A newer remote version exists 
	 */
    public static final int SYNC_REMOTE_IS_NEWER = 64;
	/**
	 * A newer remote version exists, we have a modified old version. 
	 */
    public static final int SYNC_IN_CONFLICT = SYNC_REMOTE_IS_NEWER | SYNC_LOCALLY_CHANGED;

    /**
	 * Login on the network Missing login information (null values) will be
	 * retrieved from the configuration. If it is not stored either, a
	 * LoginDataRequiredException is thrown.
	 * 
	 * @param userid
	 *            userid to use, can be null
	 * @param pw
	 *            Password to use, can be null
	 * 
	 * @throws NetworkException
	 *             if the ICS couldn't use the network.
	 * @throws LoginUseridNotValidException
	 * @throws LoginDataRequiredException
	 * @throws LoginDataNotValidException
	 */
	public void login(String userid, String pw)
			throws LoginDataRequiredException, LoginDataNotValidException,
			NetworkException, LoginUseridNotValidException;

	/**
	 * Logout from the network
	 * 
	 * @throws NetworkException
	 *             if the ICS tells, that the Logout could not be propagated to
	 *             the network (others may see us still online)
	 */
	public void logout() throws NetworkException;

	/**
	 * Checks if the application is logged into the network
	 * 
	 * @return true if app is logged in.
	 */
	public boolean isLoggedIn();
	
	/**
	 * Checks if the given user id is logged online
	 * 
	 * @param userId of the user of whom the online status is to be checked
	 * @return true if user is logged in.
	 * @throws NoSuchUseridException 
	 */
	
	public Boolean isLoggedIn(String userId) 
		throws NotLoggedInException, NoSuchUseridException;

	/**
	 * Get the user that is currently logged in.
	 * 
	 * @return
	 * @throws NoSuchConfigOptionException 
	 */
	public String getLoginUserid();

	/**
	 * Sets a config
	 * 
	 * @param configKey
	 * @param configValue
	 */
	public void setConfigOption(String configKey, String configValue);

	/**
	 * Gets a config option by the specified key
	 * 
	 * @param configKey
	 * @return the associated value
	 * @throws NoSuchConfigOptionException
	 */
	public String getConfigOption(String configKey)
			throws NoSuchConfigOptionException;

	public List<LogEntry> getLog();
	
	public void createLog(LogEntry logEntry);

	public List<LogEntry> getLog(JakeObject object);

	/**
	 * Adds a listener for the client to be informed of incoming JakeMessages.
	 *
	 * Only one listener can be registered at any time, subsequent calls to this method will overwrite the
	 * previous listener.
	 *
	 * @param listener A client object wanting to be informed of incoming JakeMessages
	 */
	public void registerReceiveMessageListener(IJakeMessageReceiveListener listener);

	/**
	 * Sends a JakeMessage to another ProjectMember.
	 * @param message The JakeMessage to be sent
	 */
	public void sendMessage(JakeMessage message) throws NetworkException, NotLoggedInException, TimeoutException,
			NoSuchUseridException, OtherUserOfflineException;

	
	/**
	 * Add a new Member to a Project
	 * 
	 * @param ProjectMember
	 */
	public void addProjectMember(String UserId);
	
	public List<ProjectMember> getMembers();

	public Project getProject();

	/*
	 * ich muss mir nochmal genau ueberlegen was ich mir dabei gedacht habe. ist
	 * schon etwas zu spaet dafuer. - dominik
	 * 
	 * public String findProjectMember(String UserId);
	 * 
	 * 
	 * public ProjectMember addProjectProjectMember(String UserId) throws
	 * NoSuchNetworkUserException;
	 * 
	 */

	/**
	 * Retrieves a ProjectMember by their user ID
	 * @param userId
	 * @return
	 */
	public ProjectMember getProjectMember(String userId) throws NoSuchProjectMemberException;
	
	/**
	 * Create a new note
	 * 
	 * @param content
	 * @return the NoteObject in question.
	 */
	public NoteObject createNote(String content);

	/**
	 * Edit a specific note.
	 * 
	 * @param selectedNote
	 */
	public void editNote(NoteObject selectedNote);

	/**
	 * Remove a specific note.
	 * 
	 * @param selectedNote
	 */
	public void removeNote(NoteObject selectedNote);

	/**
	 * Get the list of available NoteObjects.
	 * 
	 * @return list of NoteObjects
	 */
	public List<NoteObject> getNotes();

	/**
	 * Make a file manually copied in the the project folder aware to jake.
	 * 
	 * @param relPath
	 * @return list of one or more FileObjects
	 */
	public List<FileObject> createFileObjects(String relPath);

	/**
	 * Import an external File into our Jake Project by Copying it into the
	 * Project Folder
	 * 
	 * @param absolutePath
	 * @return a new JakeObject
	 */
	public FileObject createFileObjectFromExternalFile(String absolutePath)
			throws NoSuchFileException;

	/**
	 * Imports a whole folder from the filesystem into the project folder and
	 * returns a list of all the FileObjects created.
	 * 
	 * @param absolutePath
	 * @return
	 * @throws NoSuchFolderException
	 */
	public List<FileObject> importFolder(String absolutePath)
			throws NoSuchFolderException;

	/**
	 * Gets a list of all the FileObjects
	 * 
	 * @return list of FileObjects
	 * @throws NoSuchJakeObjectException
	 */
	public List<JakeObject> getFileObjects();
	
	/**
	 * Searches for Jake Objects by specifying a name
	 * 
	 * @param name
	 * @return list of JakeObjects
	 */
	public List<JakeObject> getJakeObjectsByName(String name);

	/**
	 * Searches for Jake Objects by specifying one or more tags
	 * 
	 * @param tags
	 * @return list of JakeObjects
	 */
	public List<JakeObject> getJakeObjectsByTags(List<Tag> tags);

	/**
	 * Searches for Jake Objects by specifying a name and one or more tags
	 * 
	 * @param name
	 * @param tags
	 * @return list of JakeObjects
	 */
	public List<JakeObject> getJakeObjectsByNameAndTag(String name,
			List<Tag> tags);

	/**
	 * get a list of all available tags in the project by generating a list of
	 * all used ones.
	 * 
	 * @return a list of Tag Objects
	 */
	public List<Tag> getTags();

	/**
	 * Get a list of all the tags of an certain object.
	 * 
	 * @param object
	 * @return a list of Tag Objects
	 */
	public List<Tag> getTagsOfObject(JakeObject object);

	/**
	 * Queries the FSS for the filesize of a specific FileObject
	 * 
	 * @param fileObject
	 * @return FileSize in Bytes
	 */
	public long getFileSize(FileObject fileObject);

	/**
	 * Returns the Projectmember who last modified this JakeObject (Note/File)
	 * 
	 * @param jakeObject
	 * @return the projectMember
	 */
	public ProjectMember getLastModifier(JakeObject jakeObject) throws NoSuchLogEntryException;

	/**
	 * Returns the date when the object was last modified (according to log
	 * 
	 * @param jakeObject
	 * @return the last modified date
	 */
	public Date getLastModified(JakeObject jakeObject) throws NoSuchLogEntryException;

	/**
	 * Adds a tag to a specific JakeObject
	 * 
	 * @param jakeObject
	 * @param tag
	 * @return the JakeObject given as parameter
	 */
	public JakeObject addTag(JakeObject jakeObject, Tag tag);

	/**
	 * Removes a tag from a specific JakeObject
	 * 
	 * @param jakeObject
	 * @param tag
	 * @return the JakeObject given as parameter
	 */
	public JakeObject removeTag(JakeObject jakeObject, Tag tag);

	/**
	 * open a file with the associated (external) application
	 * 
	 * @param relpath
	 * @throws InvalidFilenameException
	 * @throws LaunchException
	 * @throws IOException
	 * @throws NoProjectLoadedException
	 */
	public void launchFile(String relpath) throws InvalidFilenameException,
			LaunchException, IOException, NoProjectLoadedException;

	/**
	 * Gets the current soft lock comment for a given <code>JakeObject</code>.
	 * 
	 * @param jakeObject
	 * @return The LogEntry that has either LOCK or  UNLOCK as lock action, or
	 * null, iff no locking log entry was found.
	 */
	public LogEntry getJakeObjectLockLogEntry(JakeObject jakeObject);
	
	/**
	 * Determine if a <code>JakeObject</code> has a soft lock
	 * @param jakeObject
	 * @return
	 */
	public boolean getJakeObjectLock(JakeObject jakeObject);

	/**
	 * Sets or unsets the softlock on a certain jakeObject
	 * 
	 * @param isLocked:
	 *            True to set the softlock, false to revoke ist
	 *            
	 * @param jakeObject
	 */
	public void setJakeObjectLock(JakeObject jakeObject, boolean isLocked);
	
	/**
	 * Activates the lock and sets the lock comment for a <code>JakeObject</code>;
	 * @param jakeObject
	 * @param lockComment
	 */
	public void setJakeObjectLockComment(JakeObject jakeObject, String lockComment);
	
	/**
	 * Get the <code>ProjectMember</code> that locked the file.
	 * @param jakeObject
	 * @return
	 */
	public ProjectMember getJakeObjectLockedBy (JakeObject jakeObject);

	/**
	 * Deletes a jakeObject.
	 * 
	 * @param jakeObject
	 * @return true on success, false otherwise
	 */
	public boolean deleteJakeObject(JakeObject jakeObject);
    
    /**
     * Stores a <code>note</code> for a Project Member
     *
     * @param userId the Project Member userid
     * @throws NoSuchProjectMemberException Raised if no user can be found for the given
     *                                     <code>userid</code>.
     */
    public void setProjectMemberNote(String userId,String note) throws NoSuchProjectMemberException;
    
    /**
     * Imports a file which is not currently in the project folder by
     * copying it into a folder inside the projects root folder.
     * @param absolutePath
     * @param destinationFolderRelPath
     * @return true on success, false on error
     */
    boolean importExternalFileIntoProject(String absolutePath, String destinationFolderRelPath);

    /**
     * Deletes a Project Member
     * @param projectMember which is to be deleted
     */
	public void removeProjectMember(ProjectMember selectedMember);
	
	/**
     * Edit a Note of a project member
     * @param projectMember which is to be updated
     * @param note which is to be edited
     */
	public void editProjectMemberNote(ProjectMember selectedMember , String note);

	/**
     * Delete a project member
     * @param selectedMember which is to be updated
     * @param nickName which is to be edited
     */
	public void editProjectMemberNickName(ProjectMember selectedMember , String nickName);

	/**
     * Edit the user id of a project member
     * @param selectedMember which is to be updated
     * @param userId which is to be edited
     */
	public void editProjectMemberUserId(ProjectMember selectedMember , String userId);

	/* Syncronisation stuff */
	
	/**
	 * What should be called in case a Conflict is detected?
	 */
	public void setConflictCallback(IConflictCallback cc);
	
	/**
	 * 
	 * @param jo
	 * @param commitmsg
	 * @throws SyncException
	 * @throws NotLoggedInException
	 */
	public void pushJakeObject(JakeObject jo, String commitmsg) throws SyncException, NotLoggedInException;
	
	/**
	 * 
	 * @param jo
	 * @throws NotLoggedInException
	 * @throws OtherUserOfflineException
	 * @throws NoSuchObjectException 
	 * @throws NoSuchLogEntryException 
	 */
	public void pullJakeObject(JakeObject jo) throws NotLoggedInException, OtherUserOfflineException, NoSuchObjectException, NoSuchLogEntryException;
	
	/**
	 * 
	 * @param userid
	 * @return
	 * @throws OtherUserOfflineException
	 * @throws NotAProjectMemberException
	 * @throws NotLoggedInException
	 */
	//public List<JakeObject> syncLogAndGetChanges(String userid) throws OtherUserOfflineException, NotAProjectMemberException, NotLoggedInException;
	
	/**
	 * @param jo
	 * @return status of the JakeObject, or-Combination of the constants SYNC_*
	 */
	public int calculateJakeObjectSyncStatus(JakeObject jo);
	
	/**
	 * Performs a sync to all project members one after another and calls
	 * refreshFileObjects afterwards.
	 */
	public void syncWithProjectMembers();
	
	/** 
	 * analyses each files sync status.
	 */
	public void refreshFileObjects();
	
	/**
	 * For conflict resolving, a external file can be launched.
	 * @param f
	 * @throws LaunchException 
	 * @throws InvalidFilenameException 
	 */
	public void launchExternalFile(File f) throws InvalidFilenameException, LaunchException;
	
	/**
	 * For conflict resolving, a remote version can be pulled in a temporary 
	 * file
	 * @param jo
	 * @return the temporary file
	 * @throws NotLoggedInException 
	 * @throws OtherUserOfflineException 
	 */
	public File pullRemoteFileInTempFile(FileObject jo) 
		throws NotLoggedInException, OtherUserOfflineException;
	
	

	public void addModificationListener(IModificationListener ob);
	public void removeModificationListener(IModificationListener ob);

	public Date getLocalLastModified(FileObject conflictingFile);

    boolean importLocalFileIntoProject(String relPath);
}
