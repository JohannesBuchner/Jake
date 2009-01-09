package com.jakeapp.core.dao;

import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.dao.exceptions.NoSuchServiceCredentialsException;

import java.util.UUID;
import java.util.List;
import java.sql.SQLException;

/**
 * DAO Interface for User Credentials.
 */
public interface IServiceCredentialsDao {

    /**
     * Puts new ServiceCredentials into the database.
     *
     * @param credentials The ServiceCredentials to insert
     * @return the inserted ServiceCredentials
     * @throws SQLException if a database error occured
     */
    public ServiceCredentials create(ServiceCredentials credentials) throws InvalidCredentialsException;


    /**
     * Returns the ServiceCredentials by the given uuid.
     *
     * @param uuid the UUID of the user requested
     * @return the ServiceCredentials for the requested user
     * @throws NoSuchServiceCredentialsException
     *          if no credentials by the given UUID exist
     */
    public ServiceCredentials read(UUID uuid) throws NoSuchServiceCredentialsException;


    
    public List<ServiceCredentials> getAll();


    /**
     * Updates the given ServiceCredentials.
     *
     * @param credentials the ServiceCredentials to be updated
     * @return the updated ServiceCredentials
     * @throws NoSuchServiceCredentialsException
     *          if no credentials by the uuid of this credentials where in the
     *          database
     */
    public ServiceCredentials update(ServiceCredentials credentials) throws NoSuchServiceCredentialsException;


    /**
     * Deletes the specified ServiceCredentials.
     *
     * @param credentials the ServiceCredentials to be deleted.
     * @throws NoSuchServiceCredentialsException
     *          if no credentials by the uuid of this credentials where in the
     *          database
     */
    public void delete(ServiceCredentials credentials) throws NoSuchServiceCredentialsException;


}
