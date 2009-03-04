package com.jakeapp.jake.ics.status;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.*;


public interface IStatusService {

	/**
	 * Connects and authenticates on the used network service.
	 * If the service is already logged in, a logout is done first.
	 *
	 * @param userid the network user id to be logged in
	 * @param pw	  password
	 * @return whether the login was successful
	 * @throws NetworkException if the network connection is down
	 * @throws TimeoutException if a timeout is received
	 */
	public void login(UserId userid, String pw) throws NetworkException;

	/**
	 * Logs out and disconnects from the used network service.
	 * If the service is already logged out, this does nothing.
	 *
	 * @throws NetworkException if the network connection is down and the logout couldn't be
	 *                          propagated
	 * @throws TimeoutException if a timeout occurred
	 */
	public void logout() throws NetworkException;

	/**
	 * Creates a new user account.
	 *
	 * @param userid
	 * @param pw
	 * @throws NetworkException if the network connection is down and the logout couldn't be
	 *                          propagated
	 * @throws TimeoutException if a timeout occurred
	 */
	public void createAccount(UserId userid, String pw) throws NetworkException;

	/**
	 * Checks whether the user is logged in. The implementation has to assert
	 * that the user is still connected.
	 * @return
	 */
	public Boolean isLoggedIn();

	/**
	 * Checks if the userid may be reached (has an online status). Note: The
	 * online status is not requested by this method. You may want to use
	 * requestOnlineStatus first and use a OnlineStatusListener! (see
	 * IUsersService)
	 *
	 * @param userid the other client to talk to.
	 * @throws NetworkException	  if the network connection is down
	 * @throws NotLoggedInException if the user is not logged in
	 * @throws TimeoutException	  if a timeout is received
	 * @throws com.jakeapp.jake.ics.exceptions.NoSuchUseridException
	 * @return
	 */
	public Boolean isLoggedIn(UserId userid) throws NetworkException, TimeoutException;

	/**
	 * @param userid the network user id in question
	 * @return the firstname belonging to the userid
	 * @throws NoSuchUseridException if there is no such user
	 * @throws {@link					 OtherUserOfflineException}
	 * @throws com.jakeapp.jake.ics.exceptions.OtherUserOfflineException
	 */
	public String getFirstname(UserId userid) throws NoSuchUseridException,
			  OtherUserOfflineException;

	/**
	 * @param userid the network user id in question
	 * @return the lastname belonging to the userid
	 * @throws NoSuchUseridException if there is no such user
	 * @throws {@link					 OtherUserOfflineException}
	 * @throws com.jakeapp.jake.ics.exceptions.OtherUserOfflineException
	 */
	public String getLastname(UserId userid) throws NoSuchUseridException,
			  OtherUserOfflineException;

	/**
	 * @return the userid we are logged in with
	 * @throws NotLoggedInException if no user is logged in
	 */
	public UserId getUserid() throws NotLoggedInException;

	/**
	 * Checks if a user id is of the correct format for this network
	 *
	 * @param userid the user id
	 * @return the users id
	 */
	public UserId getUserId(String userid);

	/**
	 * on login/logout this method should be invoked
	 *
	 * @param lsl
	 */
	public void addLoginStateListener(ILoginStateListener lsl);

  public void removeLoginStateListener(ILoginStateListener lsl);

}
