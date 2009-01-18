package com.jakeapp.core.domain;

import javax.persistence.Entity;
import java.util.UUID;
import java.util.Date;
import java.io.Serializable;

@Entity
public class FileObjectLogEntry extends LogEntry<FileObject> implements Serializable {
    private static final long serialVersionUID = -7848929095284836158L;

    public FileObjectLogEntry(UUID uuid, LogAction logAction, Date timestamp, Project project, FileObject belongsTo,
                              ProjectMember member, String comment, String checksum, Boolean processed) {
        super(uuid, logAction, timestamp, project, belongsTo, member, comment, checksum, processed);
    }

    public FileObjectLogEntry() {
    }

	public FileObjectLogEntry(LogEntry<JakeObject> le) {
		this(le.getUuid(), le.getLogAction(), le.getTimestamp(), le.getProject(), (FileObject) le
				.getBelongsTo(), le.getMember(), le.getComment(), le.getChecksum(), Boolean.valueOf(le
				.isProcessed()));
	}
}
