package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;


@Entity
@DiscriminatorColumn(name = "action")
@DiscriminatorValue(value = "START_TRUSTING_PROJECTMEMBER")
public class StartTrustingProjectMemberLogEntry extends ProjectMemberLogEntry {
  	public StartTrustingProjectMemberLogEntry(UserId userId, UserId me)
	{
		super(LogAction.START_TRUSTING_PROJECTMEMBER, userId, me);
	}

	public StartTrustingProjectMemberLogEntry()
	{
		setLogAction(LogAction.START_TRUSTING_PROJECTMEMBER);
	}

	public static StartTrustingProjectMemberLogEntry parse(LogEntry<? extends ILogable> logEntry) {
		return new StartTrustingProjectMemberLogEntry((UserId) logEntry.getBelongsTo(), logEntry.getMember());
	}
}
