package com.jakeapp.violet.protocol;

import com.jakeapp.violet.model.LogEntry;

public class RequestFileMessage extends Message {
	private LogEntry logEntry;

	public LogEntry getLogEntry() {
		return logEntry;
	}

	public void setLogEntry(LogEntry logEntry) {
		this.logEntry = logEntry;
	}

}
