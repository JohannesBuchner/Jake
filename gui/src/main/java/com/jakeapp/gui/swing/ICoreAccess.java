package com.jakeapp.gui.swing;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NoSuchMsgServiceException;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.core.synchronization.UserInfo;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.callbacks.ConnectionStatus;
import com.jakeapp.gui.swing.callbacks.ErrorCallback;
import com.jakeapp.gui.swing.callbacks.FilesChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.RegistrationStatus;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.exceptions.InvalidNewFolderException;
import com.jakeapp.gui.swing.exceptions.NoteOperationFailedException;
import com.jakeapp.gui.swing.exceptions.PeopleOperationFailedException;
import com.jakeapp.gui.swing.exceptions.ProjectFolderMissingException;
import com.jakeapp.gui.swing.helpers.FolderObject;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface ICoreAccess {

	/**
	 * file was imported into project, it is represented in the database
	 */
	@Deprecated
	public static final int SYNC_IS_IN_PROJECT = 1;
	/**
	 * is distributed
	 */
	@Deprecated
	public static final int SYNC_HAS_LgetAllOGENTRIES = 2;
	/**
	 * we have this file
	 */
	@Deprecated
	public static final int SYNC_EXISTS_LOCALLY = 4;
	/**
	 * it is from the latest version
	 */
	@Deprecated
	public static final int SYNC_LOCAL_IS_LATEST = 8;
	/**
	 * we modified it
	 */
	@Deprecated
	public static final int SYNC_LOCALLY_CHANGED = 16;
	/**
	 * Did someone delete it?
	 */
	@Deprecated
	public static final int SYNC_EXISTS_REMOTELY = 32;
	/**
	 * A newer remote version exists
	 */
	@Deprecated
	public static final int SYNC_REMOTE_IS_NEWER = 64;
	/**
	 * A newer remote version exists, we have a modified old version.
	 */
	@Deprecated
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
	 * This tries to create a new Account with the given credentials (real
	 * register on XMPP/ICQ/MSN/etc.)
	 *
	 * @param credentials
	 * @return AvailableLaterObject
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *
	 * @throws Exception
	 * @throws ProtocolNotSupportedException
	 * @throws com.jakeapp.core.domain.exceptions.InvalidCredentialsException
	 *
	 * @throws com.jakeapp.jake.ics.exceptions.NetworkException
	 *
	 */
	public AvailableLaterObject<Void> createAccount(ServiceCredentials credentials)
					throws FrontendNotLoggedInException, InvalidCredentialsException,
								 ProtocolNotSupportedException, NetworkException;


	/**
	 * This adds a new Account/MsgService to the core-database of jake so it can
	 * be used to login and be used with projects
	 *
	 * @param credentials
	 * @return a MsgService instance of the concrete Protocol
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *
	 * @throws com.jakeapp.core.domain.exceptions.InvalidCredentialsException
	 *                                       If the given Credentials are not valid
	 * @throws ProtocolNotSupportedException
	 * @throws Exception
	 * @throws com.jakeapp.jake.ics.exceptions.NetworkException
	 *
	 */
	public MsgService addAccount(ServiceCredentials credentials)
					throws FrontendNotLoggedInException, InvalidCredentialsException,
								 ProtocolNotSupportedException, NetworkException;


	/**
	 * This removes a Account/MsgService in the core database.
	 *
	 * @param msg
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *
	 * @throws InvalidCredentialsException
	 * @throws ProtocolNotSupportedException
	 * @throws NetworkException
	 * @throws com.jakeapp.core.domain.exceptions.NoSuchMsgServiceException
	 *
	 */
	public void removeAccount(MsgService msg)
					throws FrontendNotLoggedInException, InvalidCredentialsException,
								 ProtocolNotSupportedException, NetworkException,
								 NoSuchMsgServiceException;


	/**
	 * This returns a list with MsgServices already registered in the core
	 *
	 * @return a list with MsgServices already registered in the core
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *          if the frontend has no session on the core
	 */
	public List<MsgService<UserId>> getMsgServics()
					throws FrontendNotLoggedInException;

	/******************* Project functions ********************/


	/**
	 * Get all my projects(started/stopped), but not the invited ones. List is
	 * alphabetically sorted.
	 *
	 * @return list of projects.
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *
	 */
	public List<Project> getMyProjects() throws FrontendNotLoggedInException;

	/**
	 * Get projects where i am invited to. List is alphabetically sorted.
	 *
	 * @return list of invited projects.
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *
	 */
	public List<Project> getInvitedProjects() throws FrontendNotLoggedInException;


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
	public AvailableLaterObject<Integer> getProjectFileCount(Project project);

	/**
	 * Returns absolute Size of all files in the project.
	 *
	 * @param project
	 * @return size in bytes.
	 */
	public AvailableLaterObject<Long> getProjectSizeTotal(Project project);


	/**
	 * Creates a new project. Works asyn, fires the callback when finished.
	 * Throws exceptions if path is null or invalid.
	 *
	 * @param name : name of the project
	 * @param path : path of the project
	 * @param msg
	 */
	// TODO: return AvailableLater?
	public void createProject(String name, String path, MsgService msg);


	/**
	 * Deletes a project. Works asyn, fires the callback when finished. Throws
	 * exceptions if path is null or invalid.
	 *
	 * @param project						: project that should be deleted
	 * @param deleteProjectFiles
	 */
	public void deleteProject(Project project, boolean deleteProjectFiles);

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
	 * Manually starts a project syncronize.
	 * Get/Sends all log entries, etc
	 *
	 * @param project
	 */
	void syncProject(Project project);


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
	 * Returns a java file class for the FileObject
	 *
	 * @param fo The wrapped File Object
	 * @return java file class
	 * @throws com.jakeapp.gui.swing.exceptions.FileOperationFailedException
	 *
	 */
	File getFile(FileObject fo) throws FileOperationFailedException;


	/**
	 * Retrieves a file/folder tree for the project
	 *
	 * @param project The project in question
	 * @param parent
	 * @return A FolderObject that represents the root of the tree
	 * @throws com.jakeapp.gui.swing.exceptions.ProjectFolderMissingException
	 *
	 */
	public FolderObject getFolder(Project project, FolderObject parent)
					throws ProjectFolderMissingException;

	/**
	 * Retrieves all files within a project
	 *
	 * @param project The project in question
	 * @return A collection of all FileObjects in the project
	 */
	public AvailableLaterObject<List<FileObject>> getFiles(Project project);

	/**
	 * Gets the sync status of a file
	 *
	 * @param project
	 * @param jakeObject		The jakeObject for which the status should be determined
	 * @return The file's status as int (defined here)
	 */
	public <T extends JakeObject> Attributed<T> getJakeObjectSyncStatus(
					Project project, T jakeObject);

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
	 * Imports a file OR folder which is not currently in the project folder by
	 * copying it into a folder inside the projects root folder.
	 *
	 * @param project
	 * @param files:             list of files to import.
	 * @param destFolderRelPath: if null or "", copy to project root. @return true on success, false on error
	 * @return
	 */
	public AvailableLaterObject<Void> importExternalFileFolderIntoProject(
					Project project, List<File> files, String destFolderRelPath);


	// TODO: What happens to FileObjects and FolderObjects? Are we going to have a common superclass?
	/**
	 * Deletes a file or folder (moves it to system trash)
	 *
	 * @param project
	 * @param relpath The file or folder to be deleted
	 */
	public void deleteToTrash(Project project, String relpath);

	/**
	 * Renames a file
	 *
	 * @param file		The file to rename
	 * @param newName The new name for the file
	 */
	public void rename(FileObject file, String newName);

	/**
	 * Renames a folder
	 *
	 * @param folder	The folder to rename
	 * @param newName The new name for the folder
	 */
	public void rename(FolderObject folder, String newName);

	/**
	 * Gets the tags for a given FileObject
	 *
	 * @param fo
	 * @return
	 */
	public Set<Tag> getTagsForFileObject(FileObject fo);

	/**
	 * Sets the tags for a FileObject.
	 *
	 * @param fo
	 * @param tags
	 */
	public void setTagsForFileObject(FileObject fo, Set<Tag> tags);

	/**
	 * @param jo				object to be announced
	 * @param commitmsg
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *
	 * @return
	 * @throws com.jakeapp.gui.swing.exceptions.FileOperationFailedException
	 */
	public AvailableLaterObject<Void> announceJakeObject(JakeObject jo,
					String commitmsg) throws FileOperationFailedException;


	/**
	 * @param jos JakeObjects to be announced
	 * @throws FileOperationFailedException nested exception for file based errors.
	 */
	public void announceFileObjects(ArrayList<FileObject> jos)
					throws FileOperationFailedException;


	/**
	 * @param jo
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *
	 * @throws OtherUserOfflineException
	 * @throws java.rmi.NoSuchObjectException
	 * @throws NoSuchLogEntryException
	 * @throws com.jakeapp.gui.swing.exceptions.FileOperationFailedException
	 * @return
	 */
	public AvailableLaterObject<Void> pullJakeObject(JakeObject jo)
					throws FileOperationFailedException;

	/**
	 * Creates a folder at a given point in the relative path hierarchy of a project
	 *
	 * @param project		The project where the folder should be created
	 * @param relpath		The relpath under which the folder should be created
	 * @param folderName The name of the new folder
	 * @throws InvalidNewFolderException if the folder cannot be created (exists already, invalid name, ...)
	 */
	public void createNewFolderAt(Project project, String relpath, String folderName)
					throws InvalidNewFolderException;

	/**
	 * Allows for listeners to register which are notified of changes to local or remote files
	 *
	 * @param listener
	 * @param project
	 */
	public void addFilesChangedListener(FilesChanged listener, Project project);

	/**
	 * Unregister of FilesChanged listeners
	 *
	 * @param listener
	 * @param project
	 */
	public void removeFilesChangedListener(FilesChanged listener, Project project);


	/******************* Notes functions ********************/

	/**
	 * Returns the list of all notes
	 *
	 * @param project : project that should be evaluated
	 * @return list of attibuted note objects
	 * @throws NoteOperationFailedException raised if fetching the list of notes failed.
	 */
	public AvailableLaterObject<List<Attributed<NoteObject>>> getNotes(Project project);

	/**
	 * Delete the given note, no matter if it is a local or shared note.
	 *
	 * @param note the note to be deleted.
	 * @throws NoteOperationFailedException raised if an the given note could not be deleted.
	 */
	public void deleteNote(NoteObject note) throws NoteOperationFailedException;

	/**
	 * Add a new note.
	 *
	 * @param note the note to be added
	 * @throws NoteOperationFailedException exception is raised whenever a note could not be created.
	 */
	public void createNote(NoteObject note) throws NoteOperationFailedException;

	/******************* Soft Lock ***************************/

	/**
	 * Set the soft lock for a <code>JakeObject</code>.
	 *
	 * @param jakeObject		 the <code>JakeObject</code> for which the lock is to be set.
	 * @param isSet					enable/disable the lock. Set this to <code>false</code> to disable the lock, <code>true</code> to
	 *                       enable the lock
	 * @param lockingMessage the locking message for the lock. This argument is ignored, if <code>isSet</code> is set to
	 *                       <code>falso</code>
	 */
	public void setSoftLock(JakeObject jakeObject, boolean isSet,
					String lockingMessage);

	/******************* People functions ********************/

	/**
	 * Get all project members for the current project.
	 * There is always at least ONE member of the project.
	 * The first person is always the current user!
	 *
	 * @param project : project that should be evaluated
	 * @return
	 * @throws PeopleOperationFailedException raised if the operations fails.
	 */
	public List<UserInfo> getProjectUser(Project project)
					throws PeopleOperationFailedException;

	/**
	 * Get the UserInfo from a User
	 *
	 * @param member
	 * @return
	 */
	UserInfo getUserInfo(UserId member);

	/**
	 * Sets the nickname of people. Checks for error
	 *
	 * @param project
	 * @param userId
	 * @param nick
	 * @return
	 */
	public boolean setPeopleNickname(Project project, UserId userId, String nick);

	/**
	 * Set the Trust State of people.
	 *
	 * @param project
	 * @param userId
	 * @param trust
	 */
	public void peopleSetTrustState(Project project, UserId userId, TrustState trust);


	/**
	 * Invites a ProjectMember to the project
	 *
	 * @param project : the project to invite the pm
	 * @param userid	: the user id as string (xmpp, ...)
	 */
	void invitePeople(Project project, String userid);

	/**
	 * Returns suggested people for invitation. Returns a list of known people
	 * that are not in current project.
	 *
	 * @param project : current project
	 * @return list of Projectmembers which have only their usernames set (people-xmpp-ids)
	 */
	public List<UserId> getSuggestedUser(Project project);


	/******************* Log functions ********************/


	/**
	 * Returns the last log entries for a project
	 *
	 * @param project		 : the project to query. can be null.
	 * @param jakeObject: log for specific object or global. can be null.
	 * @param entries		 : amount of entries. -1 for everything.
	 * @return list of log entries or empty list.
	 */
	public List<LogEntry<? extends ILogable>> getLog(Project project,
					JakeObject jakeObject, int entries);

	/**
	 * Save the given note. Jesus saves!
	 *
	 * @param note the not that is to saved.
	 * @throws NoteOperationFailedException raised if the operation can not be completed, i.e. if the
	 *                                      note couldn not be saved.
	 */
	public void saveNote(NoteObject note) throws NoteOperationFailedException;

	/**
	 * Logs a user in
	 *
	 * @param service					The message service to log in
	 * @param password				 password needed to authenticate.
	 *                         If password is null it is tried to authenticate without password.
	 * @param rememberPassword Indicates if the password should be stored with the
	 *                         service credentials that will be generated.
	 * @return An object reporting the progress of the login
	 */
	AvailableLaterObject<Boolean> login(MsgService service, String password,
					boolean rememberPassword);


	/**
	 * calculates how many notes are there for a project
	 * @param project
	 * @return
	 *
	 */
	AvailableLaterObject<Integer> getNoteCount(Project project);
}


