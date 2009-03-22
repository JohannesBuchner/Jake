package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;

import java.util.List;
import java.util.UUID;

/**
 * This interface describes the DAO to load and persist <code>Project</code>s from
 * and to the database.
 */

public interface IProjectDao {

	/**
	 * Create (persist) this <code>Project</code> into the database.
	 *
	 * @param project The <code>Project</code> to be persisted
	 * @return the persisted <code>Project</code>.
	 * @throws com.jakeapp.core.domain.exceptions.InvalidProjectException
	 *          if the supplied <code>Project</code> is invalid
	 */
	Project create(Project project) throws InvalidProjectException;

	/**
	 * Retrieve a specific <code>Project</code> from the database.
	 *
	 * @param uuid The UUID of the <code>Project</code> to be received
	 * @return The <code>Project</code> found
	 * @throws NoSuchProjectException If no <code>Project</code> by that uuid exists
	 */
	Project read(UUID uuid) throws NoSuchProjectException;

	/**
	 * Persist a <code>Project</code>.
	 *
	 * @param project the <code>Project</code> to be persisted.
	 * @return the persisted <code>Project</code>.
	 * @throws NoSuchProjectException If no <code>Project</code> with that uuid exists to be updated
	 */
	Project update(Project project) throws NoSuchProjectException;

	/**
	 * Get all <code>Project</code>s that are stored in Jake's global database.
	 *
	 * @return all <code>Project</code>s, empty <code>List</code> if none are there.
	 */
	List<Project> getAll();

	/**
	 * Returns a <code>List</code> containing all <code>Project</code>s belonging to an specific <code>Account</code>
	 *
	 * @param account the <code>Account</code> in question
	 * @return a (empty) <code>List</code> of <code>Project</code>s
	 */
	List<Project> getAll(Account account);

	/**
	 * Get all projects with a certain invitation state
	 *
	 * @param state
	 * @return
	 * @deprecated
	 */
	@Deprecated
	List<Project> getAll(InvitationState state);

	/**
	 * Delete a <code>Project</code> from Jake's global database.
	 *
	 * @param project the <code>Project</code> to be deleted.
	 * @throws NoSuchProjectException if <code>Project</code> does not exist.
	 */
	void delete(Project project) throws NoSuchProjectException;

}
