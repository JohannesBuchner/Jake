package com.jakeapp.violet.model;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.jakeapp.violet.model.exceptions.NoSuchLogEntryException;

public interface Log {

	/*
	 * here should be something smart, e.g. the query functions on the log
	 */

	/**
	 * connect to the log. Do this after creating this
	 * 
	 * @throws Exception
	 */
	void connect() throws SQLException;

	/**
	 * connect to the log. Do this after creating this
	 * 
	 * @throws Exception
	 */
	void disconnect() throws SQLException;

	void addModificationListener(ILogModificationListener l);

	void removeModificationListener(ILogModificationListener l);

	void add(LogEntry logEntry);

	/**
	 * Retrieve a </code>LogEntry</code>.
	 * 
	 * @param uuid
	 *            the uuid of the requested </code>LogEntry</code>
	 * @returns the LogEntry with the given UUID
	 * @throws NoSuchLogEntryException
	 * @throws SQLException
	 */
	LogEntry getById(UUID uuid, boolean includeUnprocessed)
			throws NoSuchLogEntryException;

	/**
	 * change the <code>processed</code> field of a logEntry. Note that the
	 * logEntry handed in is now invalid.
	 * 
	 * @param logEntry
	 *            the <code>LogEntry</code> that is to be changed.
	 * @throws NoSuchLogEntryException
	 * @throws SQLException
	 */
	void setProcessed(LogEntry logEntry) throws NoSuchLogEntryException;

	/**
	 * Get all unprocessed <code>LogEntries</code>.
	 * 
	 * @return all unprocessed LogEntries of the Project
	 * @throws SQLException
	 */
	List<LogEntry> getUnprocessed();

	/**
	 * Get the unprocessed <code>LogEntries</code> for a specific
	 * <code>JakeObject</code>.
	 * 
	 * @param jakeObject
	 * @return the unprocessed LogEntries of the specific JakeObject
	 * @throws SQLException
	 */
	List<LogEntry> getUnprocessed(JakeObject jakeObject);

	/**
	 * Check if a <code>JakeObject</code> has unprocessed
	 * <code>LogEntries</code>
	 * 
	 * @param jakeObject
	 * @return Whether unprocessed LogEntries of the JakeObject exist
	 * @throws SQLException
	 */
	boolean hasUnprocessed(JakeObject jakeObject);

	/**
	 * @return first LogEntry by timestamp that has not been processed yet
	 * @throws NoSuchLogEntryException
	 *             if there is no unprocessed <code>LogEntry</code>.
	 * @throws SQLException
	 */
	LogEntry getNextUnprocessed() throws NoSuchLogEntryException;

	/**
	 * Get all LogEntrys stored in the database for one <code>Project</code>.
	 * 
	 * @param includeUnprocessed
	 *            Whether to look at unprocessed LogEntries as well
	 * @return List of LogEntrys
	 * @throws SQLException
	 */
	List<LogEntry> getAll(boolean includeUnprocessed);

	/**
	 * Get all LogEntrys stored in the database concerning a specific
	 * <code>JakeObject</code>.
	 * 
	 * @param jakeObject
	 *            the <code>JakeObject</code> in question.
	 * @param includeUnprocessed
	 *            Whether to look at unprocessed LogEntries as well
	 * @return List of LogEntries
	 * @throws SQLException
	 */
	List<LogEntry> getAllOfJakeObject(JakeObject jakeObject,
			boolean includeUnprocessed);

	/**
	 * Retrieves the most recent <code>LogEntry</code> of any LogAction for a
	 * given <code>JakeObject</code>.
	 * 
	 * @param jakeObject
	 *            the <code>JakeObject</code> in question.
	 * @param includeUnprocessed
	 *            Whether to look at unprocessed LogEntries as well
	 * @return the most recent <code>LogEntry</code> for this
	 *         <code>JakeObject</code>
	 * @throws NoSuchLogEntryException
	 *             if there is no LogEntry for <code>jakeObject</code>.
	 * @throws SQLException
	 */
	LogEntry getLastOfJakeObject(JakeObject jakeObject,
			boolean includeUnprocessed) throws NoSuchLogEntryException;

	/**
	 * @param includeUnprocessed
	 * @return all fileObject that either don't have a
	 *         {@link LogAction#JAKE_OBJECT_DELETE} <b>or</b> have a
	 *         {@link LogAction#JAKE_OBJECT_NEW_VERSION} later than that.
	 * @throws SQLException
	 */
	List<JakeObject> getExistingFileObjects(boolean includeUnprocessed);

	/**
	 * Gets the first {@link LogEntry}.
	 * 
	 * @throws NoSuchLogEntryException
	 * @throws SQLException
	 */
	public LogEntry getFirstEntry() throws NoSuchLogEntryException;

	/**
	 * This methods sets all LogEntries with the same belongsTo object previous
	 * to this one to processed
	 * 
	 * @param logEntry
	 * @throws SQLException
	 */
	public void setAllPreviousProcessed(LogEntry logEntry);

}
