package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * This <code>LogEntry</code> specifies that the associated <code>JakeObject</code>, be it
 * a <code>FileObject</code> or a <code>NoteObject</code> was deleted by the <code>User</code>
 * specified in the user-field. Additionally the <code>User</code> can enter a message describing
 * why the <code>JakeObject</code> was deleted. 
 */
@Entity
@DiscriminatorValue(value = "JAKE_OBJECT_DELETE")
public class JakeObjectDeleteLogEntry extends JakeObjectLogEntry {

	public JakeObjectDeleteLogEntry(JakeObject belongsTo, User member, String comment,
			Boolean processed) {
		super(LogAction.JAKE_OBJECT_DELETE, belongsTo, member, comment, null,
				processed);
	}

	public JakeObjectDeleteLogEntry() {
		setLogAction(LogAction.JAKE_OBJECT_DELETE);
	}


	public static JakeObjectDeleteLogEntry parse(LogEntry<? extends ILogable> logEntry) {

		if (!logEntry.getLogAction().equals(LogAction.JAKE_OBJECT_DELETE))
			throw new UnsupportedOperationException();

		JakeObjectDeleteLogEntry le = new JakeObjectDeleteLogEntry((JakeObject) logEntry
				.getBelongsTo(), logEntry.getMember(), logEntry.getComment(), logEntry.isProcessed());
		le.setTimestamp(logEntry.getTimestamp());
		le.setUuid(logEntry.getUuid());
		return le;

	}

}
