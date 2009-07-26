package com.jakeapp.core.dao;

import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.dao.exceptions.NoSuchServiceCredentialsException;

import java.util.UUID;
import java.util.List;
import java.sql.SQLException;

/**
 * DAO Interface for User Credentials.
 */
public interface IAccountDao {

	/**
	 * Puts new ServiceCredentials into the database.
	 *
	 * @param credentials The ServiceCredentials to insert
	 * @return the inserted ServiceCredentials
	 * @throws InvalidCredentialsException if the credentials given aren't correct
	 */
	public Account create(Account credentials) throws InvalidCredentialsException;


	/**
	 * Returns the ServiceCredentials by the given uuid.
	 *
	 * @param uuid the UUID of the user requested
	 * @return the ServiceCredentials for the requested user
	 * @throws NoSuchServiceCredentialsException
	 *          if no credentials by the given UUID exist
	 */
	public Account read(UUID uuid) throws NoSuchServiceCredentialsException;

	/**
	 * Get a <code>List</code> with all <code>Account</code>s.
	 * @return a <code>List</code> with all <code>Account</code>s.
	 * The <code>List</code> is empty if there are no <code>Account</code>s available.
	 */
	public List<Account> getAll();


	/**
	 * Updates the given ServiceCredentials.
	 *
	 * @param credentials the ServiceCredentials to be updated
	 * @return the updated ServiceCredentials
	 * @throws NoSuchServiceCredentialsException
	 *          if no credentials by the uuid of this credentials where in the
	 *          database
	 */
	public Account update(Account credentials) throws NoSuchServiceCredentialsException;


	/**
	 * Deletes the specified ServiceCredentials.
	 *
	 * @param credentials the ServiceCredentials to be deleted.
	 * @throws NoSuchServiceCredentialsException
	 *          if no credentials by the uuid of this credentials where in the
	 *          database
	 */
	public void delete(Account credentials) throws NoSuchServiceCredentialsException;


}
