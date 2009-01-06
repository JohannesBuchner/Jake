package com.jakeapp.core.dao;

import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.InvalidUserIdException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.dao.exceptions.NoSuchUserIdException;

import java.util.List;
import java.util.UUID;

/**
 * Interface for working with <code>UserId</code> objects.
 *
 * @author domdorn
 */
public interface IUserIdDao {


    /**
     * Creates a <code>UserId</code>. If the
     * <code>UserId</code> does
     * not yet exist in the database, a new entry is created.
     * Otherwise an existing entry is updated.
     *
     * @param user the <code>UserId</code> to be persisted
     * @return the <code>UserId</code> that has been persisted
     * @throws InvalidUserIdException if the suppllied UserId is invalid
     */
    public UserId create(final UserId user) throws InvalidUserIdException;


    public UserId get(final UserId user) throws InvalidUserIdException, NoSuchUserIdException;


    /**
     * Gets a User by the given UUID
     *
     * @param uuid the UUID given
     * @return a UserId-Object
     * @throws InvalidUserIdException
     * @throws NoSuchUserIdException
     */
    public UserId get(final UUID uuid) throws InvalidUserIdException, NoSuchUserIdException;

    /**
     * Get all Users by this ServiceCredentials
     *
     * @param credentials The ServiceCredentials from which to load the users.
     * @return a list of users
     * @throws InvalidCredentialsException if the supplied credentials are invalid
     */
    public List<UserId> getAll(final ServiceCredentials credentials) throws InvalidCredentialsException;


    /**
     * Updates the given userId in the database.
     *
     * @param userId the UserId to be updated
     * @return the updated userId
     * @throws NoSuchUserIdException If the supplied UserId (uuid + protocol) are not present in the database
     */
    public UserId update(final UserId userId) throws NoSuchUserIdException;

    /**
     * Delete a <code>UserId</code>
     *
     * @param user the <code>UserId</code> to be deleted
     * @throws NoSuchUserIdException if the supplied user does not exist in the database
     */
    public void delete(final UserId user) throws NoSuchUserIdException;

    /**
     * Delete a <code>UserId</code>
     * @param user the <code>UserId</code> to be deleted
     * @throws NoSuchUserIdException if the supplied user does not exist in the database
     */
    public void delete(final UUID user) throws NoSuchUserIdException;
}
