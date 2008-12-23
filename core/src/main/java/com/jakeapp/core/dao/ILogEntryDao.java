package com.jakeapp.core.dao;

import java.util.Date;
import java.util.List;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;

/**
 * The interface for the logEntryDAO.
 */
public interface ILogEntryDao {
    /**
     * Persists a new LogEntry to the database of one project.
     *
     * @param logEntry the <code>LogEntry</code> to persist. It must already be
     *                 associated with a <code>Project</code>.
     */
    void create(LogEntry<? extends ILogable> logEntry);


    //FIXME hier sind einige Parameter unnoetig - beim Suchen nach LogEntries
    // wird man wohl kaum so viel Information haben.
    /**
     * Loads a specific LogEntry from the Database
     *
     * @param project       project in which 
     *   the <code>LogEntry</code> is searched.
     * @param name          the name of the jakeObject
     * @param projectmember the ProjectMember of this logEntry
     * @param timestamp     the timestamp of this logEntry
     * @return the LogEntry requested
     * @throws NoSuchLogEntryException if no such LogEntry exists.
     * @throws NoSuchProjectException  if the <code>Project</code> referenced by
     *                                 <code>project</code> does not exist.
     */
    public LogEntry<? extends ILogable> get(Project project,
                                            String name,
                                            String projectmember,
                                            Date timestamp)
            throws NoSuchLogEntryException, NoSuchProjectException;

    /**
     * Get all LogEntrys stored in the database for one <code>Project</code>.
     * This includes
     * not only unprocessed, but also processed LogEntries.
     *
     * @param project The <code>Project</code> to get all LogEntries for.
     * @return List of LogEntrys
     */
    public List<LogEntry<? extends ILogable>> getAll(Project project)
            throws NoSuchProjectException;

    /**
     * Get all LogEntrys stored in the database concerning a specific
     * <code>JakeObject</code>.
     *
     * @param jakeObject the <code>JakeObject</code> in question.
     * @return List of LogEntries
     */
    public <T extends JakeObject> List<LogEntry<T>>
    getAllOfJakeObject(T jakeObject);

    /**
     * Retrieves the most recent <code>LogEntry</code> for a given
     * <code>JakeObject</code>
     *
     * @param jakeObject the <code>JakeObject</code> in question.
     * @return the most recent <code>LogEntry</code> for this
     * <code>JakeObject</code>
     * @throws NoSuchLogEntryException if there is no LogEntry for
     * <code>jakeObject</code>.
     */
    public LogEntry<? extends ILogable> getMostRecentFor(JakeObject jakeObject)
            throws NoSuchLogEntryException;

    /**
     * Retrieves the log entry representing the last pulled version for the
     * given <code>JakeObject</code>.
     * The last pulled version denotes the newest version that was downloaded
     * from another peer, without
     * local modifications.
     *
     * @param jakeObject the <code>JakeObject</code> in question.
     * @return the <code>LogEntry</code> representing the last pulled version.
     * @throws NoSuchLogEntryException If there is not <code>LogEntry</code> for
     *  <code>jakeObject</code>
     */
    public <T extends JakeObject> LogEntry<T> getLastPulledFor(T jakeObject)
            throws NoSuchLogEntryException;

    /**
     * change the &quot;processed&quot; field of a logEntry
     *
     * @param logEntry the <T extends JakeObject>  in question
     */
    public void setProcessed(LogEntry<? extends ILogable> logEntry);

    /**
     * @return A list of all LogEntries for a <code>Project</code> that have
     * not been processed yet.
     * @throws NoSuchProjectException if the <code>Project</code> referenced by
     * <code>project</code> does not exist.
     */
    public List<LogEntry<? extends ILogable>> getAllUnprocessed(Project project)
            throws NoSuchProjectException;

    /**
     * @param project The <code>Project</code> to get
     * unprocessed LogEntries for.
     * @return any LogEntry that has not been processed yet.
     * @throws NoSuchProjectException  if the <code>Project</code>
     * referenced by <code>project</code> does not exist.
     * @throws NoSuchLogEntryException if there is no unprocessed
     * <code>LogEntry</code>.
     */
	public LogEntry<? extends ILogable> getUnprocessed(Project project)
		throws NoSuchProjectException, NoSuchLogEntryException;
}
