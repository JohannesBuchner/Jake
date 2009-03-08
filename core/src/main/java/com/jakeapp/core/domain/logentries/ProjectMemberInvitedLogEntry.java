package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.User;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * This logentry states the the user was invited to the project
 */
@Entity
@DiscriminatorValue(value = "PROJECTMEMBER_INVITED")
public class ProjectMemberInvitedLogEntry extends ProjectMemberLogEntry {
	private static final long serialVersionUID = 4036902834880403137L;

	public ProjectMemberInvitedLogEntry(User user, User me)
	{
		super(LogAction.PROJECTMEMBER_INVITED, user, me);
	}

	public ProjectMemberInvitedLogEntry() {
		setLogAction(LogAction.PROJECTMEMBER_INVITED);
	}
}
