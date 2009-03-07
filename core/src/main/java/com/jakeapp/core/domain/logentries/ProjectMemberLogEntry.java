package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.UUID;


@Entity
public class ProjectMemberLogEntry extends LogEntry<User> implements Serializable {

	private static final long serialVersionUID = 2563444652671713845L;

	public ProjectMemberLogEntry(LogAction logAction, User belongsTo, User member) {
		super(UUID.randomUUID(), logAction, getTime(), belongsTo, member, null, null,
				true);
		if (logAction != LogAction.START_TRUSTING_PROJECTMEMBER
				&& logAction != LogAction.STOP_TRUSTING_PROJECTMEMBER
				&& logAction != LogAction.FOLLOW_TRUSTING_PROJECTMEMBER)
			throw new IllegalArgumentException("invalid logaction for logentry");
	}

	public ProjectMemberLogEntry() {
	}
}
