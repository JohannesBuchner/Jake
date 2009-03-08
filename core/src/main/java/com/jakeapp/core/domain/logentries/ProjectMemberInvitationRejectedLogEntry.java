package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue(value = "START_TRUSTING_PROJECTMEMBER")
public class ProjectMemberInvitationRejectedLogEntry extends ProjectMemberLogEntry {

	public ProjectMemberInvitationRejectedLogEntry(User user, User me)
	{
		this.setMember(me);
		this.setBelongsTo(user);
		this.setLogAction(LogAction.PROJECT_REJECTED);
	}

	public ProjectMemberInvitationRejectedLogEntry(){
		this.setLogAction(LogAction.PROJECT_REJECTED);
	}
}
