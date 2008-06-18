package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchLogEntryException;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.LogEntry;

import java.util.Date;
import java.util.List;

/**
 * Serves as a frontend for database-independent LogEntry management.
 */
public interface ILogEntryDao {

	/**
	 * Persist a new LogEntry to the database.
	 *
	 * @param logEntry the logEntry to persist
	 */
	public void create(LogEntry logEntry);

	/**
	 * Loads a specific LogEntry from the Database
	 *
	 * @param name the name of the jakeObject
	 * @param projectmember the ProjectMember of this logEntry
	 * @param timestamp the timestamp of this logEntry
	 * @return the LogEntry requested
	 * @throws NoSuchLogEntryException if no such LogEntry exists
	 */
	public LogEntry get(String name, String projectmember, Date timestamp)
			  throws NoSuchLogEntryException;

	/**
	 * Get all LogEntrys stored in the database.
	 *
	 * @return List of LogEntrys
	 */
	public List<LogEntry> getAll();

	/**
	 * Get all LogEntrys stored in the database concerning a specific JakeObject
	 *
	 * @param jakeObject the jakeObject in question
	 * @return List of LogEntrys
	 */
	public List<LogEntry> getAllOfJakeObject(JakeObject jakeObject);

	/**
	 * Retrieves the most recent log entry for a given JakeObject
	 *
	 * @param jakeObject the jakeObject in question
	 * @return the most recent LogEntry for this jakeObject
	 * @throws NoSuchLogEntryException
	 */
	public LogEntry getMostRecentFor(JakeObject jakeObject) throws NoSuchLogEntryException;

	/**
	 * Retrieves the log entry representing the last pulled version for the given JakeObject
	 *
	 * @param jakeObject the jakeObject in question
	 * @return the logEntry representing the last pulled version
	 * @throws NoSuchLogEntryException
	 */
	public LogEntry getLastPulledFor(JakeObject jakeObject) throws NoSuchLogEntryException;

    /**
     * change the &quot;isLastPulled&quot; field of a logEntry
     * @param logEntry the logEntry in question 
     */
	public void setIsLastPulled(LogEntry logEntry);
}
