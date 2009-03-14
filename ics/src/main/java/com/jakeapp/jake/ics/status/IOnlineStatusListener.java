package com.jakeapp.jake.ics.status;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.msgservice.IMsgService;

/**
 * Objects wanting to notice if users go online and offline have to implement 
 * this 
 * 
 * @see IMsgService
 * @author johannes
 */

public interface IOnlineStatusListener {

    /**
     * An Object has to implement this method and register itself as an OnlineStatusListener
     * to get notified of users online status changes.
     * @param userid the userid which changed its status
     */
    public void onlineStatusChanged(UserId userid);
}
