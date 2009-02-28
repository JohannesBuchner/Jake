package com.jakeapp.core.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue(value = "JAKE_OBJECT_NEW_VERSION")
public class JakeObjectNewVersionLogEntry extends JakeObjectLogEntry {
    public JakeObjectNewVersionLogEntry(JakeObject belongsTo, UserId member, String comment, String checksum, Boolean processed) {
        super(LogAction.JAKE_OBJECT_NEW_VERSION, belongsTo, member, comment, checksum, processed);
    }


    public JakeObjectNewVersionLogEntry() {
        setLogAction(LogAction.JAKE_OBJECT_NEW_VERSION);
    }
}
