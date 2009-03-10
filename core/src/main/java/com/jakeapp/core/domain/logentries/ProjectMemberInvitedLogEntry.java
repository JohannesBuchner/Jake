package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * This logentry states the the user was invited to the project
 */
@Entity @DiscriminatorValue(value = "PROJECTMEMBER_INVITED")
public class ProjectMemberInvitedLogEntry extends ProjectMemberLogEntry {
	private static final long serialVersionUID = 4036902834880403137L;

	public ProjectMemberInvitedLogEntry(User user, User me) {
		super(LogAction.PROJECTMEMBER_INVITED, user, me);
	}

	public ProjectMemberInvitedLogEntry() {
		setLogAction(LogAction.PROJECTMEMBER_INVITED);
	}

	public static ProjectMemberInvitedLogEntry parse(LogEntry<? extends ILogable> logEntry) {

		if (!logEntry.getLogAction().equals(LogAction.PROJECTMEMBER_INVITED))
			throw new UnsupportedOperationException();

		ProjectMemberInvitedLogEntry le = new ProjectMemberInvitedLogEntry((User) logEntry.getBelongsTo(),
						logEntry.getMember());
		le.setTimestamp(logEntry.getTimestamp());
		le.setUuid(logEntry.getUuid());
		return le;
	}
}
