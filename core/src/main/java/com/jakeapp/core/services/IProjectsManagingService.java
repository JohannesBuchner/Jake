package com.jakeapp.core.services;

import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.jake.fss.IFSService;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * This handles the list of projects and their states
 * 
 * TODO: add table here what kind of states a project can have.
 * 
 * @author dominik, ..., johannes
 * 
 */
public interface IProjectsManagingService {
	/**
	 * Get a list of all Projects known to jake
	 * 
	 * @return a list of all known jake projects
	 */
	List<Project> getProjectList();


	/**
	 * Get a list of all Projects known to Jake matching the given
	 * InvitationState
	 * 
	 * @param state
	 *            The invitationState requested
	 * @return a list of all known jake projects matching the given
	 *         InvitationState
	 */
	List<Project> getProjectList(InvitationState state);

	/**
	 * Creates a new <code>Project</code> given the supplied name and rootPath
	 * 
	 * @param name
	 *            the name the new <code>Project</code> should have
	 * @param rootPath
	 *            the Path to the rootFolder of this <code>Project</code>. If it
	 *            does not yet exist, it is created.
	 * @param msgService
	 *            The MessageService this project should be assigned to. <b>THIS
	 *            CAN BE NULL!</b>
	 * @return the loaded instance of this <code>Project</code>
	 * @throws FileNotFoundException
	 *             if the rootPath is invalid
	 * @throws IllegalArgumentException
	 *             if the supplied <code>name</code> is invalid
	 */
	Project createProject(String name, String rootPath,
			MsgService msgService) throws FileNotFoundException,
			IllegalArgumentException;


	/**
	 * Start the given project (load database)
	 * 
	 * @param project
	 *            the <code>Project</code> to be loaded
	 * @param cl
	 *            get notified for changes
	 * @return true on success, false on error
	 * @throws IllegalArgumentException
	 *             if the supplied <code>Project</code> is null
	 * @throws FileNotFoundException
	 *             if the rootPath of the <code>Project</code> does not exist
	 *             anymore
	 * @throws ProjectException
	 *             couldn't start the project for another reason (algorithms
	 *             missing, desktop not supported by java, etc.)
	 */
	boolean startProject(Project project, ChangeListener cl)
			throws IllegalArgumentException, FileNotFoundException,
			ProjectException;


	/**
	 * Stops the given project (unloads the database, eventually disconnects
	 * from the network)
	 * 
	 * @param project
	 *            the <code>Project</code> to be stopped.
	 * @return true on success, false on error
	 * @throws IllegalArgumentException
	 *             if the supplied <code>Project</code> is null
	 * @throws FileNotFoundException
	 *             if the rootPath of the <code>Project</code> does not exist
	 *             anymore
	 */
	boolean stopProject(Project project)
			throws IllegalArgumentException, FileNotFoundException;

	/**
	 * Loads the given project (load database)
	 * 
	 * @param rootPath
	 *            The location where the project is. It must be a folder and it
	 *            must contain a database file describing the Jake-Project.
	 * @param name
	 *            Name of the Project
	 * @throws IllegalArgumentException
	 *             if the supplied name is null
	 * @throws FileNotFoundException
	 *             if the rootPath of the loaded <code>Project</code> does not
	 *             exist anymore or it is not a readable directory or the
	 *             database- file cannot be opened.
	 * @return the opened, but not yet started, <code>Project</code>
	 */
	Project openProject(File rootPath, String name)
			throws IllegalArgumentException, FileNotFoundException;


	/**
	 * Stops the given project and removes it from the list of projects.
	 * 
	 * @param project
	 *            the <code>Project</code> to be closed.
	 * @throws IllegalArgumentException
	 *             if the supplied <code>Project</code> is null
	 * @throws FileNotFoundException
	 *             if the rootPath of the <code>Project</code> does not exist
	 *             anymore
	 */
	void closeProject(Project project) throws IllegalArgumentException,
			FileNotFoundException;


	/**
	 * @param project
	 *            the <code>Project</code> to be deleted
	 * @return true on success, false on error
	 * @throws IllegalArgumentException
	 *             if the supplied <code>Project</code> is null
	 * @throws SecurityException
	 *             if the supplied <code>Project</code> could not be deleted due
	 *             to filesystem permissons
	 * @throws FileNotFoundException
	 *             if the rootFolder of the <code>Project</code> already got
	 *             deleted. The project is removed from within jake, but the
	 *             user should be informed that he should not manually delete
	 *             projects.
	 */
	boolean deleteProject(Project project)
			throws IllegalArgumentException, SecurityException,
			FileNotFoundException;

	/**
	 * Get all LogEntrys from the supplied project
	 * 
	 * @param project
	 *            the <code>Project</code> to get the <code>LogEntry</code>s of
	 * @return a List of <code>LogEntry</code>s corresponding to this
	 *         <code>Project</code>
	 * @throws IllegalArgumentException
	 *             if the supplied <code>Project</code> is null or does not
	 *             exist.
	 */
	List<LogEntry<? extends ILogable>> getLog(Project project)
			throws IllegalArgumentException;

	/**
	 * Gets all LogEntrys from the supplied <code>JakeObject</code>
	 * 
	 * @param jakeObject
	 *            the JakeObject to get the LogEntrys for
	 * @return a List of LogEntrys
	 * @throws IllegalArgumentException
	 *             if the supplied JakeObject is null
	 */
	List<LogEntry<? extends ILogable>> getLog(JakeObject jakeObject)
			throws IllegalArgumentException;


	/**
	 * Assigns a UserId to a project if this project has no UserId set yet.
	 * 
	 * @param project
	 *            the Project to set the UserId
	 * @param userId
	 *            the UserId to be set
	 * @throws IllegalArgumentException
	 *             if project or userId are null
	 * @throws IllegalAccessException
	 *             if the project already has a userId set
	 */
	void assignUserToProject(Project project, UserId userId)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * Sets the level of trust we have to the specified user. If the user does
	 * not exist yet in the <code>Project</code> the user is invited.
	 * 
	 * @param project
	 *            The <code>Project</code> to apply the new level of trust to.
	 * @param userId
	 *            The user whose trustlevel gets changed.
	 * @param trust
	 *            The new level of trust for the specified user.
	 * @throws IllegalArgumentException
	 *             if project or userId are null
	 * @throws IllegalAccessException
	 *             if the project has no userId set yet.
	 */
	void setTrust(Project project, UserId userId, TrustState trust)
			throws IllegalArgumentException, IllegalAccessException;


	/**
	 * Retrieves all Files of a Project that are immediate children of the
	 * specified relPath. If the relpath is empty all files in the Project's
	 * root folder are returned.
	 * 
	 * @param project
	 * @param relPath
	 * @return The fileObjects that belong are found in the specified folder.
	 * @throws IllegalArgumentException
	 *             if project or relpath are null
	 * @throws FileNotFoundException
	 *             if relPath does not point to a directory
	 */
	// FIXME is never called - do we need this??
	// public List<FileObject> getFiles(Project project, String relPath)
	// throws IllegalArgumentException, FileNotFoundException;

	/**
	 * Retrieves all Notes for a Project
	 * 
	 * @param project
	 *            The Project to retrieve all notes for
	 * @return all Notes
	 * @throws IllegalArgumentException
	 *             if <code>project</code> is null.
	 * @throws ProjectNotLoadedException
	 *             if the project is not open.
	 */
	List<NoteObject> getNotes(Project project)
			throws IllegalArgumentException, ProjectNotLoadedException;

	/**
	 * Returns a service for file-operations
	 * 
	 * @param p
	 * @return
	 */
	IFSService getFileServices(Project p);

	/**
	 * @return The number of files in a Project
	 * @see #getAllProjectFiles(Project)
	 */
	int getProjectFileCount(Project project) throws NoSuchProjectException, FileNotFoundException,IllegalArgumentException;

	/**
	 * @param project
	 * @return The amount of bytes all files in a project take up.
	 * @see #getAllProjectFiles(Project)
	 */
	long getProjectSizeTotal(Project project)
	throws NoSuchProjectException, FileNotFoundException,IllegalArgumentException;


	/**
	 * Retrieves all Files that exist in a <code>Project</code>
	 * 
	 * @param project
	 * @return A List of all files.
	 * @throws NoSuchProjectException If the specified Project does not exist.
	 * @throws FileNotFoundException If the root path of the specified Project is not found.
	 * @throws IllegalArgumentException If project is null.
	 */
	List<FileObject> getAllProjectFiles(Project project)
		throws NoSuchProjectException, FileNotFoundException,IllegalArgumentException;
	
	/**
	 * Joins the Project and notifies the inviter.
	 * @param project The project to join.
	 * @param inviter The user that invited us to join the project.
	 * 	She is notified that we accepted joining the project.
	 * @throws IllegalStateException if <code>project</code> is not an invitation. 
	 * @throws NoSuchProjectException if <code>project</code> does not exist.
	 */
	void joinProject(Project project,UserId inviter)
		throws IllegalStateException,NoSuchProjectException;


	/**
	 * Rejects joining of a invited project and notifies the inviter.
	 * @param project The project not to join.
	 * @param inviter The user that invited us to join the project.
	 * 	She is notified that we rejected joining the project.
	 * @throws IllegalStateException if <code>project</code> is not an invitation. 
	 * @throws NoSuchProjectException if <code>project</code> does not exist.
	 */
	void rejectProject(Project project, UserId inviter)
		throws IllegalStateException,NoSuchProjectException;
	
	/**
	 * Sets the project a new name and persists it.
	 * @param project The {@link Project}t to rename.
	 * @param newName the new name of the project.
	 * @throws NoSuchProjectException if <code>project</code> does not exist.
	 */
	void updateProjectName(Project project, String newName)
		throws NoSuchProjectException;
}