package com.jakeapp.core.dao;

import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.InvalidUserIdException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.dao.exceptions.NoSuchUserIdException;

import java.util.List;

/**
 * Interface for working with <code>UserId</code> objects.
 * @author domdorn
 */
public interface IUserIdDao {


	/**
	 * Persists a <code>UserId</code>. If the
     * <code>UserId</code> does
	 * not yet exist in the database, a new entry is created.
	 * Otherwise an existing entry is updated.
	 * @param user the <code>UserId</code> to be persisted
	 * @return the <code>UserId</code> that has been persisted
     * @throws InvalidUserIdException if the suppllied UserId is invalid
	 */
    public UserId persist(final UserId user) throws InvalidUserIdException;

    /**
     * Get all Users by this ServiceCredentials
     * @param credentials The ServiceCredentials from which to load the users.
     * @return a list of users
     * @throws InvalidCredentialsException if the supplied credentials are invalid
     */
    public List<UserId> getAll(final ServiceCredentials credentials) throws InvalidCredentialsException;


    /**
     * Make a given <code>ProjectMember</code> transient of a specific project
     *  by removing
     * the project member from the database.
     * @param user the <code>UserId</code> to be deleted
     * @throws NoSuchUserIdException if the supplied user does not exist in the database
     */
    public void delete(final UserId user) throws NoSuchUserIdException;
}
