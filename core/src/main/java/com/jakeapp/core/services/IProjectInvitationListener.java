package com.jakeapp.core.services;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;


public interface IProjectInvitationListener {

	/**
	 * You have been invited by user to the Project p
	 * We store the project p in the local database as state invited
	 * @param user
	 * @param p
	 */
	public void invited(UserId user, Project p);

	/**
	 * the user has accepted the invitation
	 * we create a logentry that the user is now in the project
	 * @param user
	 * @param p
	 */
	public void accepted(UserId user, Project p);

	/**
	 * the user has rejected the invitation
	 * we create a logentry that the user rejected our request.
	 * @param user
	 * @param p
	 */
	public void rejected(UserId user, Project p);

}
