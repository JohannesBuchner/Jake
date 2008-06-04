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
	private String userId;
	private String hash;
	private String comment;
	private boolean isLastPulled;
	
	/**
	 * Construct a new LogEntry with the given params.
	 * @param action The <code>LogAction</code> to be logged
	 * @param timestamp The time of the log entry
	 * @param jakeObjectName The name of the <code>JakeObject</code> this log
	 * entry corresponds to.
	 * @param userId The ProjectMember who did this
	 * @param hash The hash over the (updated) object (SHA512)
	 * @param comment An arbitrary comment
	 */
	public LogEntry(LogAction action, Date timestamp, String jakeObjectName, 
			String hash, String userId, String comment) {
		this.action = action;
		this.timestamp = timestamp;
		this.jakeObjectName = jakeObjectName;
		this.userId = userId;
		this.hash = hash;
		this.comment = comment;
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

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((action == null) ? 0 : action.hashCode());
		result = PRIME * result + ((comment == null) ? 0 : comment.hashCode());
		result = PRIME * result + ((hash == null) ? 0 : hash.hashCode());
		result = PRIME * result + ((jakeObjectName == null) ? 0 : jakeObjectName.hashCode());
		result = PRIME * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = PRIME * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final LogEntry other = (LogEntry) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		if (jakeObjectName == null) {
			if (other.jakeObjectName != null)
				return false;
		} else if (!jakeObjectName.equals(other.jakeObjectName))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	public boolean getIsLastPulled() {
		return isLastPulled;
	}

	public void setIsLastPulled(boolean isLastPulled) {
		this.isLastPulled = isLastPulled;
	}
}
