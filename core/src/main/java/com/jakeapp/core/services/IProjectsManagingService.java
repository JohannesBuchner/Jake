package com.jakeapp.core.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.Invitation;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.domain.TrustState;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import com.jakeapp.core.domain.exceptions.UserFormatException;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.synchronization.UserInfo;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.NotADirectoryException;

/**
 * This handles the list of <code>Project</code>s currently managed
 * <p/>
 */
public interface IProjectsManagingService {

	/**
	 * Retrieves a list of all Projects for the specified user.
	 *
	 * @param userMsgService the <code>MsgService</code> for which the <code>Project</code>s should be loaded.
	 * @return an <code>AvailableLaterObject</code> returning a <code>List</code> with <code>Project</code>s
	 *         belonging to the given <code>MsgService</code>
	 */
	AvailableLaterObject<List<Project>> getProjects(MsgService userMsgService);

	/**
	 * Get a list of all Projects known to jake
	 *
	 * @param msg The MsgService to get the projects for
	 * @return a list of all known jake projects
	 */
	List<Project> getProjectList(MsgService msg);


	/**
	 * Get the List of invitations
	 *
	 * @param msg The <code>MsgService</code> to get the <code>Project</code>s for
	 * @return a (possible empty) <code>List</code> of <code>MsgService</code>s
	 */
	List<Invitation> getInvitations(MsgService msg);


	/**
	 * Creates a new <code>Project</code> given the supplied name and rootPath
	 *
	 * @param name	   the name the new <code>Project</code> should have
	 * @param rootPath   the Path to the rootFolder of this <code>Project</code>. If it
	 *                   does not yet exist, it is created.
	 * @param msgService The MessageService this project should be assigned to. <b>THIS
	 *                   MUST NOT BE NULL!</b>
	 * @return the loaded instance of this <code>Project</code>
	 * @throws FileNotFoundException	if the rootPath is invalid
	 * @throws IllegalArgumentException if the supplied <code>name</code> is invalid
	 * @throws NotADirectoryException   gets thrown when the <code>rootPath</code> is not a directory
	 * @throws IOException			  if some other exception in the file system occurred.
	 */
	Project createProject(String name, String rootPath,
						  MsgService msgService) throws IllegalArgumentException, IOException, NotADirectoryException;


	/**
	 * Start the given project (load database)
	 *
	 * @param project the <code>Project</code> to be loaded
	 * @return true on success, false on error
	 * @throws IllegalArgumentException if the supplied <code>Project</code> is null
	 * @throws FileNotFoundException	if the rootPath of the <code>Project</code> does not exist
	 *                                  anymore
	 * @throws ProjectException		 couldn't start the project for another reason (algorithms
	 *                                  missing, desktop not supported by java, etc.)
	 */
	boolean startProject(Project project)
			throws IllegalArgumentException, FileNotFoundException,
			ProjectException;


	/**
	 * Stops the given project (unloads the database, eventually disconnects
	 * from the network)
	 *
	 * @param project the <code>Project</code> to be stopped.
	 * @return true on success, false on error
	 * @throws IllegalArgumentException if the supplied <code>Project</code> is null
	 * @throws FileNotFoundException	if the rootPath of the <code>Project</code> does not exist
	 *                                  anymore
	 * @throws NoSuchProjectException   gets thrown, when the <code>Project</code> to be stopped does not exist (anymore)
	 */
	boolean stopProject(Project project) throws IllegalArgumentException, FileNotFoundException,
			NoSuchProjectException;

	/**
	 * Adds a ChangeListener for <code>JakeObject</code> changes. Usually called when a frontend logs in.
	 *
	 * @param changeListener the <code>ChangeListener</code> to be added to the list.
	 */
	public void addChangeListener(ChangeListener changeListener);

	/**
	 * Loads the given project (load database)
	 *
	 * @param project the <code>Project</code> to be opened.
	 * @return the opened, but not yet started, <code>Project</code>
	 * @throws IllegalArgumentException if the supplied name is null
	 * @throws FileNotFoundException	if the rootPath of the loaded <code>Project</code> does not
	 *                                  exist anymore or it is not a readable directory or the
	 *                                  database- file cannot be opened.
	 * @throws InvalidProjectException  if the given <code>Project</code> does not exist or is invalid
	 * @throws NotADirectoryException   if the {@link Project#rootPath} is not a directory
	 * @throws IOException			  if some other IO-Error occurred
	 */
	Project openProject(Project project)
			throws IllegalArgumentException, InvalidProjectException, IOException, NotADirectoryException;


	/**
	 * Stops the given project and removes it from the list of projects.
	 *
	 * @param project the <code>Project</code> to be closed.
	 * @throws IllegalArgumentException if the supplied <code>Project</code> is null
	 * @throws FileNotFoundException	if the rootPath of the <code>Project</code> does not exist
	 *                                  anymore
	 * @throws NoSuchProjectException   if the supplied <code>Project</code> does not exist (anymore)
	 */
	void closeProject(Project project) throws IllegalArgumentException,
			FileNotFoundException,
			NoSuchProjectException;


	/**
	 * @param project			the <code>Project</code> to be deleted
	 * @param deleteProjectFiles <code>boolean</code> describing if the files within the Project-Folder should
	 *                           be deleted as well
	 * @return true on success, false on error
	 * @throws IllegalArgumentException if the supplied <code>Project</code> is null
	 * @throws SecurityException		if the supplied <code>Project</code> could not be deleted due
	 *                                  to filesystem permissons
	 * @throws NotADirectoryException   if the {@link Project#rootPath} is not a directory
	 * @throws IOException			  if some other IO-Error occured
	 * @throws FileNotFoundException	if the {@link Project#rootPath} does not exist
	 * @throws NoSuchProjectException   if the supplied <code>Project</code> does not exist (maybe its already deleted)
	 */
	boolean deleteProject(Project project, boolean deleteProjectFiles)
			throws IllegalArgumentException, SecurityException, IOException,
			NotADirectoryException, NoSuchProjectException;

	/**
	 * Get all LogEntrys from the supplied project
	 *
	 * @param project the <code>Project</code> to get the <code>LogEntry</code>s of
	 * @return a List of <code>LogEntry</code>s corresponding to this
	 *         <code>Project</code>
	 * @throws IllegalArgumentException if the supplied <code>Project</code> is null or does not
	 *                                  exist.
	 */
	List<LogEntry<? extends ILogable>> getLog(Project project)
			throws IllegalArgumentException;

	/**
	 * Gets all LogEntrys from the supplied <code>JakeObject</code>
	 *
	 * @param jakeObject the JakeObject to get the LogEntrys for
	 * @return a List of LogEntrys
	 * @throws IllegalArgumentException if the supplied JakeObject is null
	 */
	List<LogEntry<? extends ILogable>> getLog(JakeObject jakeObject)
			throws IllegalArgumentException;


	/**
	 * Sets the level of trust we have to the specified user. If the user does
	 * not exist yet in the <code>Project</code> the user is invited.
	 *
	 * @param project The <code>Project</code> to apply the new level of trust to.
	 * @param user	The user whose trustlevel gets changed.
	 * @param trust   The new level of trust for the specified user.
	 * @throws IllegalArgumentException if project or userId are null
	 * @throws IllegalAccessException   if the project has no userId set yet.
	 */
	void setTrust(Project project, User user, TrustState trust)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * Convinience method for getting the <code>IFSService</code> of a certain project.
	 *
	 * @param project the <code>Project</code> in question
	 * @return an <code>IFSService</code> or null, if the <code>Project</code> is not started
	 */
	IFSService getFileServices(Project project);

	/**
	 * Get the number of <code>FileObject</code> in the given <code>Project</code>
	 *
	 * @param project the <code>Project</code> for which to count the files
	 * @return The number of files in a Project
	 * @throws NoSuchProjectException if the <code>Project</code> given does not exist
	 * @throws FileNotFoundException  if the rootPath of the <code>Project</code> does not exist
	 * @see IProjectsManagingService#getAllProjectFiles(Project)
	 */
	AvailableLaterObject<Integer> getProjectFileCount(Project project)
			throws NoSuchProjectException, FileNotFoundException;

	/**
	 * Returns the cummulated filesize of all <code>FileObject</code>s of a given <code>Project</code>
	 *
	 * @param project the <code>Project</code> in question
	 * @return The amount of bytes all files in a <code>Project</code> take up.
	 * @throws NoSuchProjectException   if the given <code>Project</code> doesn't exist
	 * @throws FileNotFoundException	If the root path of the specified Project is not found.
	 * @throws IllegalArgumentException If project is null.
	 * @see #getAllProjectFiles(Project)
	 */
	AvailableLaterObject<Long> getProjectSizeTotal(Project project)
			throws NoSuchProjectException, FileNotFoundException, IllegalArgumentException;

	/**
	 * Retrieves all Files that exist (or existed) in a <code>Project</code>
	 *
	 * @param project the <code>Project</code> for which to get the <code>FileObject</code>s
	 * @return A <code>Collection</code> of all <code>FileObject</code>s.
	 * @throws NoSuchProjectException   If the specified Project does not exist.
	 * @throws FileNotFoundException	If the root path of the specified Project is not found.
	 * @throws IllegalArgumentException If project is null.
	 */
	AvailableLaterObject<Collection<FileObject>> getAllProjectFiles(Project project)
			throws NoSuchProjectException, FileNotFoundException, IllegalArgumentException;

	/**
	 * Retrieves all Notes that exist (or existed) in a <code>Project</code>
	 *
	 * @param project the <code>Project</code> for which to get the <code>NoteObject</code>s
	 * @return A List of all files.
	 * @throws NoSuchProjectException   If the specified Project does not exist.
	 * @throws IllegalArgumentException If project is null.
	 */
	AvailableLaterObject<Collection<NoteObject>> getAllProjectNotes(Project project)
			throws NoSuchProjectException, IllegalArgumentException;

	/**
	 * This method accepts an <code>Invitation</code>, adds the <code>Project</code> belonging to the
	 * <code>Invitation</code> to the database and informs the inviter that we accepted the invitation.
	 *
	 * @param invitation the <code>Invitation</code> we're accepting
	 * @param rootPath   a <code>File</code> pointing to the {@link Project#rootPath} of the <code>Project</code>
	 *                   we want to create.
	 * @throws IllegalStateException  if <code>project</code> is not an invitation.
	 * @throws NoSuchProjectException if <code>project</code> does not exist.
	 */
	void acceptInvitation(Invitation invitation, File rootPath)
			throws IllegalStateException, NoSuchProjectException;


	/**
	 * Rejects joining of a invited project and notifies the inviter.
	 *
	 * @param invitation the <code>Invitation</code> which was rejected.
	 * @throws IllegalStateException  if <code>project</code> is not an invitation.
	 * @throws NoSuchProjectException if <code>project</code> does not exist.
	 */
	public void rejectInvitation(Invitation invitation)
			throws IllegalStateException, NoSuchProjectException;

	/**
	 * Sets the project a new name and persists it.
	 *
	 * @param project The {@link Project}t to rename.
	 * @param newName the new name of the project.
	 * @throws NoSuchProjectException if <code>project</code> does not exist.
	 */
	void updateProjectName(Project project, String newName)
			throws NoSuchProjectException;

	/**
	 * Returns the <code>Date</code> of the last modification for a given <code>JakeObject</code>
	 *
	 * @param jakeObject the <code>JakeObject</code> in question
	 * @return The date and time when a {@link JakeObject} was last modified. If the JakeObject
	 *         has never been modified, the current Date is returned.
	 * @throws NoSuchProjectException   if the JakeObject's Project does not exist.
	 * @throws IllegalArgumentException If the JakeObject does not exist/is null.
	 * @deprecated
	 */
	@Deprecated
	Date getLastEdit(JakeObject jakeObject)
			throws NoSuchProjectException, IllegalArgumentException;


	/**
	 * Get the <code>User</code> who last edited the <code>JakeObject</code>.
	 * TODO: Redmine Issue #94
	 *
	 * @param jakeObject the <code>JakeObject</code> in question
	 * @return The <code>User</code> who last modified the <code>JakeObject</code>. If the <code>JakeObject</code>
	 *         has never been modified, null is returned.
	 * @throws NoSuchProjectException   if the <code>JakeObject</code>s <code>Project</code> does not exist.
	 * @throws IllegalArgumentException If the <code>JakeObject</code> does not exist/is null.
	 * @deprecated
	 */
	@Deprecated
	User getLastEditor(JakeObject jakeObject) throws NoSuchProjectException,
			IllegalArgumentException;

	/**
	 * @param project The <code>Project</code> that <code>Fileobject</code> should be located in.
	 * @param relpath The path where to look for a <code>FileObject</code>
	 * @return A <code>FileObject</code> at the given relative path
	 * @throws NoSuchJakeObjectException If no such <code>FileObject</code> exists.
	 */
	FileObject getFileObjectByRelPath(Project project, String relpath)
			throws NoSuchJakeObjectException;


	/**
	 * Returns all Users that are stored for a project.
	 *
	 * @param project the <code>Project</code> for which we want to collect all <code>User</code>s
	 * @return a <code>List</code> of <code>User</code>s belonging to this <code>Project</code>
	 * @throws NoSuchProjectException If <code>project</code> does
	 *                                not exist or is null.
	 */
	List<User> getProjectUsers(Project project) throws NoSuchProjectException;


	/**
	 * Returns all Users (in UserInfo's) that are stored for a project.
	 * This <code>List</code> cannot be empty because at least the current working <code>User</code> is contained
	 * within.
	 *
	 * @param project the <code>Project</code> for which we want <code>UserInfo</code>s
	 * @return a (not empty) <code>List</code> of <code>UserInfo</code>s for a given <code>Project</code>
	 * @throws NoSuchProjectException If the given <code>Project</code> does not exist or is null.
	 */
	List<UserInfo> getProjectUserInfos(Project project) throws NoSuchProjectException;


	/**
	 * Return a <code>UserInfo</code> for a specific <code>User</code>.
	 *
	 * @param project the <code>Project</code> this <code>User</code> belongs to.
	 * @param user	the <code>User</code> in question
	 * @return a <code>UserInfo</code>
	 */
	public UserInfo getProjectUserInfo(Project project, User user);


	/**
	 * Returns the <code>User</code> who last modified a specific <code>JakeObject</code>.
	 *
	 * @param jakeObject the <code>JakeObject</code> in question
	 * @return The <code>User</code> who last modified the <code>JakeObject</code> (according to the log)
	 */
	User getLastModifier(JakeObject jakeObject);


	/**
	 * Adds a person as a <code>User</code> for <code>Project</code> and sends an
	 * <code>Invitation</code> request to that person.
	 *
	 * @param project the <code>Project</code> to add the <code>User</code> for
	 * @param userid  The UserId (nick+protocol) of the other user.
	 * @return The new {@link com.jakeapp.core.domain.User}
	 * @throws com.jakeapp.core.domain.exceptions.UserFormatException
	 *          if the userid did not have the correct format
	 */
	User invite(Project project, String userid)
			throws UserFormatException;

	/**
	 * Returns a <code>List</code> with possible candidates for an <code>Invitation</code>. Only <code>User</code>s
	 * which are not already <code>User</code>s in the <code>Project</code> get returned.
	 *
	 * @param project the <code>Project</code> for which possible <code>Invitation</code>s should be provided
	 * @return A list of people who are 'friends' irt. the Project's
	 *         {@link MsgService}, are online and capable of running Projects
	 *         but are currently not attached to <code>project</code>.
	 * @throws NoSuchProjectException project is null or has no MsgService set.
	 */
	List<User> getSuggestedPeopleForInvite(Project project) throws NoSuchProjectException;


	/**
	 * Retrieves all {@link Tag}s for a JakeObject
	 *
	 * @param jakeObject The {@link JakeObject} to get {@link Tag}s for.
	 * @return The empty set if there are not {@link Tag}s for the specified {@link JakeObject},
	 *         or a {@link Set} of {@link Tag}s otherwise.
	 * @throws NoSuchJakeObjectException if the given <code>JakeObject</code> does not exist.
	 */
	Set<Tag> getTagsForJakeObject(JakeObject jakeObject) throws NoSuchJakeObjectException;


	/**
	 * Sets the {@link Tag}s for a {@link JakeObject}.
	 *
	 * @param jo   The {@link JakeObject} to set {@link Tag}s for.
	 * @param tags A {@link Set} of tags, may be empty. After this operation, the {@link JakeObject}
	 *             will have only the {@link Tag}s specified in <code>tags</code> as {@link Tag}s.
	 * @throws NoSuchJakeObjectException if the given <code>JakeObject</code> does not exist and therefor
	 *                                   has no <code>Tag</code>s
	 */
	void setTagsForJakeObject(JakeObject jo, Set<Tag> tags) throws NoSuchJakeObjectException;


	/**
	 * Saves a new version of a <code>NoteObject</code> to the database, but does not announce it
	 *
	 * @param note the <code>NoteObject</code> to be saved
	 */
	void saveNote(NoteObject note);

	/**
	 * Deletes a <code>NoteObject</code> to the database, but does not announce the deletion
	 *
	 * @param note the <code>NoteObject</code> to be deleted
	 * @throws NoSuchJakeObjectException if the given <code>JakeObject</code> does not exist
	 */
	void deleteNote(NoteObject note) throws NoSuchJakeObjectException;

	/**
	 * Deletes a <code>FileObject</code>, but does not announce the deletion
	 *
	 * @param fileObject the <code>FileObject</code> to be deleted
	 * @param trash	  a <code>boolean</code> specifying if the <code>FileObject</code> should be
	 *                   moved to the <code>Trash</code>
	 * @return an <code>AvailableLaterObject</code> Object as callback.
	 * @throws NoSuchJakeObjectException if the given <code>FileObject</code> does not exist
	 */
	AvailableLaterObject<Void> deleteFile(FileObject fileObject, boolean trash) throws NoSuchJakeObjectException;

	/**
	 * @param fileObjects a <code>List</code> of <code>FileObject</code>s
	 * @param trash	   a <code>boolean</code> indicating if the <code>FileObject</code>s should be
	 *                    moved to the <code>trash</code>
	 * @return an <code>AvailableLaterObject&lt;Integer&gt;</code> indicating number of deleted files
	 */
	AvailableLaterObject<Integer> deleteFiles(List<FileObject> fileObjects, boolean trash);

	/**
	 * Lock a specific <code>JakeObject</code> with a given <code>comment</code>
	 *
	 * @param jakeObject the <code>JakeObject</code> to be locked.
	 * @param comment	A comment (e.g. the purpose of the lock), can be null
	 */
	void lock(JakeObject jakeObject, String comment);

	/**
	 * Remove the lock of a specific <code>JakeObject</code> with a given <code>comment</code>
	 *
	 * @param jakeObject the <code>JakeObject</code> for which the Lock should be removed
	 * @param comment	A comment (e.g. why the Lock was removed), can be null
	 */
	void unlock(JakeObject jakeObject, String comment);

	/**
	 * Set the Nickname of a <code>User</code> participating in a given <code>Project</code>
	 *
	 * @param project the <code>Project</code> in which the <code>User</code> is participating
	 * @param user	the <code>User</code> for which the nickname should be set.
	 * @param nick	a <code>String</code> specifying what the new Nickname should be.
	 */
	void setUserNickname(Project project, User user, String nick);


}
