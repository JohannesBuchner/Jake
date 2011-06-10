package com.jakeapp.violet.model;

import java.sql.Timestamp;
import java.util.UUID;

public class LogEntry {

	public LogEntry(UUID id, Timestamp when, User who, JakeObject what,
			String why, String how, Boolean known) {
		super();
		this.id = id;
		this.when = when;
		this.who = who;
		this.what = what;
		this.why = why;
		this.how = how;
		this.known = known;
	}

	/**
	 * Id of the log entry
	 */
	private UUID id;
	/**
	 * Time stamp of the action
	 */
	private Timestamp when;
	/**
	 * User Id of the culprit
	 */
	private User who;
	/**
	 * reference to the object modified (i.e. the relpath of the file)
	 */
	private JakeObject what;
	/**
	 * an optional commit msg.
	 */
	private String why;
	/**
	 * a hash of the file if not empty
	 */
	private String how;
	/**
	 * true if we have processed this log entry.
	 */
	private Boolean known;

	public Timestamp getWhen() {
		return when;
	}

	public User getWho() {
		return who;
	}

	public JakeObject getWhat() {
		return what;
	}

	public String getWhy() {
		return why;
	}

	public String getHow() {
		return how;
	}

	public Boolean getKnown() {
		return known;
	}

	public UUID getId() {
		return id;
	}
}
