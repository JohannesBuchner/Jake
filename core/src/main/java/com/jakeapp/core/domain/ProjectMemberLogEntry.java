package com.jakeapp.core.domain;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.UUID;
import java.util.Date;


@Entity
public class ProjectMemberLogEntry extends LogEntry<ProjectMember> implements Serializable {
    private static final long serialVersionUID = 2563444652671713845L;

    public ProjectMemberLogEntry(UUID uuid, LogAction logAction, Date timestamp, Project project,
                                 ProjectMember belongsTo, ProjectMember member, String comment,
                                 String checksum, Boolean processed) {
        super(uuid, logAction, timestamp, project, belongsTo, member, comment, checksum, processed);
    }

    public ProjectMemberLogEntry()
    {}
}
