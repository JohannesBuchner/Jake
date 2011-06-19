package com.jakeapp.violet.protocol.invites;

/**
 * Public interface for handling <code>Invitation</code>s
 */
public interface IInvitationHandler {

	/**
	 * Adding another <code>IProjectInvitationListener</code> to the list of
	 * listeners.
	 * 
	 * @param invitationListener
	 *            the <code>IProjectInvitationListener</code> listener to be
	 *            added.
	 */
	void registerInvitationListener(
			IProjectInvitationListener invitationListener);

	/**
	 * Remove the given <code>IProjectInvitationListener</code> from the list of
	 * listeners (if it exists)
	 * 
	 * @param invitationListener
	 *            the <code>IProjectInvitationListener</code> listener to be
	 *            removed.
	 */
	void unregisterInvitationListener(
			IProjectInvitationListener invitationListener);
}
