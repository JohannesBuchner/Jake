package com.jakeapp.jake.ics;


/**
 * Objects wanting to receive messages have to implement this
 * 
 * @see IICService
 * @author johannes
 */

public interface IMessageReceiveListener {
    /**
     * Receive a message from a user. An Object has to implement this method and register
     * itself, to get notified of new Messages
     * @param from_userid the user id which sent the message
     * @param content the content of the message
     */
    public void receivedMessage(String from_userid, String content);
	
}
