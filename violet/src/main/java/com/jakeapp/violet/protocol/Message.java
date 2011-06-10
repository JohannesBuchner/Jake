package com.jakeapp.violet.protocol;

import java.util.UUID;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.violet.model.User;

public abstract class Message {
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
