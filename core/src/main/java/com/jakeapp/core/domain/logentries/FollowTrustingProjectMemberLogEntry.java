package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;

@Entity
@DiscriminatorColumn(name = "action")
@DiscriminatorValue(value = "FOLLOW_TRUSTING_PROJECTMEMBER")
public class FollowTrustingProjectMemberLogEntry extends ProjectMemberLogEntry {
	public FollowTrustingProjectMemberLogEntry(UserId userId, UserId me)
	{
		super(LogAction.FOLLOW_TRUSTING_PROJECTMEMBER, userId, me);
	}


	public FollowTrustingProjectMemberLogEntry() {
		setLogAction(LogAction.FOLLOW_TRUSTING_PROJECTMEMBER);
	}

	public static FollowTrustingProjectMemberLogEntry parse(LogEntry<? extends ILogable> logEntry) {
		return new FollowTrustingProjectMemberLogEntry((UserId) logEntry.getBelongsTo(), logEntry.getMember());
	}
}
