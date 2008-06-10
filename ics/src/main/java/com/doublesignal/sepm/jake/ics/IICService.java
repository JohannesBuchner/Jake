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
	 * @param userid
	 * @param pw password
	 * @return wether the login was successful
	 * @throws NetworkException
	 * @throws TimeoutException
	 */
	public Boolean login(String userid, String pw)
		throws NetworkException, TimeoutException;
	
	/**
	 * Logs out and disconnects from the used network service.
	 * @throws NetworkException
	 * @throws TimeoutException
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
	 * @throws NetworkException
	 * @throws TimeoutException
	 * @throws NotLoggedInException
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
	 * @throws NetworkException
	 * @throws NotLoggedInException
	 * @throws TimeoutException
	 */
	public Boolean sendMessage(String to_userid, String content)
		throws NetworkException, NotLoggedInException, TimeoutException, 
			NoSuchUseridException, OtherUserOfflineException;
	
	/**
	 * Registers a callback for the event that a message is received.
	 * 
	 * @param rl    object to be called
	 */
	public void registerReceiveMessageListener(IMessageReceiveListener rl);
	
	/**
	 * Checks if the userid may be reached (has an online status). 
	 * @param userid the other client to talk to.
	 * @throws NetworkException
	 * @throws NotLoggedInException
	 * @throws TimeoutException
	 */
	public Boolean isLoggedIn(String userid)
		throws NoSuchUseridException, NetworkException, NotLoggedInException, 
		TimeoutException;
	
	/**
	 * Registers a callback for the event that the userid goes online or offline
	 * 
	 * @param osc     object to be called
	 * @param userid  
	 * @throws NoSuchUseridException 
	 */
	public void registerOnlineStatusListener(IOnlineStatusListener osc, 
			String userid) throws NoSuchUseridException;
	
	/**
	 * @return the firstname belonging to the userid
	 * @param userid
	 * @throws NoSuchUseridException
	 * @throws {@link OtherUserOfflineException}
	 */
	public String getFirstname(String userid) throws NoSuchUseridException, 
		OtherUserOfflineException;

	/**
	 * @return the lastname belonging to the userid
	 * @param userid
	 * @throws NoSuchUseridException
	 * @throws {@link OtherUserOfflineException}
	 */
	public String getLastname(String userid) throws NoSuchUseridException, 
		OtherUserOfflineException; 

	/**
	 * @return the userid we are logged in with
	 * @throws NotLoggedInException
	 */
	public String getUserid() throws NotLoggedInException;
	
	/**
	 * @return wether the userid has the right format for this implementation
	 */
	public boolean isOfCorrectUseridFormat(String userid);
	
	/**
	 * @return the name of the implemented service
	 */
	public String getServiceName();
}
