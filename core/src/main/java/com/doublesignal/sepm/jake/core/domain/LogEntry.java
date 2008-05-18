package com.doublesignal.sepm.jake.core.domain;

import java.util.Date;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 8, 2008
 * Time: 11:04:28 PM
 */
public class LogEntry {
	private LogAction action;
	private Date timestamp;
	private String comment;
	private String jakeObjectName;


	public LogAction getAction()
	{
		return action;
	}

	public void setAction(LogAction action)
	{
		this.action = action;
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public String getJakeObjectName()
	{
		return jakeObjectName;
	}

	public void setJakeObjectName(String jakeObjectName)
	{
		this.jakeObjectName = jakeObjectName;
	}

	public LogEntry()
	{
	}

	public LogEntry(LogAction action, Date timestamp, String jakeObjectName, String comment )
	{
		this.action = action;
		this.timestamp = timestamp;
		this.comment = comment;
		this.jakeObjectName = jakeObjectName;
	}


	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		LogEntry logEntry = (LogEntry) o;

		if (action != logEntry.action)
		{
			return false;
		}
		if (comment != null ? !comment.equals(logEntry.comment) : logEntry.comment != null)
		{
			return false;
		}
		if (jakeObjectName != null ? !jakeObjectName.equals(logEntry.jakeObjectName) : logEntry.jakeObjectName != null)
		{
			return false;
		}
		if (timestamp != null ? !timestamp.equals(logEntry.timestamp) : logEntry.timestamp != null)
		{
			return false;
		}

		return true;
	}

	public int hashCode()
	{
		int result;
		result = (action != null ? action.hashCode() : 0);
		result = 28 * result + (timestamp != null ? timestamp.hashCode() : 0);
		result = 28 * result + (comment != null ? comment.hashCode() : 0);
		result = 28 * result + (jakeObjectName != null ? jakeObjectName.hashCode() : 0);
		return result;
	}
}
