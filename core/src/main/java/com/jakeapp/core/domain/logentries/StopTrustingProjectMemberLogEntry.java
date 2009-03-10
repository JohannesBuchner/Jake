package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue(value = "STOP_TRUSTING_PROJECTMEMBER")
public class StopTrustingProjectMemberLogEntry extends ProjectMemberLogEntry {

	public StopTrustingProjectMemberLogEntry(User user, User me) {
		super(LogAction.STOP_TRUSTING_PROJECTMEMBER, user, me);
	}

	public StopTrustingProjectMemberLogEntry() {
		setLogAction(LogAction.STOP_TRUSTING_PROJECTMEMBER);
	}


	public static StopTrustingProjectMemberLogEntry parse(
			LogEntry<? extends ILogable> logEntry) {
		StopTrustingProjectMemberLogEntry le = new StopTrustingProjectMemberLogEntry(
				(User) logEntry.getBelongsTo(), logEntry.getMember());
		le.setTimestamp(logEntry.getTimestamp());
		le.setUuid(logEntry.getUuid());
		return le;
	}
}
