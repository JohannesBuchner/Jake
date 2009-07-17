package com.jakeapp.core.domain;

import java.io.Serializable;


/**
 * <em>This is deprecated. Remove as soon as safe and possible</em><br />
 * States for <code>Project</code>. When creating a new <code>Project</code>, initialize
 * it with the ACCEPTED-State; when receiving an invitation, create a <code>Project</code>
 * that is in INVITED-State.
 * @author Djinn
 * @deprecated
 */
@Deprecated
public enum InvitationState implements Serializable {
	/**
	 * Invitation was accepted, the <code>Project</code> can be used.
	 */
	ACCEPTED,
	/**
	 * An invitation was received, but the invitation was not accepted yet.
	 */
	INVITED
}
