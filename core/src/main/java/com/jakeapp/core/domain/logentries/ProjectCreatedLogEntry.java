package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.LogAction;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue(value = "PROJECT_CREATED")
public class ProjectCreatedLogEntry extends ProjectLogEntry {
    public ProjectCreatedLogEntry(Project project, UserId member) {
        super(LogAction.PROJECT_CREATED, project, member);
    }

    public ProjectCreatedLogEntry() {
        setLogAction(LogAction.PROJECT_CREATED);
    }

    
}
