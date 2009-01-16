package com.jakeapp.jake.ics.status;

/**
 * called when a login/logout happened. Please note that you are in the main
 * thread and blocking everything. quit or spawn asap.
 * 
 * @author johannes
 */
public interface ILoginStateListener {

	public void loginHappened();

	public void logoutHappened();
}
