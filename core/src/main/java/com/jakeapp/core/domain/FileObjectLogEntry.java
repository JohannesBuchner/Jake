package com.jakeapp.core.domain;

import java.util.UUID;
import java.util.Date;


public class FileObjectLogEntry extends LogEntry<FileObject> {
    public FileObjectLogEntry(UUID uuid, LogAction logAction, Date timestamp, Project project, FileObject belongsTo,
                              ProjectMember member, String comment, String checksum, Boolean processed) {
        super(uuid, logAction, timestamp, project, belongsTo, member, comment, checksum, processed);
    }

    public FileObjectLogEntry() {
    }
}
