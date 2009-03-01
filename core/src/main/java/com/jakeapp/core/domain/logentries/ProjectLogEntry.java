package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.LogAction;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.UUID;
import java.io.Serializable;

@Entity
public class ProjectLogEntry extends LogEntry<Project> implements Serializable {
    private static final long serialVersionUID = -8773156028147182736L;

    @Transient
    private Project project;

    public ProjectLogEntry(Project project, UserId member) {
        super(UUID.randomUUID(),
				LogAction.PROJECT_CREATED, getTime(), project, member, null,
				null, true);
        this.project = project;
    }

    public void setProject(Project p)
    {
        this.project = p;
    }

    public Project getProject()
    {
        return project;
    }

    public ProjectLogEntry() {
    }


    public String toString()
    {
        return "";
    }


    

}
