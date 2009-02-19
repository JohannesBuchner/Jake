package com.jakeapp.core.domain;

import javax.persistence.Entity;
import java.util.UUID;
import java.util.Date;
import java.io.Serializable;

@Entity
public class JakeObjectLogEntry extends LogEntry<JakeObject> implements Serializable {

	private static final long serialVersionUID = 3507342231350901911L;

	public JakeObjectLogEntry(UUID uuid, LogAction logAction, Date timestamp,
			Project project, JakeObject belongsTo, ProjectMember member, String comment,
			String checksum, Boolean processed) {
		super(uuid, logAction, timestamp, project, belongsTo, member, comment, checksum,
				processed);
		this.setObjectuuid(belongsTo.getUuid().toString());
	}

	public JakeObjectLogEntry() {
	}

	public JakeObjectLogEntry(LogEntry<JakeObject> le) {
		this(le.getUuid(), le.getLogAction(), le.getTimestamp(), le.getProject(),
				(NoteObject) le.getBelongsTo(), le.getMember(), le.getComment(), le
						.getChecksum(), le.isProcessed());
	}

}
