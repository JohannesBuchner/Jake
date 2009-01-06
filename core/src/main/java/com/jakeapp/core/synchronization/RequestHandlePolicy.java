package com.jakeapp.core.synchronization;

import java.io.InputStream;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;

/**
 * implement trust or whatever over this
 **/
public interface RequestHandlePolicy {

	/**
	 * finds users that can possibly provide a jake object (the last editor for
	 * example)
	 * 
	 * @param jo
	 * @return the UserIds to try, null if noone could be found
	 */
	/*
	 * Notes for implementation: Provide the last editor as first, then maybe
	 * other users that are online.
	 */
	public Iterable<UserId> getPotentialJakeObjectProviders(JakeObject jo);

	/**
	 * Checks if it is ok to return the object.
	 * @param jo
	 * @return null if not allowed, the content otherwise
	 */
	public InputStream handleJakeObjectRequest(UserId from,  JakeObject jo);
	

	/**
	 * Checks if it is ok to return the log.
	 * @param jo
	 * @return whether retrieving is allowed
	 */
	public boolean handleLogSyncRequest(Project project, UserId from);
}
