package com.jakeapp.core.services;

import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.core.synchronization.ISyncService;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.jake.fss.IFSService;

import java.util.List;
import java.util.Map;
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
	public List<Project> getProjectList();


	/**
	 * Get a list of all Projects known to Jake matching the given
	 * InvitationState
	 * 
	 * @param state
	 *            The invitationState requested
	 * @return a list of all known jake projects matching the given
	 *         InvitationState
	 */
	public List<Project> getProjectList(InvitationState state);

	/**
	 * Creates a new <code>Project</code> given the supplied name and rootPath
	 * 
	 * @param name
	 *            the name the new <code>Project</code> should have
	 * @param rootPath
	 *            the Path to the rootFolder of this <code>Project</code>. If
	 *            it does not yet exist, it is created.
	 * @param msgService
	 *            The MessageService this project should be assigned to. <b>THIS
	 *            CAN BE NULL!</b>
	 * @return the loaded instance of this <code>Project</code>
	 * @throws FileNotFoundException
	 *             if the rootPath is invalid
	 * @throws IllegalArgumentException
	 *             if the supplied <code>name</code> is invalid
	 */
	public Project createProject(String name, String rootPath, MsgService msgService)
			throws FileNotFoundException, IllegalArgumentException;


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
	public boolean startProject(Project project, ChangeListener cl)
			throws IllegalArgumentException, FileNotFoundException, ProjectException;


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
	public boolean stopProject(Project project) throws IllegalArgumentException,
			FileNotFoundException;

	/**
	 * Loads the given project (load database)
	 * 
	 * @param project
	 *            the name of the project to be loaded.
	 * @throws IllegalArgumentException
	 *             if the supplied name is null
	 * @throws FileNotFoundException
	 *             if the rootPath of the loaded <code>Project</code> does not
	 *             exist anymore
	 * @return the started Project
	 */
	public Project openProject(String project) throws IllegalArgumentException,
			FileNotFoundException;


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
	public void closeProject(Project project) throws IllegalArgumentException,
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
	public boolean deleteProject(Project project) throws IllegalArgumentException,
			SecurityException, FileNotFoundException;


	/**
	 * get all log entries from all projects, grouped by Project
	 * 
	 * @return a Map with the Project as key and a List of <code>LogEntry</code>
	 *         s.
	 */
	public Map<Project, List<LogEntry<? extends ILogable>>> getLog();

	/**
	 * Get all LogEntrys from the supplied project
	 * 
	 * @param project
	 *            the <code>Project</code> to get the <code>LogEntry</code>s of
	 * @return a List of <code>LogEntry</code>s corresponding to this
	 *         <code>Project</code>
	 * @throws IllegalArgumentException
	 *             if the supplied <code>Project</code> is null
	 */
	public List<LogEntry<? extends ILogable>> getLog(Project project)
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
	public List<LogEntry<? extends ILogable>> getLog(JakeObject jakeObject)
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
	public void assignUserToProject(Project project, UserId userId)
			throws IllegalArgumentException, IllegalAccessException;

	/**
	 * Sets the level of trust we have to the specified user. If the user does
	 * not exist yet in the <code>Project</code> the user is invited.
	 * 
	 * @param project
	 *            The <code>Project</code> to apply the new level of trust to.
	 * @param userid
	 *            The user whose trustlevel gets changed.
	 * @param trust
	 *            The new level of trust for the specified user.
	 * @throws IllegalArgumentException
	 *             if project or userId are null
	 * @throws IllegalAccessException
	 *             if the project has no userId set yet.
	 */
	public void setTrust(Project project, UserId userid, TrustState trust)
			throws IllegalArgumentException, IllegalAccessException;


	/**
	 * // TODO
	 * 
	 * @param project
	 * @param relPath
	 * @return
	 * @throws IllegalArgumentException
	 */
	public List<FileObject> getFiles(Project project, String relPath)
			throws IllegalArgumentException;


	/**
	 * // TODO
	 * 
	 * @param project
	 * @return
	 * @throws IllegalArgumentException
	 */
	public List<NoteObject> getNotes(Project project) throws IllegalArgumentException,
			ProjectNotLoadedException;
	
	/**
	 * Returns a service for file-operations 
	 * @param p
	 * @return
	 */
	IFSService getFileServices(Project p);

}
