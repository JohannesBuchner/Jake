package com.jakeapp.core.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NotLoggedInException;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.synchronization.ISyncService;

/**
 * This is the only visible interface to other components accessing the jake
 * core
 */
public interface IFrontendService {

	/**
	 * This method is used to authenticate a client accessing the jake-core. On
	 * a successful authentication a Session-Identifier (sessionId) is returned
	 * 
	 * @param credentials
	 *            a Map of credentials // TODO to be specified
	 * @return a Session-Identifier
	 * @throws IllegalArgumentException
	 *             if the supplied credentials are null or one of the entries is
	 *             null
	 * @throws InvalidCredentialsException
	 *             if the supplied credentials are wrong
	 */
	public String authenticate(Map<String, String> credentials)
			throws IllegalArgumentException, InvalidCredentialsException;

	/**
	 * This method logs a specific client out.
	 * 
	 * @param sessionId
	 *            the session to be terminated
	 * @return true on success, false on failure
	 * @throws IllegalArgumentException
	 *             if the supplied session is null
	 * @throws NotLoggedInException
	 *             if no such session existed
	 */
	public boolean logout(String sessionId) throws IllegalArgumentException,
			NotLoggedInException;

	/**
	 * Gets an instance of a {@link IProjectsManagingService}
	 * 
	 * @param sessionId
	 *            a Session-Identifier
	 * @return a ProjectService on success
	 * @throws IllegalArgumentException
	 *             if the supplied session is null
	 * @throws NotLoggedInException
	 *             if no such session existed
	 * @throws IllegalStateException
	 *             if no ProjectService is available
	 */
	public IProjectsManagingService getProjectsManagingService(String sessionId)
			throws IllegalArgumentException, NotLoggedInException, IllegalStateException;


	/**
	 * Gets a list of the currently available MessageServices
	 * 
	 * @param sessionId
	 *            a Session-Identifier
	 * @return a List of MessageServices
	 * @throws IllegalArgumentException
	 *             if the supplied session is null
	 * @throws NotLoggedInException
	 *             if no such session existed
	 * @throws IllegalStateException
	 *             if no MessageServices are configured for this component
	 */
	public List<MsgService> getMsgServices(String sessionId) throws NotLoggedInException;

	/**
	 * Gets the SyncService
	 * 
	 * @param sessionId
	 *            a Session-Identifier
	 * @return 
	 * @throws IllegalArgumentException
	 *             if the supplied session is null
	 * @throws NotLoggedInException
	 *             if no such session existed
	 * @throws IllegalStateException
	 *             if no MessageServices are configured for this component
	 */
	public ISyncService getSyncService(String sessionId) throws NotLoggedInException;


	/**
	 * Method that creates an account by the IM-Provider
	 * 
	 * @param sessionId
	 * @param credentials
	 * @return
	 * @throws NotLoggedInException
	 * @throws InvalidCredentialsException
	 * @throws ProtocolNotSupportedException
	 * @throws Exception
	 *             the creation failed for another reason
	 */
	public boolean createAccount(String sessionId, ServiceCredentials credentials)
			throws NotLoggedInException, InvalidCredentialsException,
			ProtocolNotSupportedException, Exception;


	/**
	 * Provides and registers a new MsgService ready for login.
	 * 
	 * @param sessionId
	 * @param credentials
	 * @return
	 * @throws NotLoggedInException
	 * @throws InvalidCredentialsException
	 * @throws ProtocolNotSupportedException
	 */
	public MsgService addAccount(String sessionId, ServiceCredentials credentials)
			throws NotLoggedInException, InvalidCredentialsException,
			ProtocolNotSupportedException;
	
	/**
	 * Logs all Message services that are currently active out.
	 * After calling this method all MessageServices within the specified
	 * Session are logged out.
	 * @param sessionId 
	 * @throws NotLoggedInException If <code>sessionId</code> is invalid.
	 */
	void signOut(String sessionId) throws NotLoggedInException;

	/**
	 * Pings the core to prevent session expiry
	 * 
	 * @param sessionId
	 *            a Session-Identifier
	 * @throws IllegalArgumentException
	 *             if the supplied session is null
	 * @throws NotLoggedInException
	 *             if no such session existed
	 */
	public void ping(String sessionId) throws IllegalArgumentException,
			NotLoggedInException;

	/**
	 * @return All stored Service-credentials that were 'recently' used.
	 * 	A definition of recently is pending.
	 */
	public Collection<ServiceCredentials> getLastLogins();
}
