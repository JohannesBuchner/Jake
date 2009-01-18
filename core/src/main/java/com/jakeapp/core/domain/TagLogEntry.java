package com.jakeapp.core.domain;

import javax.persistence.Entity;
import java.util.UUID;
import java.util.Date;
import java.io.Serializable;

@Entity
public class TagLogEntry extends LogEntry<Tag> implements Serializable {
    private static final long serialVersionUID = -7799185912611559431L;

    public TagLogEntry(UUID uuid, LogAction logAction, Date timestamp, Project project, Tag belongsTo,
                       ProjectMember member, String comment, String checksum, Boolean processed) {
        super(uuid, logAction, timestamp, project, belongsTo, member, comment, checksum, processed);

        if(belongsTo.getObject() != null && belongsTo.getObject().getUuid() != null)
        this.setObjectuuid(belongsTo.getObject().getUuid().toString());
    }

    public TagLogEntry() {
    }
}
