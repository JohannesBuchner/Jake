package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;

import java.util.List;
import java.util.UUID;

/**
 * This interface describes the DAO to load and persist Projects from
 * and to the database.
 */

public interface IProjectDao {

    /**
     * Create (persist) this Project into the database.
     * @param project The Project to be persisted
     * @return the persisted project.
     * @throws com.jakeapp.core.domain.exceptions.InvalidProjectException if the supplied Project is invalid
     */
    public Project create(Project project) throws InvalidProjectException;

    /**
     * Retrieve a specific Project from the database.
     * @param uuid The UUID of the project to be received
     * @return The Project found
     * @throws NoSuchProjectException If no project by that uuid exists
     */
    public Project read(UUID uuid) throws NoSuchProjectException;

	/**
	 * Persist a <code>Project</code>.
	 * @param project the <code>Project</code> to be persisted.
	 * @return the persisted <code>Project</code>.
     * @throws NoSuchProjectException If no project with that uuid exists to be updated
	 */
    public Project update(Project project) throws NoSuchProjectException;

    /**
     * Get all projects that are stored in Jake's global database.
     * @return all projects.
     */
    List<Project> getAll();

    /**
     * Delete a project from Jake's global database.
     * @param project the project to be deleted.
     * @throws NoSuchProjectException if project does not exist.
     */
    public void delete(Project project) throws NoSuchProjectException;

}
