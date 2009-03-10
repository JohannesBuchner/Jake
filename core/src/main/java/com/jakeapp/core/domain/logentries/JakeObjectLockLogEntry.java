package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;


@Entity
@DiscriminatorValue(value = "JAKE_OBJECT_LOCK")
public class JakeObjectLockLogEntry extends JakeObjectLogEntry {
    public JakeObjectLockLogEntry(JakeObject belongsTo, User member, String comment, Boolean processed) {
        super(LogAction.JAKE_OBJECT_LOCK, belongsTo, member, comment, null, processed);
    }

    public JakeObjectLockLogEntry() {
        setLogAction(LogAction.JAKE_OBJECT_LOCK);
    }


        public static JakeObjectLockLogEntry parse(LogEntry<? extends ILogable> logEntry)
    {

        if (!logEntry.getLogAction().equals(LogAction.JAKE_OBJECT_LOCK))
            throw new UnsupportedOperationException();

        JakeObjectLockLogEntry le = new JakeObjectLockLogEntry(
                    (JakeObject) logEntry.getBelongsTo(),
                    logEntry.getMember(),
                    logEntry.getComment(),
                    logEntry.isProcessed()
            );
		le.setTimestamp(logEntry.getTimestamp());
		le.setUuid(logEntry.getUuid());
		return le;
    }
}
