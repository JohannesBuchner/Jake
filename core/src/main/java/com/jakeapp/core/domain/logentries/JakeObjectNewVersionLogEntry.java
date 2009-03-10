package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue(value = "JAKE_OBJECT_NEW_VERSION")
public class JakeObjectNewVersionLogEntry extends JakeObjectLogEntry {
    public JakeObjectNewVersionLogEntry(JakeObject belongsTo, User member, String comment, String checksum, Boolean processed) {
        super(LogAction.JAKE_OBJECT_NEW_VERSION, belongsTo, member, comment, checksum, processed);
    }


    public JakeObjectNewVersionLogEntry() {
        setLogAction(LogAction.JAKE_OBJECT_NEW_VERSION);
    }

    public static JakeObjectNewVersionLogEntry parse(LogEntry<? extends ILogable> logEntry) {

        if (!logEntry.getLogAction().equals(LogAction.JAKE_OBJECT_NEW_VERSION))
            throw new UnsupportedOperationException();

        JakeObjectNewVersionLogEntry le = new JakeObjectNewVersionLogEntry(
                (JakeObject) logEntry.getBelongsTo(),
                logEntry.getMember(),
                logEntry.getComment(),
                logEntry.getChecksum(),
                logEntry.isProcessed()
        );
		le.setTimestamp(logEntry.getTimestamp());
		le.setUuid(logEntry.getUuid());
		return le;
    }

}
