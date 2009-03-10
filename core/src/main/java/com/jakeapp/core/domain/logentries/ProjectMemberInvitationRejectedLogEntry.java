package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity @DiscriminatorValue(value = "PROJECT_REJECTED")
public class ProjectMemberInvitationRejectedLogEntry extends ProjectMemberLogEntry {

	public ProjectMemberInvitationRejectedLogEntry(User user, User me) {
		super(LogAction.PROJECT_REJECTED, user, me);
	}

	public ProjectMemberInvitationRejectedLogEntry() {
		this.setLogAction(LogAction.PROJECT_REJECTED);
	}

	public static ProjectMemberInvitationRejectedLogEntry parse(
					LogEntry<? extends ILogable> logEntry) {

		if (!logEntry.getLogAction().equals(LogAction.PROJECT_REJECTED))
			throw new UnsupportedOperationException();

		ProjectMemberInvitationRejectedLogEntry le =
						new ProjectMemberInvitationRejectedLogEntry(
										(User) logEntry.getBelongsTo(), logEntry.getMember());
		le.setTimestamp(logEntry.getTimestamp());
		le.setUuid(logEntry.getUuid());
		return le;
	}
}
