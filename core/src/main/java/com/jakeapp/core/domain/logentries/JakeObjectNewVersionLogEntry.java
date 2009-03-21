package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * This <code>LogAction</code> specifies, that a new version of the given <code>JakeObject</code> is available
 * from the <code>User</code> creating this <code>LogEntry</code>. The <code>Checksum</code> of the new
 * <code>JakeObject</code> (in case it is a <code>FileObject</code>) is also saved, to recognize, if
 * this <code>JakeObject</code> already is on the local system.
 */
@Entity
@DiscriminatorValue(value = "JAKE_OBJECT_NEW_VERSION")
public class JakeObjectNewVersionLogEntry extends JakeObjectLogEntry {
	private static final long serialVersionUID = -6219151568430370082L;

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
