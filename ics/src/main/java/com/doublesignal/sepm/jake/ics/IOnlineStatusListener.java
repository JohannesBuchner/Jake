package com.doublesignal.sepm.jake.ics;

/**
 * Objects wanting to notice if users go online and offline have to implement 
 * this 
 * 
 * @see IICService
 * @author johannes
 */

public interface IOnlineStatusListener {

    /**
     * An Object has to implement this method and register itself as an OnlineStatusListener
     * to get notified of users online status changes.
     * @param userid the userid which changed its status
     */
    public void onlineStatusChanged(String userid);

}
