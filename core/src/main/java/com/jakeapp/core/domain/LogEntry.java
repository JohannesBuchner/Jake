package com.jakeapp.core.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;


/**
 * A Log entry. It consists of an <code> action, timestamp </code> and a
 * <code>comment</code>. It belongs to a LogEntryObject <code>T</code> and it
 * may belong to a <code>ProjectMember</code>. A <code>LogEntry</code> is an
 * object that is synced with other clients. When receiving a
 * <code>LogEntry</code> from another client, it is marked as not processed.
 * When a client may choose to accept the changes described by a
 * <code>LogEntry</code>, it performs the specified actions (e.g. pulling a new
 * version of a file) and marks a <code>LogEntry</code> as processed.
 * 
 * @author Dominik, Simon, christopher
 */

@Entity(name = "logentries")
public class LogEntry<T extends ILogable> implements Serializable {

	protected static Date getTime() {
		return new Date();
	}

	private UUID uuid;

	private LogAction logAction;

	private Date timestamp;

	private T belongsTo;

	private UserId member;

	private String comment;

	private String checksum;

	private String objectuuid;

	private boolean processed;

	private static final long serialVersionUID = 8192394452617311262L;

	/**
	 * Construct a <code>LogEntry</code>with the given params.
	 * 
	 * @param uuid
	 *            the uuid of the <code>LogEntry</code> which identifies this
	 *            <code>LogEntry</code> even between clients.
	 * @param logAction
	 * @param timestamp
	 * @param project
	 * @param belongsTo
	 * @param member
	 * @param comment
	 * @param checksum
	 * @param processed
	 * 
	 * @deprecated try to always use the subclasses (ProjectMemberLogEntry, etc)
	 */
	@Deprecated
	LogEntry(UUID uuid, LogAction logAction, Date timestamp, 
			T belongsTo, UserId member, String comment, String checksum,
			Boolean processed) {
		this.setUuid(uuid);
		this.setLogAction(logAction);
		this.setTimestamp(timestamp);
		this.setBelongsTo(belongsTo);
		this.setMember(member);
		this.setComment(comment);
		this.setChecksum(checksum);
		this.setProcessed(processed);
	}

	public LogEntry() {
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the <code>uuid</code> for this <code>LogEntry</code>.
	 */
	@Transient
	public UUID getUuid() {
		return this.uuid;
	}

	@Id
	@Column(name = "id", nullable = false)
	private String getUUIDString() {
		return this.uuid.toString();
	}

	private void setUUIDString(String uuid) {
		this.uuid = UUID.fromString(uuid);
	}

	/**
	 * @param logAction
	 *            The <code>LogAction</code> indicating what this
	 *            <code>LogEntry</code> describes.
	 */
	public void setLogAction(LogAction logAction) {
		this.logAction = logAction;
	}

	/**
	 * @return The action that caused this <code>LogEntry</code>.
	 */
	@Column(name = "action", nullable = false)
	public LogAction getLogAction() {
		return this.logAction;
	}


	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return The creation date of this <code>LogEntry</code>, when it was
	 *         first created (possibly on another client).
	 */
	@Column(name = "time", nullable = false)
	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setBelongsTo(T belongsTo) {
		this.belongsTo = belongsTo;
	}

	/**
	 * @return An object whose modification caused this <code>LogEntry</code> to
	 *         be created. Since not all LogEntries are caused by an object,
	 *         <code>null</code> can be returned as well.
	 */
	@Lob
	@Column(name = "belongsto", nullable = true)
	public T getBelongsTo() {
		return this.belongsTo;
	}

	// public void setMember(ProjectMember member) {
	// this.member = member;
	// }

	/**
	 * @param comment
	 * @return The <code>ProjectMember</code> that caused this
	 *         <code>LogEntry</code>.
	 */
	// @Column(name="memberID", table = "projectmember", nullable = false)
	// @Column(name = "projectmember", nullable = false, columnDefinition =
	// "char(36)")
	// public ProjectMember getMember() {
	// return this.member;
	// }
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return The comment the user entered when performing the action that
	 *         caused the LogEntry. If the user did not enter any comments, an
	 *         empty String is returned.
	 */
	@Column(name = "comment", nullable = true)
	public String getComment() {
		return this.comment;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	/**
	 * @return The hash the modified object had when causing this
	 *         <code>LogEntry</code>.
	 */
	@Column(name = "hash", nullable = true)
	public String getChecksum() {
		return this.checksum;
	}

	public void setProcessed(Boolean processed) {
		this.processed = processed;
	}

	/**
	 * @return <code>true</code>, if this Logentry corresponds to the current
	 *         version of the Object the LogEntry refers to, or it the version
	 *         of the corresponding Object is even newer.
	 */
	@Column(name = "processed", nullable = false)
	public boolean isProcessed() {
		return this.processed;
	}

	// @ManyToOne(optional = false)
	// @Column(name = "projectmember", table = "projectmember")
	// @ForeignKey(name = "projectmember" )
	// @Column(name = "projectmember", columnDefinition = "char(36)")

	// @ManyToOne

	@Lob
	@Column(name = "issuer", nullable = true)
	public UserId getMember() {
		return member;
	}

	public void setMember(UserId member) {
		this.member = member;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + (isProcessed() ? "P " : "NP")
				+ "]: " + getLogAction().toString() + " for " + getObjectuuid() + "["
				+ getBelongsTo() + "] " + " by " + getMember() + " @" + getTimestamp()
				+ " (checksum=" + getChecksum() + ")";
	}


	@Column(name = "objectuuid", nullable = true)
	public String getObjectuuid() {
		return objectuuid;
	}

	protected void setObjectuuid(String objectuuid) {
		this.objectuuid = objectuuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((belongsTo == null) ? 0 : belongsTo.hashCode());
		result = prime * result + ((checksum == null) ? 0 : checksum.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((logAction == null) ? 0 : logAction.hashCode());
		result = prime * result + ((member == null) ? 0 : member.hashCode());
		result = prime * result + ((objectuuid == null) ? 0 : objectuuid.hashCode());
		result = prime * result + (processed ? 1231 : 1237);
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		LogEntry other = (LogEntry) obj;
		if (belongsTo == null) {
			if (other.belongsTo != null)
				return false;
		} else if (!belongsTo.equals(other.belongsTo))
			return false;
		if (checksum == null) {
			if (other.checksum != null)
				return false;
		} else if (!checksum.equals(other.checksum))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (logAction == null) {
			if (other.logAction != null)
				return false;
		} else if (!logAction.equals(other.logAction))
			return false;
		if (member == null) {
			if (other.member != null)
				return false;
		} else if (!member.equals(other.member))
			return false;
		if (objectuuid == null) {
			if (other.objectuuid != null)
				return false;
		} else if (!objectuuid.equals(other.objectuuid))
			return false;
		if (processed != other.processed)
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
