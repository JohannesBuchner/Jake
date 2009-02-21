package com.jakeapp.core.domain;

import javax.persistence.Entity;
import java.util.UUID;
import java.util.Date;
import java.io.Serializable;

@Entity
public class ProjectLogEntry extends LogEntry<Project> implements Serializable {
    private static final long serialVersionUID = -8773156028147182736L;

    public ProjectLogEntry(Project project, UserId member) {
        super(UUID.randomUUID(),
				LogAction.PROJECT_CREATED, getTime(), project, member, null,
				null, true);
    }

    public ProjectLogEntry() {
    }
}
