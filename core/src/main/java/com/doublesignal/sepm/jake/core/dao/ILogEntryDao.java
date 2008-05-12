package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.exceptions.NoSuchLogEntryException;

import java.util.Date;
import java.util.List;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 9, 2008
 * Time: 1:11:46 AM
 */
public interface ILogEntryDao
{

    /**
     * Persist a new LogEntry to the database.
     * @param logEntry
     * @return the LogEntry persisted.
     */
    public LogEntry create(LogEntry logEntry);

    /**
     * Loads a specific LogEntry from the Database
     * @param name
     * @param type
     * @param timestamp
     * @return the LogEntry requested
     * @throws NoSuchLogEntryException if no such LogEntry exists
     */
    public LogEntry load(String name, String type, Date timestamp)
            throws NoSuchLogEntryException;

    /**
     * Get all LogEntrys stored in the database.
     * @return List of LogEntrys
     */
    public List<LogEntry> getAll();

    /**
     * Get all LogEntrys stored in the database concerning a specific JakeObject
     * @param jakeObject
     * @return List of LogEntrys
     */
    public List<LogEntry> getAllOfJakeObject(JakeObject jakeObject);


}
