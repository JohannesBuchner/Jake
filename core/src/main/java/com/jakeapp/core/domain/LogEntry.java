package com.jakeapp.core.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * A Log entry. It consists of an <code> action, timestamp </code>
 * and a <code>comment</code>. It belongs to a LogEntryObject <code>T</code>
 * and it may belong to a <code>ProjectMember</code>.
 * A <code>LogEntry</code> is an object that is synced with other clients.
 * When receiving
 * a <code>LogEntry</code> from another client, it is marked as not processed.
 * When a client may choose to accept the changes described by a
 * <code>LogEntry</code>,
 * it performs the specified actions (e.g. pulling a new version of a file)
 *  and marks a <code>LogEntry</code> as processed.
 * @author Dominik, Simon, christopher
 */
@Entity
public class LogEntry<T extends ILogable> {
	private UUID uuid;
    private LogAction logAction;
    private Date timestamp;
    private Project project;
    private T belongsTo;

    private ProjectMember member;
    private String comment;
    private String checksum;
	private boolean processed;

    {
    	this.processed = false;
    }

    /**
     * Construct a <code>LogEntry</code>with the given params.
     * @param uuid the uuid of the <code>LogEntry</code> which identifies
     * this <code>LogEntry</code> even between clients.
     * @param logAction
     * @param timestamp
     * @param project
     * @param belongsTo
     * @param member
     * @param comment
     * @param checksum
     * @param processed
     */
	public LogEntry(UUID uuid, LogAction logAction, Date timestamp,
			Project project, T belongsTo, ProjectMember member,
			String comment, String checksum, Boolean processed) {
		this.setUuid(uuid);
		this.setLogAction(logAction);
		this.setTimestamp(timestamp);
		this.setProject(project);
		this.setBelongsTo(belongsTo);
		this.setMember(member);
		this.setComment(comment);
		this.setChecksum(checksum);
		this.setProcessed(processed);
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the <code>uuid</code> for this <code>LogEntry</code>.
	 */
	@Id
	@Column(name="ID", nullable=false)
	public UUID getUuid() {
		return this.uuid;
	}

	/**
	 * @param logAction The <code>LogAction</code> indicating what this
     * <code>LogEntry</code> describes.
	 */
	public void setLogAction(LogAction logAction) {
		this.logAction = logAction;
	}

	/**
	 * @return The action that caused this <code>LogEntry</code>.
	 */
	@Column(name="action",nullable=false)
	public LogAction getLogAction() {
		return this.logAction;
	}

	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return The creation date of this <code>LogEntry</code>,
     * when it was first created (possibly on another client).
	 */
	@Column(name="time",nullable=false)
	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	/**
	 * @return the project this LogEntry was created for.
	 */
	public Project getProject() {
		return this.project;
	}

	public void setBelongsTo(T belongsTo) {
		this.belongsTo = belongsTo;
	}

	/**
	 * @return An object whose modification caused this
     * <code>LogEntry</code> to be created.
	 * Since not all LogEntries are caused by an object,
     * <code>null</code> can be returned as well.
	 */
	public T getBelongsTo() {
		return this.belongsTo;
	}

	public void setMember(ProjectMember member) {
		this.member = member;
	}

	/**
	 * @return The <code>ProjectMember</code> that caused this
     * <code>LogEntry</code>.
	 */
	@Column(name="memberID", table = "projectmember", nullable = false)
	public ProjectMember getMember() {
		return this.member;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return The comment the user entered when performing the action
	 * that caused the LogEntry. If the user did not enter any comments,
     * an empty String is returned.
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
     * <code>LogEntry</code>.
	 */
	@Column(name = "hash", nullable = false)
	public String getChecksum() {
		return this.checksum;
	}

	public void setProcessed(Boolean processed) {
		this.processed = processed;
	}

	/**
	 * @return <code>true</code>, if this Logentry corresponds to the
	 *  current version of the Object the LogEntry refers to, or it the
	 *  version of the corresponding Object is even newer.
	 */
	@Column(name = "processed", nullable = false)
	public boolean isProcessed() {
		return this.processed;
	}
}
