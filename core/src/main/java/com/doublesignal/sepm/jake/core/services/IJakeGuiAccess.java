package com.doublesignal.sepm.jake.core.services;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Observer;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchConfigOptionException;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchProjectMemberException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeMessage;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.Project;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.domain.Tag;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginDataNotValidException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginDataRequiredException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginUseridNotValidException;
import com.doublesignal.sepm.jake.core.services.exceptions.NoProjectLoadedException;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFileException;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFolderException;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchJakeObjectException;
import com.doublesignal.sepm.jake.fss.InvalidFilenameException;
import com.doublesignal.sepm.jake.fss.LaunchException;
import com.doublesignal.sepm.jake.fss.NotADirectoryException;
import com.doublesignal.sepm.jake.fss.NotAFileException;
import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;

/**
 * @author domdorn
 * @author johannes
 */
public interface IJakeGuiAccess {

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
	 * Get the user that is currently logged in.
	 * 
	 * @return
	 */
	public String getLoginUser() throws NotLoggedInException;

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

	/**
	 * do a logSync
	 */
	public void logSync() throws NetworkException;

	/**
	 * Returns a list of one or more JakeObjects which are reported as
	 * OutOfSync, so they can be visualized by the gui.
	 * 
	 * @return list of Jake Objects
	 */
	public List<JakeObject> getOutOfSyncObjects();

	/**
	 * Returns a list of one or more JakeObjects which where changed by a recent
	 * PullObjects() call, so they can be visualized by the gui.
	 * 
	 * @return list of Jake Objects
	 */
	public List<JakeObject> getChangedObjects();

	public void pushObjects() throws NetworkException;

	public void pullObjects() throws NetworkException;

	/*
	 * TODO: Resolve version conflicts!!!
	 */

	public List<LogEntry> getLog();

	public List<LogEntry> getLog(JakeObject object);

	public void registerReceiveMessageCallback(Observer observer);

	public List<JakeMessage> getNewMessages();
	
	/**
	 * Add a new Member to a Project
	 * 
	 * @param UserId
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
	 * Registers
	 * 
	 * @param obs
	 */
	public void registerProjectInvitationCallback(Observer obs);

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
	 * Gets a list of all the FileObjects in one directory (if relpath is a
	 * directory) or the FileObject in question
	 * 
	 * @param relPath
	 * @return list of FileObjects
	 * @throws NoSuchJakeObjectException
	 */
	public List<JakeObject> getFileObjects(String relPath)
            ;

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
	public ProjectMember getLastModifier(JakeObject jakeObject);

	/**
	 * Returns the date when the object was last modified (according to log
	 * 
	 * @param jakeObject
	 * @return the last modified date
	 */
	public Date getLastModified(JakeObject jakeObject);

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
	 * Get the SyncStatus of a JakeObject
	 * 
	 * @param jakeObject
	 * @return SyncStatus (String)
	 */
	public Integer getFileObjectSyncStatus(JakeObject jakeObject);

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
	 * Gets the current softlock status of a jakeObject
	 * 
	 * @param jakeObject
	 * @return true if file has softlock, false otherwise
	 */
	boolean getJakeObjectLock(JakeObject jakeObject);

	/**
	 * Sets or unsets the softlock on a certain jakeObject
	 * 
	 * @param isLocked:
	 *            True to set the softlock, false otherwise
	 * @param jakeObject
	 * @return returns the new softlock status of the jakeObject
	 */
	boolean setJakeObjectLock(boolean isLocked, JakeObject jakeObject);

	/**
	 * Deletes a jakeObject.
	 * 
	 * @param jakeObject
	 * @return true on success, false otherwise
	 */
	boolean deleteJakeObject(JakeObject jakeObject);

	/**
	 * Shedules the pushing of the given JakeObject
	 * 
	 * @param jakeObject
	 *            the JakeObject which should be distributed
	 */
	void propagateJakeObject(JakeObject jakeObject);

	/**
	 * Shedules the pulling of the given JakeObject *
	 * 
	 * @param jakeObject
	 *            the JakeObject to be pulled
	 */
	void pullJakeObject(JakeObject jakeObject);


    /**
     * Querys the FSS and Database for the current data and makes shure
     * the used datastructures are updated accordingly
     */
    void refreshFileObjects();
    
    /**
     * Stores a <code>note</code> for a Project Member
     *
     * @param userId the Project Member userid
     * @throws NoSuchProjectMemberException Raised if no user can be found for the given
     *                                     <code>userid</code>.
     */
    public void setProjectMemberNote(String userId,String note) throws NoSuchProjectMemberException;

    /**
     * Imports a file which is already in the project folder into the projects
     * repository (adding to databse, generating log entries, etc.)
     * @param relPath the relative path of this file
     * @return true on success, false on error
     */
    boolean importLocalFileIntoProject(String relPath);

    /**
     * Imports a file which is not currently in the project folder by
     * copying it into a folder inside the projects root folder.
     * @param absolutePath
     * @param destinationFolderRelPath
     * @return true on success, false on error
     */
    boolean importLocalFileIntoProject(String absolutePath, String destinationFolderRelPath);

    /**
     * Deletes a Project Member
     * @param projectMember which is to be deleted
     */
	public void removeProjectMember(ProjectMember selectedMember);
    
}
