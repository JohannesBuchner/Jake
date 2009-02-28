package com.jakeapp.core.domain;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue(value = "JAKE_OBJECT_UNLOCK")
public class JakeObjectUnlockLogEntry extends JakeObjectLogEntry {
    public JakeObjectUnlockLogEntry(JakeObject belongsTo, UserId member, String comment, String checksum, Boolean processed) {
        super(LogAction.JAKE_OBJECT_UNLOCK, belongsTo, member, comment, checksum, processed);
    }


    public JakeObjectUnlockLogEntry() {
        setLogAction(LogAction.JAKE_OBJECT_UNLOCK);
    }
}
