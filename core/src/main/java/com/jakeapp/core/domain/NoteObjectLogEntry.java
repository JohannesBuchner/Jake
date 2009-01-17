package com.jakeapp.core.domain;

import java.util.UUID;
import java.util.Date;

public class NoteObjectLogEntry extends LogEntry<NoteObject> {
    public NoteObjectLogEntry(UUID uuid, LogAction logAction, Date timestamp, Project project,
                              NoteObject belongsTo, ProjectMember member, String comment,
                              String checksum, Boolean processed) {
        super(uuid, logAction, timestamp, project, belongsTo, member, comment, checksum, processed);
    }

    public NoteObjectLogEntry() {
    }
}
