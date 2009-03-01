package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.DiscriminatorColumn;


@Entity
@DiscriminatorValue(value = "STOP_TRUSTING_PROJECTMEMBER")
public class StopTrustingProjectMemberLogEntry extends ProjectMemberLogEntry {

   	public StopTrustingProjectMemberLogEntry(UserId userId, UserId me)
	{
		super(LogAction.STOP_TRUSTING_PROJECTMEMBER, userId, me);
	}

	public StopTrustingProjectMemberLogEntry()
	{
		setLogAction(LogAction.STOP_TRUSTING_PROJECTMEMBER);
	}


	public static StopTrustingProjectMemberLogEntry parse(LogEntry<? extends ILogable> logEntry) {
		return new StopTrustingProjectMemberLogEntry((UserId) logEntry.getBelongsTo(), logEntry.getMember());
	}
}
