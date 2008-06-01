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
	 * @param logEntry
	 */
	public void create(LogEntry logEntry);

	/**
	 * Loads a specific LogEntry from the Database
	 *
	 * @param name
	 * @param projectmember
	 * @param timestamp
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
	 * @param jakeObject
	 * @return List of LogEntrys
	 */
	public List<LogEntry> getAllOfJakeObject(JakeObject jakeObject);

	/**
	 * Retrieves the most recent log entry for a given JakeObject
	 * @param jakeObject
	 * @return
	 * @throws NoSuchLogEntryException
	 */
	public LogEntry getMostRecentFor(JakeObject jakeObject) throws NoSuchLogEntryException;
}
