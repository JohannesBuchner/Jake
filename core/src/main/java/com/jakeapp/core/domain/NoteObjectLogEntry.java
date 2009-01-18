package com.jakeapp.core.domain;

import java.util.UUID;
import java.util.Date;

public class NoteObjectLogEntry extends LogEntry<NoteObject> {

	public NoteObjectLogEntry(UUID uuid, LogAction logAction, Date timestamp,
			Project project, NoteObject belongsTo, ProjectMember member, String comment,
			String checksum, Boolean processed) {
		super(uuid, logAction, timestamp, project, belongsTo, member, comment, checksum,
				processed);
	}

	public NoteObjectLogEntry() {
	}

	public NoteObjectLogEntry(LogEntry<JakeObject> le) {
		this(le.getUuid(), le.getLogAction(), le.getTimestamp(), le.getProject(), (NoteObject) le
				.getBelongsTo(), le.getMember(), le.getComment(), le.getChecksum(), Boolean.valueOf(le
				.isProcessed()));
	}
}
