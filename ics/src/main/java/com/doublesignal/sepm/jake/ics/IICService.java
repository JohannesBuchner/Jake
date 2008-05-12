package com.doublesignal.sepm.jake.ics;

import java.io.IOException;


/**
 * 
 * The task of the InterClient Communication Service (ICService) is to provide 
 * a communication layer based on the network for communication between users 
 * based on messages and objects.
 * 
 * @author johannes
 * 
 * userid:   A way of identifying the user within the used network protocol.
 *           example: user@host
 * network service: The implementation of IICService use some sort of 
 *           Interclient Communication protocol. We reference to this underlying
 *           system as network service.
 *           examples: XMPP, TCP-Sockets, ... 
 **/

public interface IICService {
	
	/**
	 * Connects and authentificates on the used network service.
	 * @param userid
	 * @param pw password
	 * @return wether the login was successful
	 */
	public Boolean login(String userid, String pw);
	
	/**
	 * Logs out and disconnects from the used network service.
	 * @return wether the logout was successful
	 */
	public Boolean logout();
	
	/**
	 * Checks wether a previous login was successful and the connection is 
	 * still functioning
	 */
	public Boolean isConnected();
	
	/* TODO: This pretty much have to be strings, right? 
	 * or should we try some fancy serialization stuff? */
	
	/**
	 * Sends a object to another user.
	 * @param to_userid        Userid to send to 
	 * @param objectidentifier name of the content so the receiver knows what 
	 *                         he/she is receiving
	 * @param content          Full object content as String
	 * @return                 wether the object could be sent. Does not 
	 *                         guarantee the object has been retrieved.
	 */
	public Boolean sendObject(String to_userid, String objectidentifier, 
			String content) 
		throws IOException;
	
	/**
	 * Registers a callback for the event that a object is received.
	 * If sendMessage() uses sendObject(), this is also called for messages. 
	 * 
	 * @param rl    object to be called
	 */
	public void registerReceiveObjectCallback(IObjectReceiveListener rl);
	
	/**
	 * Sends a message to another user.
	 * Can call sendObject with the objectidentifier "message".
	 * @param to_userid        Userid to send to
	 * @param content          Full message content as String 
	 * @return                 wether the message could be sent. Does not 
	 *                         guarantee the object has been retrieved.
	 */
	public Boolean sendMessage(String to_userid, String content)
		throws IOException;
	
	/**
	 * Registers a callback for the event that a message is received.
	 * 
	 * @param rl    object to be called
	 */
	public void registerReceiveMessageCallback(IMessageReceiveListener rl);
	
	/**
	 * Checks if the userid may be reached (has an online status). 
	 * @param userid the other client to talk to.
	 */
	public Boolean isOnline(String userid);
	
	/**
	 * Registers a callback for the event that the userid goes online or offline
	 * 
	 * @param osc   object to be called
	 */
	public void registerOnlineStatusCallback(IOnlineStatusCallback osc, 
			String userid);
	
}
