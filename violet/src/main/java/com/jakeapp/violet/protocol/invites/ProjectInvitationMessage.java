package com.jakeapp.violet.protocol.invites;

import java.util.UUID;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.violet.protocol.Message;

/**
 * Invitation to join a project.
 */
public class ProjectInvitationMessage extends Message {

	ProjectInvitationMessage(UUID projectId, UserId user) {
		super(projectId, user);
	}
}
