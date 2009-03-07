package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

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
		return le;
	}
}
