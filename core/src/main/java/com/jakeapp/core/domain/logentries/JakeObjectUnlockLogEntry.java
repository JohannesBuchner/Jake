package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.Entity;
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


    public static JakeObjectUnlockLogEntry parse(LogEntry<? extends ILogable> logEntry) {

        if (!logEntry.getLogAction().equals(LogAction.JAKE_OBJECT_UNLOCK))
            throw new UnsupportedOperationException();

        return new JakeObjectUnlockLogEntry(
                (JakeObject) logEntry.getBelongsTo(),
                logEntry.getMember(),
                logEntry.getComment(),
                logEntry.getChecksum(),
                logEntry.isProcessed()
        );
    }
}
