package com.jakeapp.jake.ics;


/**
 * Objects wanting to receive objects have to implement this
 * 
 * @see IICService
 * @author johannes
 */

public interface IObjectReceiveListener {

    /**
     * To receive objects from other users, an object/class has to implement this method and register itself
     * as a ObjectReceiveListener, to receive objects from other users.
     * @param from_userid the user id which sent the object
     * @param identifier the identifier of this object (useually relPath or note:xxx)
     * @param content the content itself as a byte array
     */
    public void receivedObject(String from_userid, String identifier,
			byte[] content);
	
	
	
}
