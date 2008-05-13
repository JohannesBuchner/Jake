package com.doublesignal.sepm.jake.ics;

/**
 * Objects wanting to notice if users go online and offline have to implement 
 * this 
 * 
 * @see IICService
 * @author johannes
 */

public interface IOnlineStatusListener {
	
	public void onlineStatusChanged(String userid);

}
