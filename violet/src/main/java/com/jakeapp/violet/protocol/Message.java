package com.jakeapp.violet.protocol;

import java.util.UUID;

import com.jakeapp.jake.ics.UserId;

/**
 * A project-related message
 */
public abstract class Message {

	public Message(UUID projectId, UserId user) {
		super();
		this.projectId = projectId;
		this.user = user;
	}

	private UUID projectId;

	private UserId user;

	public void setProjectId(UUID projectId) {
		this.projectId = projectId;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public void setUser(UserId userId) {
		this.user = userId;
	}

	public UserId getUser() {
		return user;
	}
}
