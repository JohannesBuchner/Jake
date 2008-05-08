package com.doublesignal.sepm.jake.ics;


/**
 * Objects wanting to receive messages have to implement this
 * 
 * @author johannes
 */

public interface MessageReceiveListener extends ObjectReceiveListener {
	
	public void receivedMessage(String from_userid, String content);
	
}
