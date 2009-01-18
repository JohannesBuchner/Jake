package com.jakeapp.core.domain;

import javax.persistence.Entity;
import java.util.UUID;
import java.util.Date;
import java.io.Serializable;

@Entity
public class TagLogEntry extends LogEntry<Tag> implements Serializable {
    public TagLogEntry(UUID uuid, LogAction logAction, Date timestamp, Project project, Tag belongsTo,
                       ProjectMember member, String comment, String checksum, Boolean processed) {
        super(uuid, logAction, timestamp, project, belongsTo, member, comment, checksum, processed);
    }

    public TagLogEntry() {
    }
}
