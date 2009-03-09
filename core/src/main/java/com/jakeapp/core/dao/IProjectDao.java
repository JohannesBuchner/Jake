package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.InvitationState;
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
     * @throws com.jakeapp.core.domain.exceptions.InvalidProjectException
     * if the supplied Project is invalid
     */
    Project create(Project project) throws InvalidProjectException;

    /**
     * Retrieve a specific Project from the database.
     * @param uuid The UUID of the project to be received
     * @return The Project found
     * @throws NoSuchProjectException If no project by that uuid exists
     */
    Project read(UUID uuid) throws NoSuchProjectException;

	/**
	 * Persist a <code>Project</code>.
	 * @param project the <code>Project</code> to be persisted.
	 * @return the persisted <code>Project</code>.
     * @throws NoSuchProjectException If no project with that uuid exists to be updated
	 */
    Project update(Project project) throws NoSuchProjectException;

    /**
     * Get all projects that are stored in Jake's global database.
     * @return all projects.
     */
    List<Project> getAll();
    
    /**
     * @param account
     * @return all Projects that belong to the user specified via <code>account</code>.
     */
    List<Project> getAll(Account account);
    
    /**
     * Get all projects with a certain invitation state
		 * @param state
		 * @return
		 */
    List<Project> getAll(InvitationState state);

    /**
     * Delete a project from Jake's global database.
     * @param project the project to be deleted.
     * @throws NoSuchProjectException if project does not exist.
     */
    void delete(Project project) throws NoSuchProjectException;

}
