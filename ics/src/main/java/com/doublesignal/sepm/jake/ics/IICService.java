package com.doublesignal.sepm.jake.ics;

import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;
import com.doublesignal.sepm.jake.ics.exceptions.NoSuchUseridException;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.ics.exceptions.OtherUserOfflineException;
import com.doublesignal.sepm.jake.ics.exceptions.TimeoutException;



/**
 * 
 * The task of the InterClient Communication Service (ICService) is to provide 
 * a communication layer based on the network for communication between users 
 * based on messages and objects.
 * 
 * <p>userid:   A way of identifying the user within the used network protocol.
 *           example: user@host</p>
 * <p>network service: The implementation of IICService use some sort of 
 *           Interclient Communication protocol. We reference to this underlying
 *           system as network service.
 *           examples: XMPP, TCP-Sockets, ...</p> 
 * 
 * @author johannes
 **/

public interface IICService {
	
	/**
	 * Connects and authentificates on the used network service.
	 * @param userid the network user id to be logged in
	 * @param pw password
	 * @return wether the login was successful
	 * @throws NetworkException if the network connection is down
	 * @throws TimeoutException if a timeout is received
	 */
	public Boolean login(String userid, String pw)
		throws NetworkException, TimeoutException;
	
	/**
	 * Logs out and disconnects from the used network service.
	 * @throws NetworkException if the network connection is down and the logout couldn't be propagated
	 * @throws TimeoutException if a timeout occured
	 */
	public void logout()
		throws NetworkException, TimeoutException;
	
	/**
	 * Checks wether the user is logged in. 
	 * The implementation has to assert that the user is still connected.
	 */
	public Boolean isLoggedIn();
	
	/**
	 * Sends a object to another user.
	 * @param to_userid        Userid to send to 
	 * @param objectidentifier name of the content so the receiver knows what 
	 *                         it is receiving
	 * @param content          Full object content as String
	 * @return                 wether the object could be sent. Does not 
	 *                         guarantee the object has been retrieved.
	 * @throws NetworkException  if the network connection is down
	 * @throws TimeoutException if a timeout occured while transmitting the object
	 * @throws NotLoggedInException if the user is not logged in
	 */
	public Boolean sendObject(String to_userid, String objectidentifier, 
			byte[] content) 
		throws NetworkException, NotLoggedInException, TimeoutException,
			NoSuchUseridException, OtherUserOfflineException;
	
	/**
	 * Registers a callback for the event that a object is received.
	 * If sendMessage() uses sendObject(), this is also called for messages. 
	 * 
	 * @param rl    object to be called
	 */
	public void registerReceiveObjectListener(IObjectReceiveListener rl);
	
	/**
	 * Sends a message to another user.
	 * May call sendObject with the objectidentifier "message".
	 * @param to_userid        Userid to send to
	 * @param content          Full message content as String 
	 * @return                 wether the message could be sent. Does not 
	 *                         guarantee the object has been retrieved.
	 * @throws NetworkException if the network connection is down
	 * @throws NotLoggedInException if the user is not logged in
	 * @throws TimeoutException if a timeout occured
	 */
	public Boolean sendMessage(String to_userid, String content)
		throws NetworkException, NotLoggedInException, TimeoutException, 
			NoSuchUseridException, OtherUserOfflineException;
	
	/**
	 * Registers a callback for the event that a message is received.
	 * 
	 * @param receiveListener    object to be called
	 */
	public void registerReceiveMessageListener(IMessageReceiveListener receiveListener);
	
	/**
	 * Checks if the userid may be reached (has an online status). 
	 * @param userid the other client to talk to.
	 * @throws NetworkException if the network connection is down
	 * @throws NotLoggedInException if the user is not logged in
	 * @throws TimeoutException if a timeout is received
	 */
	public Boolean isLoggedIn(String userid)
		throws NoSuchUseridException, NetworkException, NotLoggedInException, 
		TimeoutException;
	
	/**
	 * Registers a callback for the event that the userid goes online or offline
	 * 
	 * @param onlineStatusListener     object to be called
	 * @param userid                   the user id to look for
	 * @throws NoSuchUseridException    if the user does not exist
	 */
	public void registerOnlineStatusListener(IOnlineStatusListener onlineStatusListener,
			String userid) throws NoSuchUseridException;
	
	/**
	 * @return the firstname belonging to the userid
	 * @param userid the network user id in question
	 * @throws NoSuchUseridException if there is no such user
	 * @throws {@link OtherUserOfflineException}
	 */
	public String getFirstname(String userid) throws NoSuchUseridException, 
		OtherUserOfflineException;

	/**
	 * @return the lastname belonging to the userid
	 * @param userid the network user id in question
	 * @throws NoSuchUseridException if there is no such user
	 * @throws {@link OtherUserOfflineException}
	 */
	public String getLastname(String userid) throws NoSuchUseridException, 
		OtherUserOfflineException; 

	/**
	 * @return the userid we are logged in with
	 * @throws NotLoggedInException if no user is logged in
	 */
	public String getUserid() throws NotLoggedInException;
	
	/**
     * Checks if a user id is of the correct format for this network
	 * @param userid the user id to check
     * @return wether the userid has the right format for this implementation
	 */
	public boolean isOfCorrectUseridFormat(String userid);
	
	/**
	 * @return the name of the implemented service
	 */
	public String getServiceName();
}
