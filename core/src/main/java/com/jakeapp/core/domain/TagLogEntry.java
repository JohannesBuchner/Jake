package com.jakeapp.core.domain;

import java.util.UUID;
import java.util.Date;

public class TagLogEntry extends LogEntry<Tag> {
    public TagLogEntry(UUID uuid, LogAction logAction, Date timestamp, Project project, Tag belongsTo,
                       ProjectMember member, String comment, String checksum, Boolean processed) {
        super(uuid, logAction, timestamp, project, belongsTo, member, comment, checksum, processed);
    }

    public TagLogEntry() {
    }
}
