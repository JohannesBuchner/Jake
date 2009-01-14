package com.jakeapp.gui.swing;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NotLoggedInException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.synchronization.exceptions.SyncException;
import com.jakeapp.gui.swing.callbacks.ConnectionStatus;
import com.jakeapp.gui.swing.callbacks.ErrorCallback;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.RegistrationStatus;
import com.jakeapp.gui.swing.exceptions.ProjectFolderMissingException;
import com.jakeapp.gui.swing.helpers.FolderObject;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;

import java.rmi.NoSuchObjectException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collection;


public interface ICoreAccess {

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


	/**************** Main core integration point *************/

	/**
	 * Sets a frontendService to use by the gui - either a direct java
	 * implementation or e.g. a proxy (sockets, rmi, corba, whatever)
	 *
	 * @param frontendService the <code>FrontendService</code> to use by the gui
	 */
	public void setFrontendService(IFrontendService frontendService);


	/**
	 * Credentials needed to authenticate this GUI on the backend
	 *
	 * @param authenticationData A map consiting of credentials (not credentials to
	 *                           MsgServices!)
	 * @throws com.jakeapp.core.domain.exceptions.InvalidCredentialsException
	 *          if the credentials supplied to the backend are invalid
	 */
	public void authenticateOnBackend(Map<String, String> authenticationData)
		 throws InvalidCredentialsException;


	/**
	 * This method has to be called when the application shuts down or the user
	 * explicitly wants to logout this frontend from the backend (destroy the
	 * session)
	 */
	public void backendLogOff();


	/**
	 * This returns a list with MsgServices already registered in the core
	 *
	 * @return a list with MsgServices already registered in the core
	 * @throws NotLoggedInException if the frontend has no session on the core
	 */
	public List<MsgService> getMsgServics() throws NotLoggedInException;


	/******************* Generic functions ********************/

	/**
	 * Adds an error listener for error events
	 *
	 * @param ec
	 */
	public void addErrorListener(ErrorCallback ec);

	/**
	 * Removes the error listener for error events
	 *
	 * @param ec
	 */
	public void removeErrorListener(ErrorCallback ec);


	/******************* User functions ********************/

	/**
	 * Sync Sercice Log In.
	 *
	 * @param user
	 * @param pass
	 * @Deprecated use (boolean) msgService.login() instead
	 */
	@Deprecated
	public void signIn(final String user, final String pass);

	/**
	 * Registers the Connection Status Callback
	 *
	 * @param cb
	 */
	public void addConnectionStatusCallbackListener(ConnectionStatus cb);

	/**
	 * Deregisters the Connecton Status Callback
	 *
	 * @param cb
	 */
	public void removeConnectionStatusCallbackListener(ConnectionStatus cb);

	/**
	 * Register on sync services.
	 *
	 * @param user
	 * @param pass
	 * @Deprecated use (boolean) registerAccount(ServiceCredentials)
	 */
	@Deprecated
	public void register(String user, String pass);


	/**
	 * This tries to create a new Account with the given credentials (real
	 * register on XMPP/ICQ/MSN/etc.)
	 *
	 * @param credentials
	 * @return true on success, false otherwise
	 * @throws NotLoggedInException
	 * @throws Exception
	 * @throws ProtocolNotSupportedException
	 */
	public boolean createAccount(ServiceCredentials credentials)
		 throws NotLoggedInException, InvalidCredentialsException,
		 ProtocolNotSupportedException, Exception;


	/**
	 * This adds a new Account/MsgService to the core-database of jake so it can
	 * be used to login and be used with projects
	 *
	 * @param credentials
	 * @return a MsgService instance of the concrete Protocol
	 * @throws NotLoggedInException
	 * @throws com.jakeapp.core.domain.exceptions.InvalidCredentialsException
	 *                                       If the given Credentials are not valid
	 * @throws ProtocolNotSupportedException
	 * @throws Exception
	 */
	public MsgService addAccount(ServiceCredentials credentials)
		 throws NotLoggedInException, InvalidCredentialsException,
		 ProtocolNotSupportedException, Exception;


	/**
	 * Registers the Registration Callback
	 *
	 * @param cb
	 */
	public void addRegistrationStatusCallbackListener(RegistrationStatus cb);


	/**
	 * Deregsters the Registration Status Callback
	 *
	 * @param cb
	 */
	public void removeRegistrationStatusCallbackListener(RegistrationStatus cb);

	/**
	 * Returns true if a user is signed in successfully.
	 *
	 * @return // TODO 4 Peter
	 * @Deprecated Either redefine this to return true if the frontend is
	 * successfully connected to the backend or get the
	 * "sign-in-status" from the MsgService.getVisibilityStatus()
	 */
	@Deprecated
	public boolean isSignedIn();

	/**
	 * Returns the current logged in project member.
	 *
	 * @return
	 * @Deprecated What do you want? get all MsgServices and their status? Or
	 * just those, that are online? tell dominik
	 */
	@Deprecated
	public ProjectMember getCurrentProjectMember();


	/**
	 * Signs the current user out.
	 *
	 * @Deprecated please use MsgService.logout()
	 */
	@Deprecated
	public void signOut();


	/**
	 * Returns an Array of the last isers that signed in.
	 *
	 * @return
	 */
	public String[] getLastSignInNames();


	/******************* Project functions ********************/


	/**
	 * Get all my projects(started/stopped), but not the invited ones. List is
	 * alphabetically sorted.
	 *
	 * @return list of projects.
	 * @throws com.jakeapp.core.domain.exceptions.NotLoggedInException
	 *
	 */
	public List<Project> getMyProjects() throws NotLoggedInException;

	/**
	 * Get projects where i am invited to. List is alphabetically sorted.
	 *
	 * @return list of invited projects.
	 * @throws com.jakeapp.core.domain.exceptions.NotLoggedInException
	 *
	 */
	public List<Project> getInvitedProjects() throws NotLoggedInException;


	/**
	 * Registers the Project changed Callback. This is called when a project
	 * changes somehow.
	 *
	 * @param cb
	 */
	public void addProjectChangedCallbackListener(ProjectChanged cb);

	/**
	 * Deregisters the project changed callbac.
	 *
	 * @param cb
	 */
	public void removeProjectChangedCallbackListener(ProjectChanged cb);

	/**
	 * Stops the given project
	 *
	 * @param project
	 */
	public void stopProject(Project project);


	/**
	 * Starts the given project
	 *
	 * @param project
	 */
	public void startProject(Project project);

	/**
	 * Returns absolute Number of files of the project.
	 *
	 * @param project
	 * @return
	 */
	public int getProjectFileCount(Project project);

	/**
	 * Returns absolute Size of all files in the project.
	 *
	 * @param project
	 * @return size in bytes.
	 */
	public int getProjectSizeTotal(Project project);


	/**
	 * Creates a new project. Works asyn, fires the callback when finished.
	 * Throws exceptions if path is null or invalid.
	 *
	 * @param name : name of the project
	 * @param path : path of the project
	 */
	public void createProject(String name, String path);


	/**
	 * Deletes a project. Works asyn, fires the callback when finished. Throws
	 * exceptions if path is null or invalid.
	 *
	 * @param project : project that should be deleted
	 */
	public void deleteProject(Project project);

	/**
	 * Joins into a invited project
	 *
	 * @param loc
	 * @param project
	 */
	public void joinProject(String loc, Project project);


	/**
	 * Rejects join of a invited project.
	 *
	 * @param project
	 */
	public void rejectProject(Project project);


	/**
	 * Changes the project name. Needed in the interface, because we need change
	 * events.
	 *
	 * @param project
	 * @param prName
	 */
	public void setProjectName(Project project, String prName);


	/*******************************************************/
	/******************* File functions ********************/
	/*******************************************************/

	/**
	 * Retrieves a file/folder tree for the project
	 *
	 * @param project The project in question
	 * @return A FolderObject that represents the root of the tree
	 */
	public FolderObject getProjectRootFolder(Project project)
		 throws ProjectFolderMissingException;

	/**
	 * Retrieves all files within a project
	 *
	 * @param project The project in question
	 * @return A collection of all FileObjects in the project
	 * @throws ProjectFolderMissingException if the project folder doesn't exist
	 */
	public List<FileObject> getAllProjectFiles(Project project)
		 throws ProjectFolderMissingException;

	/**
	 * Gets the sync status of a file
	 *
	 * @param file The file for which the status should be determined
	 * @return The file's status as int (defined here)
	 */
	public int getFileStatus(Project project, FileObject file);

	/**
	 * Gets the size of a FileObject in the filesystem
	 *
	 * @param file
	 * @return
	 */
	public long getFileSize(FileObject file);

	/**
	 * Get the file size for a local file copy (possibly modified)
	 *
	 * @param fo: the FileObject
	 * @return file size of local file (long)
	 */
	public long getLocalFileSize(FileObject fo);

	/**
	 * Get the lastmodified time for a local file copy (possibly modified)
	 *
	 * @param fo: the FileObject
	 * @return date of last modified stamp of local file
	 */
	public Date getLocalFileLastModified(FileObject fo);

	/**
	 * Gets the last modified date for a FileObject in the filesystem
	 *
	 * @param file
	 * @return
	 */
	public Date getFileLastModified(FileObject file);


	/**
	 * Imports a file which is not currently in the project folder by
	 * copying it into a folder inside the projects root folder.
	 *
	 * @param absPath
	 * @param destFolderRelPath
	 * @return true on success, false on error
	 */
	public boolean importExternalFileIntoProject(String absPath, String destFolderRelPath);


	/**
	 * Returns the Projectmember who last modified this JakeObject (Note/File)
	 *
	 * @param jakeObject
	 * @return the projectMember
	 */
	public ProjectMember getLastModifier(JakeObject jakeObject) throws NoSuchLogEntryException;

	// TODO: What happens to FileObjects and FolderObjects? Are we going to have a common superclass?
	/**
	 * Deletes a file (moves it to system trash)
	 *
	 * @param file The file to be deleted
	 */
	public void deleteToTrash(FileObject file);

	/**
	 * Deletes a folder (moves it to system trash)
	 *
	 * @param folder The folder to be deleted
	 */
	public void deleteToTrash(FolderObject folder);

	/**
	 * Renames a file
	 *
	 * @param file    The file to rename
	 * @param newName The new name for the file
	 */
	public void rename(FileObject file, String newName);

	/**
	 * Renames a folder
	 *
	 * @param folder  The folder to rename
	 * @param newName The new name for the folder
	 */
	public void rename(FolderObject folder, String newName);


	/**
	 * @param jo
	 * @param commitmsg
	 * @throws SyncException
	 * @throws NotLoggedInException
	 */
	public void pushJakeObject(JakeObject jo, String commitmsg) throws SyncException, NotLoggedInException;

	/**
	 * @param jo
	 * @throws NotLoggedInException
	 * @throws OtherUserOfflineException
	 * @throws java.rmi.NoSuchObjectException
	 * @throws NoSuchLogEntryException
	 */
	public void pullJakeObject(JakeObject jo) throws NotLoggedInException, OtherUserOfflineException, NoSuchObjectException, NoSuchLogEntryException;


	/******************* Notes functions ********************/


	/**
	 * Returns the list of all notes
	 *
	 * @param project : project that should be evaluated
	 * @return
	 */
	public List<NoteObject> getNotes(Project project) throws NotLoggedInException,
		 ProjectNotLoadedException;

	/**
	 * Get the <code>Date</code> of the last edit of the note.
	 *
	 * @param note    the note in question
	 * @param project the project the note is associated with
	 * @return the date of the last edit
	 */
	public Date getLastEdit(NoteObject note, Project project);

	/**
	 * Get the <code>ProjectMemeber<code> who last edited the given note.
	 *
	 * @param note    the note in question
	 * @param project the project the note is associated with
	 * @return the <code>ProjectMember</code> who last edited this note.
	 */
	public ProjectMember getLastEditor(NoteObject note, Project project);

	/**
	 * Determine if a note is only local or if it is a shared note.
	 *
	 * @param note the note in question
	 * @param project the <code>Project</code> the given note is associated with.
	 * @return <code>true</code> iff this note is a local note.
	 */
	public boolean isLocalNote(NoteObject note, Project project);
	
	/**
	 * Delete the given note, no matter if it is a local or shared note.
	 * @param note the note to be deleted. 
	 */
	public void deleteNote(NoteObject note);
	
	/**
	 * Add a new note. 
	 * @param note the note to be added
	 * @param project the project the note is associated with
	 */
	public void newNote(NoteObject note, Project project);
	
	/******************* Soft Lock ***************************/
	
	/**
	 * Determine if the given jakeObject is soft locked.
	 * @param jakeObject the jakeObject in question.
	 * @param project the <code>Project</code> the jakeObject is associated with.
	 * @return <code>true</code> iff the given <code>JakeObject</code> is soft locked.
	 */
	public boolean isSoftLocked(JakeObject jakeObject, Project project);
	
	/**
	 * Get the locking message of a soft locked <code>JakeObject</code>
	 * @param jakeObject the JakeObject in question
	 * @param project the <code>Project</code> the jakeObject is associated with.
	 * @return the locking message of the given <code>JakeObject</code>. The method may return <code>
	 * null</code> iff the given <code>JakeObject</code> is not locked.
	 */
	public String getLockingMessage(JakeObject jakeObject, Project project);
	
	/**
	 * Remove the soft lock from a given <code>JakeObject</code>. 
	 * @param jakeObject the <code>JakeObject</code> that should be unlocked.
	 * @param project the <code>Project</code> the jakeObject is associated with.
	 */
	public void removeSoftLock(JakeObject jakeObject, Project project);
	
	/******************* People functions ********************/

	/**
	 * Get all project members for the current project
	 *
	 * @param project : project that should be evaluated
	 * @return
	 */
	public List<ProjectMember> getPeople(Project project);

	/**
	 * Sets the nickname of people. Checks for error
	 *
	 * @param project
	 * @param pm
	 * @param nick
	 * @return
	 */
	public boolean setPeopleNickname(Project project, ProjectMember pm, String nick);

	/**
	 * Set the Trust State of people.
	 *
	 * @param project
	 * @param member
	 * @param trust
	 */
	public void peopleSetTrustState(Project project, ProjectMember member,
	                                TrustState trust);


	/**
	 * Invites a ProjectMember to the project
	 *
	 * @param project : the project to invite the pm
	 * @param userid  : the user id as string (xmpp, ...)
	 */
	void invitePeople(Project project, String userid);

	/**
	 * Returns suggested people for invitation. Returns a list of known people
	 * that are not in current project.
	 *
	 * @param project : current project
	 * @return list of strings (people-xmpp-ids)
	 */
	List<ProjectMember> getSuggestedPeople(Project project);

	/******************* Log functions ********************/


	/**
	 * Returns the last log entries for a project
	 *
	 * @param project     : the project to query. can be null.
	 * @param jakeObject: log for specific object or global. can be null.
	 * @param entries     : amount of entries. -1 for everything.
	 * @return: list of log entries or empty list.
	 */
	public List<LogEntry> getLog(Project project, JakeObject jakeObject, int entries);


}
