package com.jakeapp.violet.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.mockito.exceptions.misusing.NullInsteadOfMockException;

public class LogEntry {

	public LogEntry(UUID id, Timestamp when, User who, JakeObject what,
			String why, String how, boolean known) {
		super();
		if (id == null)
			this.id = UUID.randomUUID();
		else
			this.id = id;
		if (when == null)
			this.when = new Timestamp(new Date().getTime());
		else
			this.when = when;
		if (who == null)
			throw new NullPointerException();
		this.who = who;
		if (what == null)
			throw new NullPointerException();
		this.what = what;
		if (why == null)
			this.why = "";
		else
			this.why = why;
		if (how == null)
			this.how = "";
		else
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

	public UUID getId() {
		return id;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (how == null) {
			if (other.how != null)
				return false;
		} else if (!how.equals(other.how))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (known == null) {
			if (other.known != null)
				return false;
		} else if (!known.equals(other.known))
			return false;
		if (what == null) {
			if (other.what != null)
				return false;
		} else if (!what.equals(other.what))
			return false;
		if (when == null) {
			if (other.when != null)
				return false;
		} else if (!when.equals(other.when))
			return false;
		if (who == null) {
			if (other.who != null)
				return false;
		} else if (!who.equals(other.who))
			return false;
		if (why == null) {
			if (other.why != null)
				return false;
		} else if (!why.equals(other.why))
			return false;
		return true;
	}
}
