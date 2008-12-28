package com.jakeapp.jake.ics.status;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;


public interface IStatusService {
	
	/**
	 * Connects and authenticates on the used network service.
	 * @param userid the network user id to be logged in
	 * @param pw password
	 * @return whether the login was successful
	 * @throws NetworkException if the network connection is down
	 * @throws TimeoutException if a timeout is received
	 */
	public Boolean login(UserId userid, String pw)
		throws NetworkException, TimeoutException;
	
	/**
	 * Logs out and disconnects from the used network service.
	 * @throws NetworkException if the network connection is down and the logout couldn't be propagated
	 * @throws TimeoutException if a timeout occurred
	 */
	public void logout()
		throws NetworkException, TimeoutException;
	
	/**
	 * Checks whether the user is logged in. 
	 * The implementation has to assert that the user is still connected.
	 */
	public Boolean isLoggedIn();

	/**
	 * Checks if the userid may be reached (has an online status). 
	 * @param userid the other client to talk to.
	 * @throws NetworkException if the network connection is down
	 * @throws NotLoggedInException if the user is not logged in
	 * @throws TimeoutException if a timeout is received
	 */
	public Boolean isLoggedIn(UserId userid)
		throws NoSuchUseridException, NetworkException, NotLoggedInException, 
		TimeoutException;
	/**
	 * @return the firstname belonging to the userid
	 * @param userid the network user id in question
	 * @throws NoSuchUseridException if there is no such user
	 * @throws {@link OtherUserOfflineException}
	 */
	public String getFirstname(UserId userid) throws NoSuchUseridException, 
		OtherUserOfflineException;

	/**
	 * @return the lastname belonging to the userid
	 * @param userid the network user id in question
	 * @throws NoSuchUseridException if there is no such user
	 * @throws {@link OtherUserOfflineException}
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
	 * @param userid the user id
     * @return the users id
	 */
	public UserId getUserId(String userid);

	/**
	 * Registers a callback for the event that the userid goes online or offline
	 * 
	 * @param onlineStatusListener     object to be called
	 * @param userid                   the user id to look for
	 * @throws NoSuchUseridException    if the user does not exist
	 */
	public void registerOnlineStatusListener(IOnlineStatusListener onlineStatusListener,
			UserId userid) throws NoSuchUseridException;
	
}
