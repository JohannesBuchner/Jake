package com.jakeapp.core.dao;

import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.dao.exceptions.NoSuchServiceCredentialsException;

import java.util.UUID;
import java.util.List;


// FIXME: is there a difference between "ServiceCredentials" and "Accounts"
// If there is none why have two names. If there is one, make it explicit.
/**
 * DAO Interface for service credentials (aka <code>Account</code>).
 */
public interface IAccountDao {

	// FIXME what are the requirements for credentials to be valid???
	// see InvalidCredentialsException
	/**
	 * Puts new ServiceCredentials into the database.
	 *
	 * @param credentials The ServiceCredentials to insert
	 * @return the inserted ServiceCredentials
	 * @throws InvalidCredentialsException if the credentials given aren't correct
	 */
	public Account create(Account credentials) throws InvalidCredentialsException;


	/**
	 * Returns the ServiceCredentials that correspond the given uuid.
	 *
	 * @param uuid the UUID of the <code>Account</code> that is to be fetched.
	 * @return the ServiceCredentials for the requested user
	 * @throws NoSuchServiceCredentialsException
	 *          if no credentials exist for the given <code>uuid</code>.
	 */
	public Account read(UUID uuid) throws NoSuchServiceCredentialsException;

	/**
	 * Get a <code>List</code> of all <code>Account</code>s.
	 * 
	 * @return a <code>List</code> of all <code>Account</code>s. The
	 * <code>List</code> is empty if there are no <code>Account</code>s available.
	 */
	public List<Account> getAll();


	/**
	 * Updates the given ServiceCredentials.
	 *
	 * @param credentials the ServiceCredentials to be updated
	 * @return the updated ServiceCredentials
	 * @throws NoSuchServiceCredentialsException
	 *          if no credentials with a matching uuid of the given 
	 *          <code>credentials</code> where found in the database
	 */
	public Account update(Account credentials) throws NoSuchServiceCredentialsException;


	/**
	 * Deletes the specified ServiceCredentials.
	 *
	 * @param credentials the ServiceCredentials to be deleted.
	 * @throws NoSuchServiceCredentialsException
	 *          raised if no credentials with a matching uuid can be found.
	 */
	public void delete(Account credentials) throws NoSuchServiceCredentialsException;
}
