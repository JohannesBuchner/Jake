package com.jakeapp.violet.protocol.msg;

import java.util.UUID;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.protocol.Message;

/**
 * Newsflash about a new log entry.
 */
public class PokeMessage extends Message {

	private LogEntry logEntry;

	public LogEntry getLogEntry() {
		return logEntry;
	}

	public void setLogEntry(LogEntry logEntry) {
		this.logEntry = logEntry;
	}

	private PokeMessage(UUID projectId, UserId user, LogEntry logEntry) {
		super(projectId, user);
		setLogEntry(logEntry);

	}

	public static PokeMessage createPokeMessage(UUID projectId, UserId user,
			LogEntry logEntry) {
		return new PokeMessage(projectId, user, logEntry);
	}
}
