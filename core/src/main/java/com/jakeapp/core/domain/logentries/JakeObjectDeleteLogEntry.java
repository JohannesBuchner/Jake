package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.LogAction;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue(value = "JAKE_OBJECT_DELETE")
public class JakeObjectDeleteLogEntry extends JakeObjectLogEntry {
    public JakeObjectDeleteLogEntry(JakeObject belongsTo, UserId member, String comment, String checksum, Boolean processed) {
        super(LogAction.JAKE_OBJECT_DELETE, belongsTo, member, comment, checksum, processed);
    }

    public JakeObjectDeleteLogEntry() {
        setLogAction(LogAction.JAKE_OBJECT_DELETE);
    }
}
