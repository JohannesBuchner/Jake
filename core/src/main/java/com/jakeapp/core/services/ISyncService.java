package com.jakeapp.core.services;

import java.util.List;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;

public interface ISyncService {

	/**
	 * Tries a logSync with each user of the supplied <code>Project</code>
	 * 
	 * @param project
	 *            The <code>Project</code> to do the logSync
	 * @throws IllegalArgumentException
	 *             if the supplied <code>Project</code> object is null or
	 *             invalid
	 */
	public void startLogSync(Project project) throws IllegalArgumentException;

	/**
	 * Tries to logSync from one specified user of the Project
	 * 
	 * @param project
	 * @param userId
	 * @throws IllegalArgumentException
	 *             if the supplied project or userId is null
	 * @throws IllegalProtocolException
	 *             if the supplied UserId is of the wrong protocol-type
	 */
	public void startLogSync(Project project, UserId userId)
			throws IllegalArgumentException, IllegalProtocolException;

	/**
	 * Starts the pulling of all remaining files of the supplied project
	 * 
	 * @param project
	 *            The Project to get the files for
	 * @throws IllegalArgumentException
	 *             if the Project is null or invalid
	 */
	public void pullObjects(Project project) throws IllegalArgumentException;

	/**
	 * Starts the pulling of all the supplied FileObjects if pulling is
	 * necessary and possible
	 * 
	 * @param objects
	 *            the objects to be pulled
	 */
	public void pullObjects(List<JakeObject> objects);


	/**
	 * Tries to start the pushing of all remaining files of the given Project
	 * 
	 * @param project
	 *            the project whichs objects should be pushed
	 * @throws IllegalArgumentException
	 */
	public void pushObjects(Project project) throws IllegalArgumentException;


	/**
	 * Tries to start the pushing of all supplied FileObjects if available
	 * 
	 * @param objects
	 *            the objects to be pushed
	 */
	public void pushObjects(List<JakeObject> objects);


	/**
	 * Gets a list of all objects that changed since the last local change //
	 * TODO wirklich so??
	 * 
	 * @param project
	 *            the Project from which the changed objects should be shown
	 * @return a List of changed JakeObjects
	 * @throws IllegalArgumentException
	 *             if the supplied Project is null or invalid
	 */
	public List<JakeObject> getChangedObjects(Project project)
			throws IllegalArgumentException;


	/**
	 * Gets a list of all objects that are currently out of sync
	 * 
	 * @param project
	 *            the Project from which the changed objects should be shown
	 * @return a list of all out-of-sync JakeObjects
	 * @throws IllegalArgumentException
	 *             if the supplied Project is null or invalid
	 */
	public List<JakeObject> getOutOfSyncObjects(Project project)
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
	public boolean isObjectLocked(JakeObject object)
			throws IllegalArgumentException;


	/**
	 * Tries to set the (soft-)lock of the supplied JakeObject
	 * 
	 * @param object
	 *            the JakeObject the lock should be set
	 * @throws IllegalArgumentException
	 *             if the supplied JakeObject is null or invalid
	 * @throws ProjectNotLoadedException
	 *             if the project corresponding to this object is not loaded
	 *             currently
	 */
	public void setObjectLocked(JakeObject object)
			throws IllegalArgumentException, ProjectNotLoadedException;

}
