package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.*;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue(value = "TAG_ADD")
public class TagAddLogEntry extends TagLogEntry {

	public TagAddLogEntry(Tag belongsTo, User member) {
		super(LogAction.TAG_ADD, belongsTo, member);
	}

	public TagAddLogEntry() {
		setLogAction(LogAction.TAG_ADD);
	}


	public static TagAddLogEntry parse(LogEntry<? extends ILogable> logEntry) {

		if (!logEntry.getLogAction().equals(LogAction.TAG_ADD))
			throw new UnsupportedOperationException();

		TagAddLogEntry le = new TagAddLogEntry((Tag) logEntry.getBelongsTo(), logEntry
				.getMember());
		le.setTimestamp(logEntry.getTimestamp());
		return le;
	}

}
