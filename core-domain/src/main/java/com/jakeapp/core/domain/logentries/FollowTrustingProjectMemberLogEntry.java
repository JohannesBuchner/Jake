package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * This LogEntry specifies that a <code>User</code> &quot;fully trusts&quot; another <code>User</code>
 * and therefor also trusts all <code>User</code>s the other <code>User</code> trusts.
 */
@Entity
@DiscriminatorValue(value = "FOLLOW_TRUSTING_PROJECTMEMBER")
public class FollowTrustingProjectMemberLogEntry extends ProjectMemberLogEntry {

	public FollowTrustingProjectMemberLogEntry(User user, User me)
	{
		super(LogAction.FOLLOW_TRUSTING_PROJECTMEMBER, user, me);
	}


	public FollowTrustingProjectMemberLogEntry() {
		setLogAction(LogAction.FOLLOW_TRUSTING_PROJECTMEMBER);
	}

	public static FollowTrustingProjectMemberLogEntry parse(LogEntry<? extends ILogable> logEntry) {
		FollowTrustingProjectMemberLogEntry le = new FollowTrustingProjectMemberLogEntry((User) logEntry.getBelongsTo(), logEntry.getMember());
		le.setTimestamp(logEntry.getTimestamp());
		le.setUuid(logEntry.getUuid());
		return le;
	}
}
