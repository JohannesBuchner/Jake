package com.jakeapp.violet.actions.global.serve;

import java.io.InputStream;

import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.User;

/**
 * implement trust or whatever over this
 **/
public interface RequestHandlePolicy {

	/**
	 * finds users that can possibly provide a jake object (the last editor for
	 * example) Instead of a List of all possible providers, the LogEntries
	 * corresponding to the providers are returned. The providers can be
	 * accessed via
	 * {@link com.com.jakeapp.violet.model.logentries.LogEntry#getMember()}
	 * 
	 * @param jo
	 * @return the Logentries with the UserIds to try, a empty list if noone
	 *         could be found
	 */
	/*
	 * Notes for implementation: Provide the last editor as first, then maybe
	 * other users that are online.
	 */
	public Iterable<LogEntry> getPotentialJakeObjectProviders(JakeObject jo);

	/**
	 * Checks if it is ok to return the object.
	 * 
	 * @param from
	 * @param jo
	 * @return null if not allowed, the content otherwise
	 */
	public InputStream handleJakeObjectRequest(User from, JakeObject jo);


	/**
	 * Checks if it is ok to return the log.
	 * 
	 * @param jo
	 * @return whether retrieving is allowed
	 * @param project
	 * @param from
	 */
	public boolean handleLogSyncRequest(User from);
}
