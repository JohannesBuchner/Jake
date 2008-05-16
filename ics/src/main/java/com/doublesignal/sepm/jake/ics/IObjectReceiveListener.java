package com.doublesignal.sepm.jake.ics;


/**
 * Objects wanting to receive objects have to implement this
 * 
 * @see IICService
 * @author johannes
 */

public interface IObjectReceiveListener {
	
	public void receivedObject(String from_userid, String identifier, 
			String content);
	
	
	
}