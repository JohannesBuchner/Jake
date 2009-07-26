package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * This <code>LogEntry</code> specifies that the <code>User</code> creating this <code>LogEntry</code> joined the
 * given <code>Project</code>.
 */
@Entity
@DiscriminatorValue(value = "PROJECT_JOINED")
public class ProjectJoinedLogEntry extends ProjectLogEntry {

	public ProjectJoinedLogEntry(Project project, User member) {
		super(LogAction.PROJECT_JOINED, project, member);
	}

	public ProjectJoinedLogEntry() {
		setLogAction(LogAction.PROJECT_JOINED);
	}

	public static ProjectLogEntry parse(LogEntry<? extends ILogable> logEntry) {
		ProjectJoinedLogEntry le = new ProjectJoinedLogEntry((Project) logEntry
				.getBelongsTo(), logEntry.getMember());
		le.setTimestamp(logEntry.getTimestamp());
		le.setUuid(logEntry.getUuid());
		return le;
	}
}
