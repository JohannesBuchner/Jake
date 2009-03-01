package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.LogAction;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue(value = "PROJECT_JOINED")
public class ProjectJoinedLogEntry extends ProjectLogEntry {
    public ProjectJoinedLogEntry(Project project, UserId member) {
        super(LogAction.PROJECT_JOINED, project, member);
    }

    public ProjectJoinedLogEntry() {
        setLogAction(LogAction.PROJECT_JOINED);
    }
}
