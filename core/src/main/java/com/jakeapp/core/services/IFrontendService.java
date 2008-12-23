package com.jakeapp.core.services;

import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NotLoggedInException;

import java.util.Map;
import java.util.List;

/**
 * This is the only visible interface to other components accessing
 * the jake core
 */
public interface IFrontendService {

    /**
     * This method is used to authenticate a client accessing the jake-core.
     * On a successfull
     * authentication a Session-Identifier (sessionId) is returned
     *
     * @param credentials a Map of credentials // TODO to be specified
     * @return a Session-Identifier
     * @throws IllegalArgumentException    if the supplied credentials are
     *                                     null or one of the entries is null
     * @throws InvalidCredentialsException if the supplied credentials are wrong
     */
    public String authenticate(Map<String, String> credentials)
            throws IllegalArgumentException,
            InvalidCredentialsException;

    /**
     * This method logs a specific client out.
     *
     * @param sessionId the session to be terminated
     * @return true on success, false on failure
     * @throws IllegalArgumentException if the supplied session is null
     * @throws NotLoggedInException     if no such session existed
     */
    public boolean logout(String sessionId)
            throws IllegalArgumentException, NotLoggedInException;

    /**
     * Gets an instance of a IProjectService
     *
     * @param sessionId a Session-Identifier
     * @return a ProjectService on success
     * @throws IllegalArgumentException if the supplied session is null
     * @throws NotLoggedInException     if no such session existed
     * @throws IllegalStateException    if no ProjectService is available
     */
    public IProjectService getProjectService(String sessionId)
            throws IllegalArgumentException, NotLoggedInException,
            IllegalStateException;


    /**
     * Gets a list of the currently available MessageServices
     *
     * @param sessionId a Session-Identifier
     * @return a List of MessageServices
     * @throws IllegalArgumentException if the supplied session is null
     * @throws NotLoggedInException     if no such session existed
     * @throws IllegalStateException    if no MessageServices are configured
     *                                  for this component
     */
    public List<MsgService> getMsgServices(String sessionId)
            throws IllegalArgumentException, NotLoggedInException,
            IllegalStateException;


    /**
     * Pings the core to prevent session expiry
     *
     * @param sessionId a Session-Identifier
     * @throws IllegalArgumentException if the supplied session is null
     * @throws NotLoggedInException     if no such session existed
     */
    public void ping(String sessionId)
            throws IllegalArgumentException, NotLoggedInException;

}
