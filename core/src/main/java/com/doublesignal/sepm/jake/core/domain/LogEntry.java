package com.doublesignal.sepm.jake.core.domain;

import java.util.Date;

/**
 * A Log entry. It consists of a <code> action, timestamp, jakoObjectName </code>
 * and a <code>comment</code>.
 * 
 * @author Dominik, Simon
 */
public class LogEntry {
	
	private LogAction action;
	private Date timestamp;
	private String jakeObjectName;
	private String hash;
	private String userId;
	private String comment;
	
	/**
	 * Construct a new LogEntry with the given params.
	 * @param action The <code>LogAction</code> to be logged
	 * @param timestamp The time of the log entry
	 * @param jakeObjectName The name of the <code>JakeObject</code> this log
	 * entry corresponds to.
	 * @param comment An arbitrary comment
	 */
	public LogEntry(LogAction action, Date timestamp, String jakeObjectName, 
			String hash, String userId, String comment) {
		this.action = action;
		this.timestamp = timestamp;
		this.comment = comment;
		this.jakeObjectName = jakeObjectName;
		this.hash = hash;
		this.userId = userId;
	}

	public LogAction getAction() {
		return action;
	}

	public void setAction(LogAction action) {
		this.action = action;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getJakeObjectName() {
		return jakeObjectName;
	}

	public void setJakeObjectName(String jakeObjectName) {
		this.jakeObjectName = jakeObjectName;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * calculates and returns the hash value of the log entry
	 * @return hash code
	 */
	public int hashCode() {
		int result;
		result = (action != null ? action.hashCode() : 0);
		result = 28 * result + (timestamp != null ? timestamp.hashCode() : 0);
		result = 28 * result + (comment != null ? comment.hashCode() : 0);
		result = 28 * result + (jakeObjectName != null ? jakeObjectName.hashCode() : 0);
		return result;
	}

	/**
	 * Test if two LogEntrys are equal.
	 * @return <code>true</code> iff all fields are equal.
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
	
		LogEntry logEntry = (LogEntry) o;
	
		if (action != logEntry.action) {
			return false;
		}
		if (comment != null ? !comment.equals(logEntry.comment) : logEntry.comment != null) {
			return false;
		}
		if (jakeObjectName != null ? !jakeObjectName.equals(logEntry.jakeObjectName) : logEntry.jakeObjectName != null) {
			return false;
		}
		if (timestamp != null ? !timestamp.equals(logEntry.timestamp) : logEntry.timestamp != null) {
			return false;
		}
	
		return true;
	}
}
