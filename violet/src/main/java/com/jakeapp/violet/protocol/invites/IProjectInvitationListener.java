package com.jakeapp.violet.protocol.invites;

import java.util.UUID;

import com.jakeapp.violet.model.User;


/**
 * Interface for handling <code>Invitation</code>s. Implementations of
 * <code>IProjectInvitationListener</code> can be added to
 * <code>IInvitationHandler</code>s. When an <code>Invitation</code> arrives,
 * the methods get called with the appropriate parameters.
 */
public interface IProjectInvitationListener {

	/**
	 * You have been invited by user to the Project p We store the project p in
	 * the local database as state invited
	 * 
	 * @param inviter
	 *            The <code>User</code> who invited us to the
	 *            <code>Project</code>
	 * @param project
	 *            The <code>Project</code> we've been invited to.
	 */
	public void invited(User inviter, String name, UUID id);

}
