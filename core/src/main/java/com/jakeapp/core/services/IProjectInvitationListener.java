package com.jakeapp.core.services;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;


public interface IProjectInvitationListener {

	/**
	 * You have been invited by user to the Project p
	 * 
	 * @param user
	 * @param p
	 */
	public void invited(UserId user, Project p);

	/**
	 * the user has accepted the invitation
	 * 
	 * @param user
	 * @param p
	 */
	public void accepted(UserId user, Project p);

	/**
	 * the user has accepted the invitation
	 * 
	 * @param user
	 * @param p
	 */
	public void rejected(UserId user, Project p);

}
