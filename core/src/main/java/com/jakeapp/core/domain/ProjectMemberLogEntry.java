package com.jakeapp.core.domain;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.UUID;
import java.util.Date;


@Entity
public class ProjectMemberLogEntry extends LogEntry<UserId> implements Serializable {

	private static final long serialVersionUID = 2563444652671713845L;

	public ProjectMemberLogEntry(LogAction logAction, UserId belongsTo, UserId member) {
		super(UUID.randomUUID(), logAction, getTime(), belongsTo, member, null, null,
				true);
		if (logAction != LogAction.START_TRUSTING_PROJECTMEMBER
				&& logAction != LogAction.STOP_TRUSTING_PROJECTMEMBER)
			throw new IllegalArgumentException("invalid logaction for logentry");
	}

	public ProjectMemberLogEntry() {
	}
}
