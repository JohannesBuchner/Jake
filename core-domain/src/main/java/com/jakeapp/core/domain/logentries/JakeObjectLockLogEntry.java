package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;


/**
 * This <code>LogEntry</code> specifies, that a certain <code>JakeObject</code> was <em>Softlocked</em> by
 * the <code>User</code> creating this <code>LogEntry</code>. An additional <code>comment</code> can be added
 * to the <code>Lock</code> specifying why the <code>Lock</code> was issued.
 */
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
