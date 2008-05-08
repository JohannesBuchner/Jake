package com.doublesignal.sepm.jake.ics;


/**
 * Objects wanting to receive messages have to implement this
 * 
 * @author johannes
 */

public interface IMessageReceiveListener extends IObjectReceiveListener {
	
	public void receivedMessage(String from_userid, String content);
	
}
