package com.jakeapp.core.services;

import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.TrustState;
import com.jakeapp.core.domain.UserId;

import java.util.List;
import java.util.Map;
import java.io.FileNotFoundException;


public interface IProjectService {

    /**
     * Get a list of all Projects known to jake
     *
     * @return a list of all known jake projects
     */
    public List<Project> getProjectList();

    /**
     * Creates a new <code>Project</code> given the supplied
     * name and rootPath
     *
     * @param name     the name the new <code>Project</code> should have
     * @param rootPath the Path to the rootFolder of this <code>Project</code>
     * @return the loaded instance of this <code>Project</code>
     * @throws FileNotFoundException    if the rootPath is invalid
     * @throws IllegalArgumentException if the supplied <code>name</code>
     *                                  is invalid
     */
    public Project createProject(String name, String rootPath)
            throws FileNotFoundException, IllegalArgumentException;


    /**
     * Start the given project (load database)
     *
     * @param project the <code>Project</code> to be loaded
     * @return true on success, false on error
     * @throws IllegalArgumentException if the supplied
     *                                  <code>Project</code> is null
     * @throws FileNotFoundException    if the rootPath of the
     *                                  <code>Project</code> does
     *                                  not exist anymore
     */
    public boolean startProject(Project project)
            throws IllegalArgumentException, FileNotFoundException;


    /**
     * Stops the given project (unloads the database, eventually
     * disconnects from the network)
     *
     * @param project the <code>Project</code> to be stopped.
     * @return true on success, false on error
     * @throws IllegalArgumentException if the supplied
     *                                  <code>Project</code> is null
     * @throws FileNotFoundException    if the rootPath of the
     *                                  <code>Project</code> does not exist
     *                                  anymore
     */
    public boolean stopProject(Project project)
            throws IllegalArgumentException, FileNotFoundException;
    
    /**
     * Loads the given project (load database)
     *
     * @param project the name of the project to be loaded.
     * @throws IllegalArgumentException if the supplied name is null
     * @throws FileNotFoundException    if the rootPath of the loaded
     *                                  <code>Project</code> does
     *                                  not exist anymore
     */
    public Project openProject(String project)
            throws IllegalArgumentException, FileNotFoundException;


    /**
     * Stops the given project and removes it from the list of projects.
     *
     * @param project the <code>Project</code> to be closed.
     * @throws IllegalArgumentException if the supplied
     *                                  <code>Project</code> is null
     * @throws FileNotFoundException    if the rootPath of the
     *                                  <code>Project</code> does not exist
     *                                  anymore
     */
    public void closeProject(Project project)
            throws IllegalArgumentException, FileNotFoundException;


    /**
     * @param project the <code>Project</code> to be deleted
     * @return true on success, false on error
     * @throws IllegalArgumentException if the supplied <code>Project</code>
     *                                  is null
     * @throws SecurityException        if the supplied <code>Project</code>
     *                                  could not be deleted due to filesystem
     *                                  permissons
     * @throws FileNotFoundException    if the rootFolder of the
     *                                  <code>Project</code> already got
     *                                  deleted. The project is removed from
     *                                  within jake, but the user should be
     *                                  informed that he should not
     *                                  manually delete projects.
     */
    public boolean deleteProject(Project project)
            throws IllegalArgumentException, SecurityException,
            FileNotFoundException;


    /**
     * get all log entries from all projects, grouped by Project
     *
     * @return a Map with the Project as key and a List
     *         of <code>LogEntry</code>s.
     */
    public Map<Project, List<LogEntry<? extends ILogable>>> getLog();

    /**
     * Get all LogEntrys from the supplied project
     *
     * @param project the <code>Project</code> to get the
     *                <code>LogEntry</code>s of
     * @return a List of <code>LogEntry</code>s corresponding
     *         to this <code>Project</code>
     * @throws IllegalArgumentException if the supplied
     *                                  <code>Project</code> is null
     */
    public List<LogEntry<? extends ILogable>> getLog(Project project)
            throws IllegalArgumentException;

    /**
     * Gets all LogEntrys from the supplied <code>JakeObject</code>
     *
     * @param jakeObject the JakeObject to get the LogEntrys for
     * @return a List of LogEntrys
     * @throws IllegalArgumentException if the supplied JakeObject is null
     */
    public List<LogEntry<? extends ILogable>> getLog(JakeObject jakeObject)
            throws IllegalArgumentException;


    /**
     * Assigns a UserId to a project if this project has no UserId set yet.
     *
     * @param project the Project to set the UserId
     * @param userId  the UserId to be set
     * @throws IllegalArgumentException if project or userId are null
     * @throws IllegalAccessException   if the project already has a userId set
     */
    public void assignUserToProject(Project project, UserId userId)
            throws IllegalArgumentException, IllegalAccessException;
    
    /**
     * Sets the level of trust we have to the specified user.
     * If the user does not exist yet in the <code>Project</code> the user is invited.
     * @param project The <code>Project</code> to apply the new level
     * of trust to.
     * @param userid The user whose trustlevel gets changed.
     * @param trust The new level of trust for the specified user.
     * @throws IllegalArgumentException if project or userId are null
     * @throws IllegalAccessException   if the project has no userId set yet.
     */
    public void setTrust(Project project,UserId userid,TrustState trust) throws IllegalArgumentException, IllegalAccessException;
}
