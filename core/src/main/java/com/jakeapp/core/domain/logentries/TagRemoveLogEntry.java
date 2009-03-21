package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ILogable;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * This <code>LogEntry</code> specifies that the given <code>Tag</code> was removed from a <code>JakeObject</code>
 * by the <code>User</code> creating this <code>LogEntry</code>
 * The programmer has to make sure, that the corresponding <code>JakeObject</code> is set inside the <code>Tag</code>.
 * TODO: This will be changed in the final version. 
 */
@Entity
@DiscriminatorValue(value = "TAG_REMOVE")
public class TagRemoveLogEntry extends TagLogEntry {

    public TagRemoveLogEntry(Tag belongsTo, User member) {
        super(LogAction.TAG_REMOVE, belongsTo, member);
    }

    public TagRemoveLogEntry() {
        setLogAction(LogAction.TAG_REMOVE);
    }


    public static TagRemoveLogEntry parse(LogEntry<? extends ILogable> logEntry) {

        if (!logEntry.getLogAction().equals(LogAction.TAG_REMOVE))
            throw new UnsupportedOperationException();

        TagRemoveLogEntry le = new TagRemoveLogEntry((Tag) logEntry.getBelongsTo(), logEntry.getMember());
		le.setTimestamp(logEntry.getTimestamp());
		le.setUuid(logEntry.getUuid());
		return le;
    }
}
