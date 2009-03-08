package com.jakeapp.core.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.logentries.LogEntry;

/**
 * The interface for the logEntryDAO.
 */
public interface ILogEntryDao {

	/**
	 * Persists a new LogEntry to the database of one project.
	 * 
	 * @param logEntry
	 *            the <code>LogEntry</code> to persist. It must already be
	 *            associated with a <code>Project</code>.
	 */
	void create(LogEntry<? extends ILogable> logEntry);


	/**
	 * @returns the LogEntry with the given UUID
	 * @param uuid
	 * @throws NoSuchLogEntryException
	 */
	LogEntry<? extends ILogable> get(UUID uuid, boolean includeUnprocessed) throws NoSuchLogEntryException;

	/**
	 * change the &quot;processed&quot; field of a logEntry
	 * 
	 * @param logEntry
	 * @throws NoSuchLogEntryException 
	 */
	public void setProcessed(LogEntry<JakeObject> logEntry) throws NoSuchLogEntryException;


	/**
	 * @return all unprocessed LogEntries of the Project
	 */
	public List<LogEntry<JakeObject>> getUnprocessed();


	/**
	 * @param jakeObject
	 * @return the unprocessed LogEntries of the JakeObject
	 */
	public List<LogEntry<JakeObject>> getUnprocessed(JakeObject jakeObject);


	/**
	 * @param jakeObject
	 * @return Whether unprocessed LogEntries of the JakeObject exist
	 */
	public boolean hasUnprocessed(JakeObject jakeObject);


	/**
	 * @return first LogEntry by timestamp that has not been processed yet
	 * @throws NoSuchLogEntryException
	 *             if there is no unprocessed <code>LogEntry</code>.
	 */
	public LogEntry<JakeObject> getNextUnprocessed()
			throws NoSuchLogEntryException;


	/**
	 * Get all LogEntrys stored in the database for one <code>Project</code>.
	 * 
	 * @param includeUnprocessed
	 *            Whether to look at unprocessed LogEntries as well
	 * @return List of LogEntrys
	 */
	public List<LogEntry<? extends ILogable>> getAll(boolean includeUnprocessed);

	/**
	 * Get all LogEntrys stored in the database concerning a specific
	 * <code>JakeObject</code>.
	 * 
	 * @param jakeObject
	 *            the <code>JakeObject</code> in question.
	 * @param includeUnprocessed
	 *            Whether to look at unprocessed LogEntries as well
	 * @return List of LogEntries
	 */
	public <T extends JakeObject> List<LogEntry<T>> getAllOfJakeObject(T jakeObject,
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
	 */
	public LogEntry<JakeObject> getLastOfJakeObject(JakeObject jakeObject,
			boolean includeUnprocessed) throws NoSuchLogEntryException;

	/**
	 * Get all LogEntrys with {@value LogAction#JAKE_OBJECT_NEW_VERSION} as
	 * LogAction stored in the database.
	 * 
	 * @param jakeObject
	 *            the <code>JakeObject</code> in question.
	 * @param includeUnprocessed
	 *            Whether to look at unprocessed LogEntries as well
	 * @return List of LogEntries
	 */
	public <T extends JakeObject> List<LogEntry<T>> getAllVersions(
			boolean includeUnprocessed);

	/**
	 * Get all LogEntrys with {@value LogAction#JAKE_OBJECT_NEW_VERSION} as
	 * LogAction stored in the database concerning a specific
	 * <code>JakeObject</code>.
	 * 
	 * @param jakeObject
	 *            the <code>JakeObject</code> in question.
	 * @param includeUnprocessed
	 *            Whether to look at unprocessed LogEntries as well
	 * @return List of LogEntries
	 */
	public <T extends JakeObject> List<LogEntry<T>> getAllVersionsOfJakeObject(
			T jakeObject, boolean includeUnprocessed);

	/**
	 * Retrieves the most recent <code>LogEntry</code> of
	 * {@value LogAction#JAKE_OBJECT_NEW_VERSION} for a given
	 * <code>JakeObject</code>.
	 * 
	 * @param jakeObject
	 *            the <code>JakeObject</code> in question.
	 * @param includeUnprocessed
	 *            Whether to look at unprocessed LogEntries as well
	 * @return the most recent <code>LogEntry</code> for this
	 *         <code>JakeObject</code>
	 * @throws NoSuchLogEntryException
	 *             if there is no LogEntry for <code>jakeObject</code>.
	 */
	public <T extends JakeObject> LogEntry<T> getLastVersionOfJakeObject(T jakeObject,
			boolean includeUnprocessed) throws NoSuchLogEntryException;

	/**
	 * looks at all LogEntries that either have a
	 * {@link LogAction#JAKE_OBJECT_DELETE} or
	 * {@link LogAction#JAKE_OBJECT_NEW_VERSION}.
	 * 
	 * <p>
	 * Note the subtle difference to {@link #getLastVersion(JakeObject)}
	 * </p>
	 * 
	 * @param jakeObject
	 * @param includeUnprocessed
	 *            Whether to look at unprocessed LogEntries as well
	 * @return true: if the last in time is a
	 *         {@link LogAction#JAKE_OBJECT_DELETE} <br>
	 *         false: if the last in time is a
	 *         {@link LogAction#JAKE_OBJECT_NEW_VERSION} <br>
	 *         null: if no matching LogEntries could be found
	 */
	public Boolean getDeleteState(JakeObject jakeObject, boolean includeUnprocessed);


	/**
	 * looks at all LogEntries that either have a
	 * {@link LogAction#JAKE_OBJECT_DELETE} or
	 * {@link LogAction#JAKE_OBJECT_NEW_VERSION}.
	 * 
	 * <p>
	 * Note the subtle difference to {@link #getDeleteState(JakeObject)}
	 * </p>
	 * 
	 * @param jakeObject
	 * @param includeUnprocessed
	 *            Whether to look at unprocessed LogEntries as well
	 * @return null: if the last in time is a
	 *         {@link LogAction#JAKE_OBJECT_DELETE} or no LogEntries were found<br>
	 *         the LogEntry of the {@link LogAction#JAKE_OBJECT_NEW_VERSION}
	 *         otherwise
	 */
	public LogEntry<JakeObject> getLastVersion(JakeObject jakeObject,
			boolean includeUnprocessed);


	/**
	 * @param
	 * @return all fileObject that either don't have a
	 *         {@link LogAction#JAKE_OBJECT_DELETE} <b>or</b> have a
	 *         {@link LogAction#JAKE_OBJECT_NEW_VERSION} later than that.
	 * @param includeUnprocessed
	 */
	public List<FileObject> getExistingFileObjects(boolean includeUnprocessed);

	/**
	 * checks if jo is currently locked by looking at all
	 * {@link LogAction#JAKE_OBJECT_LOCK} and
	 * {@link LogAction#JAKE_OBJECT_UNLOCK} entries
	 * 
	 * NOTE: a object might be locked, but also not exist
	 * 
	 * @param belongsTo
	 * @return the LogEntry doing the lock: if the last in time is a
	 *         {@link LogAction#JAKE_OBJECT_LOCK} <br>
	 *         null: if no Logentries were found or last was
	 *         {@link LogAction#JAKE_OBJECT_UNLOCK}
	 */
	public LogEntry<JakeObject> getLock(JakeObject belongsTo);

	/**
	 * Iterates in time through all {@link LogAction#TAG_ADD} and
	 * {@link LogAction#TAG_REMOVE}. <br>
	 * on add, the tag is added to the collection, on remove, the tag is removed
	 * from the collection. At the end, returns the collection.
	 * 
	 * @param belongsTo
	 * @return an empty collection if no tags (not null) or the tags otherwise
	 */
	public Collection<Tag> getTags(JakeObject belongsTo);

	/**
	 * Gets the first {@link LogEntry}. It has the
	 * {@link LogAction#PROJECT_CREATED}.
	 * 
	 * @return the LogEntry that marks the project as created or null,
	 * if no such LogEntry exists.
	 */
	public LogEntry<? extends ILogable> getProjectCreatedEntry();


	/**
	 * Iterates in time through all
	 * {@link LogAction#START_TRUSTING_PROJECTMEMBER} and
	 * {@link LogAction#STOP_TRUSTING_PROJECTMEMBER}. <br>
	 * Keeps a map of who trusts a {@link com.jakeapp.core.domain.User}. returns all
	 * {@link com.jakeapp.core.domain.User} that at the end of the time have people that trust
	 * them. Also looks at the {@value LogAction#PROJECT_CREATED} at the
	 * beginning.
	 * 
	 * @param belongsTo
	 * @return an empty collection if no projectmembers (not null) or the tags
	 *         otherwise
	 */
	public List<User> getCurrentProjectMembers();

	/**
	 * Does a trust b?
	 * 
	 * @param a
	 * @param b
	 * @return false if no logentries found or last was
	 *         {@link LogAction#STOP_TRUSTING_PROJECTMEMBER}
	 */
	public boolean trusts(User a, User b);

	/**
	 * Does a trust b?
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public TrustState trustsHow(User a, User b);

	/**
	 * Whom does a trust?
	 * 
	 * @param a
	 * @deprecated don't know if needed
	 * @return
	 */
	@Deprecated
	public Map<User, TrustState> trustsHow(User a);

	/**
	 * @return a mapping of the trusted users for each user (A trusts [B, C, D])
	 *         must not be null
	 */
	public Map<User, List<User>> getTrustGraph();


	/**
	 * @return a mapping of truststate to user for each user (A trusts [B full, C not, D normal])
	 */
	public Map<User, Map<User, TrustState>> getExtendedTrustGraph();


	/**
	 * This methods sets all LogEntries with the same belongsTo object
	 * previous to this one to processed 
	 * @param logEntry
	 */
	public void setAllPreviousProcessed(LogEntry<? extends ILogable> logEntry);

	/**
	 * This method creates the required logentries when accepting an invitation.
	 * This is currently only the LogEntry stating that we trust the other member.
	 * @param invitation The Invitation to be accepted
	 */
	public void acceptInvitation(Invitation invitation);
}
