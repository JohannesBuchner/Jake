package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.UUID;


/**
 * This is the superclass for all <code>LogEntry</code>s working with <code>User</code>
 * objects in the <code>belongsTo</code> member of a <code>LogEntry</code>.
 */
@Entity
public class ProjectMemberLogEntry extends LogEntry<User> implements Serializable {

	private static final long serialVersionUID = 2563444652671713845L;

	public ProjectMemberLogEntry(LogAction logAction, User belongsTo, User member) {
		super(UUID.randomUUID(), logAction, getTime(), belongsTo, member, null, null,
				true);
		if (logAction != LogAction.START_TRUSTING_PROJECTMEMBER
				&& logAction != LogAction.STOP_TRUSTING_PROJECTMEMBER
				&& logAction != LogAction.FOLLOW_TRUSTING_PROJECTMEMBER
				&& logAction != LogAction.PROJECTMEMBER_INVITED
				&& logAction != LogAction.PROJECT_REJECTED
				&& logAction != LogAction.PROJECT_JOINED		
				)
			throw new IllegalArgumentException("invalid logaction for logentry");
	}

	public ProjectMemberLogEntry() {
	}
}
