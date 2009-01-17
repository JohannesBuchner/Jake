package com.jakeapp.core.domain;

import javax.persistence.Entity;
import java.util.UUID;
import java.util.Date;

@Entity
public class ProjectLogEntry extends LogEntry<Project> {

    public ProjectLogEntry(UUID uuid, LogAction logAction, Date timestamp, Project project, Project belongsTo, ProjectMember member, String comment, String checksum, Boolean processed) {
        super(uuid, logAction, timestamp, project, belongsTo, member, comment, checksum, processed);
    }

    public ProjectLogEntry() {
    }
}
