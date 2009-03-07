package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;


@Entity
@DiscriminatorValue(value = "START_TRUSTING_PROJECTMEMBER")
public class StartTrustingProjectMemberLogEntry extends ProjectMemberLogEntry {

	public StartTrustingProjectMemberLogEntry(User user, User me) {
		super(LogAction.START_TRUSTING_PROJECTMEMBER, user, me);
	}

	public StartTrustingProjectMemberLogEntry() {
		setLogAction(LogAction.START_TRUSTING_PROJECTMEMBER);
	}

	public static StartTrustingProjectMemberLogEntry parse(
			LogEntry<? extends ILogable> logEntry) {
		StartTrustingProjectMemberLogEntry le = new StartTrustingProjectMemberLogEntry(
				(User) logEntry.getBelongsTo(), logEntry.getMember());
		le.setTimestamp(logEntry.getTimestamp());
		return le;
	}
}
