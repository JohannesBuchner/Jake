package com.jakeapp.core.services;

import java.util.List;
import java.util.Map;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NoSuchMsgServiceException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.status.ILoginStateListener;

/**
 * This is the only visible interface to other components accessing the jake
 * core
 */
public interface IFrontendService {

	/**
	 * This method is used to authenticate a client accessing the jake-core. On
	 * a successful authentication a Session-Identifier (sessionId) is returned
	 *
	 * @param credentials	a <code>Map</code> of credentials,
	 *                       will be replaced by a concrete <code>FrontendCredentials</code> obj.
	 * @param changeListener the callback or JakeObject-changes
	 * @return a Session-Identifier
	 * @throws IllegalArgumentException	if the supplied credentials are null or one of the entries is null
	 * @throws InvalidCredentialsException if the supplied credentials are wrong
	 */
	public String authenticate(Map<String, String> credentials, ChangeListener changeListener)
			throws IllegalArgumentException, InvalidCredentialsException;

	/**
	 * This method logs a specific client out.
	 *
	 * @param sessionId the session to be terminated
	 * @return true on success, false on failure
	 * @throws IllegalArgumentException if the supplied session is null
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *                                  if no such session existed
	 */
	public boolean logout(String sessionId) throws IllegalArgumentException,
			FrontendNotLoggedInException;

	/**
	 * Gets an instance of a {@link IProjectsManagingService}
	 *
	 * @param sessionId a Session-Identifier
	 * @return a ProjectService on success
	 * @throws IllegalArgumentException	 if the supplied session is null
	 * @throws FrontendNotLoggedInException If the supplied <code>sessionId</code> was invalid or
	 *                                      the session already timed out.
	 * @throws IllegalStateException		if no ProjectService is available
	 */
	public IProjectsManagingService getProjectsManagingService(String sessionId)
			throws IllegalArgumentException, FrontendNotLoggedInException, IllegalStateException;


	/**
	 * Gets a list of the currently available MessageServices
	 *
	 * @param sessionId a Session-Identifier
	 * @return a List of MessageServices
	 * @throws IllegalArgumentException	 if the supplied session is null
	 * @throws FrontendNotLoggedInException If the supplied <code>sessionId</code> was invalid or
	 *                                      the session already timed out.
	 * @throws IllegalStateException		if no MessageServices are configured for this component
	 */
	public List<MsgService<User>> getMsgServices(String sessionId) throws FrontendNotLoggedInException;

	/**
	 * Gets the SyncService
	 *
	 * @param sessionId a Session-Identifier
	 * @return an instance of <code>IFriendlySyncService</code>
	 * @throws IllegalArgumentException	 if the supplied session is null
	 * @throws FrontendNotLoggedInException If the supplied <code>sessionId</code> was invalid or
	 *                                      the session already timed out.
	 * @throws IllegalStateException		if no MessageServices are configured for this component
	 */
	public IFriendlySyncService getSyncService(String sessionId) throws FrontendNotLoggedInException;


	/**
	 * Method that creates an account by the IM-Provider
	 *
	 * @param sessionId   a <code>String</code> identifying our Session
	 * @param credentials the <code>Account</code>-Data for which a new User on the IM-Provider should be registered.
	 * @return
	 * @throws FrontendNotLoggedInException  If the supplied <code>sessionId</code> was invalid or
	 *                                       the session already timed out.
	 * @throws InvalidCredentialsException
	 * @throws ProtocolNotSupportedException
	 * @throws Exception					 the creation failed for another reason
	 * @throws com.jakeapp.jake.ics.exceptions.NetworkException
	 *
	 */
	public AvailableLaterObject<Void> createAccount(String sessionId, Account credentials)
			throws FrontendNotLoggedInException, InvalidCredentialsException,
			ProtocolNotSupportedException, NetworkException;


	/**
	 * Provides and registers a new MsgService ready for login.
	 *
	 * @param sessionId   a <code>String</code> identifying our Session
	 * @param credentials an <code>Account</code> on which the new <code>MsgService</code> should be based on
	 * @return the created <code>MsgService</code>, ready for login!
	 * @throws FrontendNotLoggedInException  If the supplied <code>sessionId</code> was invalid or
	 *                                       the session already timed out.
	 * @throws InvalidCredentialsException   if the supplied <code>Account</code> contains invalid data
	 * @throws ProtocolNotSupportedException if the supplied <code>Account</code> is specified for a
	 *                                       <code>ProtocolType</code> currently not supported.
	 */
	public MsgService addAccount(String sessionId, Account credentials)
			throws FrontendNotLoggedInException, InvalidCredentialsException,
			ProtocolNotSupportedException;

	/**
	 * Removes an account from the database registry
	 *
	 * @param sessionId  a <code>String</code> identifying our Session
	 * @param msgService the <code>MsgService</code> to be removed from the system.
	 * @throws FrontendNotLoggedInException If the supplied <code>sessionId</code> was invalid or
	 *                                      the session already timed out.
	 * @throws NoSuchMsgServiceException	if the MsgService specified does not exist.
	 */
	void removeAccount(String sessionId, MsgService msgService)
			throws FrontendNotLoggedInException, NoSuchMsgServiceException;

	/**
	 * Logs all Message services that are currently active out.
	 * After calling this method all MessageServices within the specified
	 * Session are logged out.
	 *
	 * @param sessionId a <code>String</code> identifying our Session
	 * @throws FrontendNotLoggedInException If the supplied <code>sessionId</code> was invalid or
	 *                                      the session already timed out.
	 */
	void signOut(String sessionId) throws FrontendNotLoggedInException;

	/**
	 * Pings the core to prevent session expiry
	 *
	 * @param sessionId a <code>String</code> identifying our Session
	 * @throws IllegalArgumentException	 if the supplied session is null
	 * @throws FrontendNotLoggedInException If the supplied <code>sessionId</code> was invalid or
	 *                                      the session already timed out.
	 */
	public void ping(String sessionId) throws IllegalArgumentException,
			FrontendNotLoggedInException;

	/**
	 * Logs a <code>MsgService</code> in and sets the password
	 *
	 * @param sessionId		a <code>String</code> identifying our Session
	 * @param msgService	   the <code>MsgService</code> to login to.
	 * @param password		 new password to be stored, or null to use the stored one.
	 * @param rememberPassword a <code>boolean</code> indicating of the entered password should be saved or not
	 * @return an <code>AvailableLaterObject</code> with param <code>Boolean</code> indicating if the login succeeded
	 *         or not.
	 * @internally throws Exception
	 */
	AvailableLaterObject<Boolean> login(String sessionId, MsgService msgService, String password,
										boolean rememberPassword, final ILoginStateListener loginListener);


	/**
	 * Logs a <code>MsgService</code> in with the stored password
	 *
	 * @param sessionId	 a <code>String</code> identifying our Session
	 * @param msgService	the <code>MsgService</code> to login to.
	 * @param credentials   an <code>Account</code> object containing the changed credentials
	 * @param loginListener a <code>ILoginStateListener</code> receiving &quot;Login State Events&quot;
	 * @return an <code>AvailableLaterObject</code> with param <code>Boolean</code> indicating if the login succeeded
	 *         or not.
	 * @internally throws Exception
	 */
	AvailableLaterObject<Boolean> login(String sessionId, MsgService msgService,
										Account credentials, final ILoginStateListener loginListener);

}
