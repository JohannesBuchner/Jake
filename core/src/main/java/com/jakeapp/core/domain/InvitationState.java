package com.jakeapp.core.domain;


/**
 * States for Projects. When creating a new <code>Project</code>, initialize
 * it with the ACCEPTED-State; when receiving an invitation, create a <code>Project</code>
 * that is in INVITED-State.
 * @author Djinn
 */
public enum InvitationState {
	/**
	 * Invitation was accepted, the <code>Project</code> can be used.
	 */
	ACCEPTED,
	/**
	 * An invitation was received, but the invitation was not accepted yet.
	 */
	INVITED;
}