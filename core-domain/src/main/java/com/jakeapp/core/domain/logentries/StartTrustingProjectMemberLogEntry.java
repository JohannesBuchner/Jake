package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * This <code>LogEntry</code> specifies that the <code>User</code> creating this <code>LogEntry</code>
 * starts trusting the given <code>User</code>. 
 */
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
		le.setUuid(logEntry.getUuid());
		return le;
	}
}
