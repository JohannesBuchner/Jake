package com.jakeapp.core.domain;

import java.util.Date;
import java.util.UUID;


public class LogEntryGenerator {

	public static JakeObjectLogEntry newLogEntry(JakeObject belongsTo,
			LogAction logAction, Project project, ProjectMember member, String comment,
			String checksum, Boolean processed) {
		if (logAction != LogAction.JAKE_OBJECT_DELETE
				&& logAction != LogAction.JAKE_OBJECT_NEW_VERSION
				&& logAction != LogAction.JAKE_OBJECT_LOCK
				&& logAction != LogAction.JAKE_OBJECT_UNLOCK)
			throw new IllegalArgumentException("invalid logaction for logentry");
		JakeObjectLogEntry le = new JakeObjectLogEntry(UUID.randomUUID(), logAction,
				getTime(), project, belongsTo, member, comment, checksum, processed);
		le.setObjectuuid(belongsTo.getUuid().toString());
		return le;
	}


	private static Date getTime() {
		return new Date();
	}


	public static ProjectMemberLogEntry newLogEntry(ProjectMember belongsTo,
			LogAction logAction, Project project, ProjectMember member) {
		if (logAction != LogAction.START_TRUSTING_PROJECTMEMBER
				&& logAction != LogAction.STOP_TRUSTING_PROJECTMEMBER)
			throw new IllegalArgumentException("invalid logaction for logentry");

		ProjectMemberLogEntry le = new ProjectMemberLogEntry(UUID.randomUUID(),
				logAction, getTime(), project, belongsTo, member, null, null, true);
		return le;
	}

	public static TagLogEntry newLogEntry(Tag belongsTo, LogAction logAction,
			Project project, ProjectMember member) {
		if (logAction != LogAction.TAG_ADD && logAction != LogAction.TAG_REMOVE)
			throw new IllegalArgumentException("invalid logaction for logentry");

		return new TagLogEntry(UUID.randomUUID(), logAction, getTime(),
				project, belongsTo, member, null, null, true);
	}


	public static ProjectLogEntry newProjectLogEntry(Project project, ProjectMember member) {
		ProjectLogEntry le = new ProjectLogEntry(UUID.randomUUID(),
				LogAction.PROJECT_CREATED, getTime(), project, project, member, null,
				null, true);
		return le;
	}
}
