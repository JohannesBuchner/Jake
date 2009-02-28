package com.jakeapp.core.domain;

import javax.persistence.Entity;
import java.util.UUID;
import java.util.Date;
import java.io.Serializable;

@Entity
public abstract class JakeObjectLogEntry extends LogEntry<JakeObject> implements Serializable {

	private static final long serialVersionUID = 3507342231350901911L;

	public JakeObjectLogEntry(LogAction logAction, JakeObject belongsTo,
			UserId member, String comment, String checksum, Boolean processed) {
		this(UUID.randomUUID(), logAction, getTime(), belongsTo, member, comment,
				checksum, processed);
	}

	public JakeObjectLogEntry(UUID uuid, LogAction logAction, Date timestamp,
			JakeObject belongsTo, UserId member, String comment, String checksum,
			Boolean processed) {
		super(uuid, logAction, timestamp, belongsTo, member, comment, checksum, processed);
		if (logAction != LogAction.JAKE_OBJECT_DELETE
				&& logAction != LogAction.JAKE_OBJECT_NEW_VERSION
				&& logAction != LogAction.JAKE_OBJECT_LOCK
				&& logAction != LogAction.JAKE_OBJECT_UNLOCK)
			throw new IllegalArgumentException("invalid logaction for logentry");
		this.setObjectuuid(belongsTo.getUuid().toString());
	}

	public JakeObjectLogEntry() {
	}

	public JakeObjectLogEntry(LogEntry<JakeObject> le) {
		this(le.getUuid(), le.getLogAction(), le.getTimestamp(), le.getBelongsTo(), le
				.getMember(), le.getComment(), le.getChecksum(), le.isProcessed());
	}

}
