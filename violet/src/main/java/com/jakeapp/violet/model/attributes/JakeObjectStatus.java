package com.jakeapp.violet.model.attributes;

import org.apache.log4j.Logger;

import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.User;

/**
 * This is the JakeObjectStatus - the base class for AttributedJakeObject.
 * Describing Infos about a domain object are saved here. Primary User:
 * FileObject, NoteObject.
 * 
 * @author johannes, peter
 */
public class JakeObjectStatus {

	static final Logger log = Logger.getLogger(JakeObjectStatus.class);

	private Existence existence;

	private SyncStatus syncStatus;

	private LogEntry lastVersionLogEntry;

	/**
	 * @param lastVersionLogEntry
	 *            The logentry that describes the last known version of the
	 *            JakeObject, either on this computer or remote.
	 * @param lastLockLogEntry
	 *            The logentry that is the last lock or unlock-entry of the
	 *            JakeObject. If it is of type
	 *            {@link LogAction#JAKE_OBJECT_LOCK}, the JakeObject can be
	 *            considered locked, if it is of type
	 *            {@link LogAction#JAKE_OBJECT_UNLOCK}, the JakeObject can be
	 *            considered unlocked.
	 * @param objectExistsLocally
	 *            true if there is a local version of the JakeObject.
	 * @param checksumDifferentFromLastNewVersionLogEntry
	 *            true, if the JakeObject has a different checksum as the one
	 *            listed in the log and thus can be regarded as modified.
	 * @param hasUnprocessedLogEntries
	 *            true if there are LogEntries for this JakeObject that have not
	 *            been processed yet.
	 * @param lastProcessedLogAction
	 *            LogAction of the newest processed LogEntry.
	 * @see {@link com.jakeapp.core.synchronization.attributes.LockStatus#getLockStatus(com.jakeapp.core.domain.LogAction)}
	 *      ,
	 *      {@link com.jakeapp.core.synchronization.attributes.Existence#getExistance(boolean, com.jakeapp.core.domain.LogAction)}
	 *      {@link com.jakeapp.core.synchronization.attributes.SyncStatus#getSyncStatus(boolean, boolean, com.jakeapp.core.domain.LogAction, boolean)}
	 */
	public JakeObjectStatus(LogEntry lastVersionLogEntry,
			boolean objectExistsLocally,
			boolean checksumDifferentFromLastNewVersionLogEntry,
			boolean hasUnprocessedLogEntries) {
		this.lastVersionLogEntry = lastVersionLogEntry;

		boolean lastWasNewVersion = getLastVersionLogEntryWasNewVersion();
		this.existence = Existence.getExistance(objectExistsLocally,
				lastWasNewVersion);
		this.syncStatus = SyncStatus.getSyncStatus(
				checksumDifferentFromLastNewVersionLogEntry,
				hasUnprocessedLogEntries, lastWasNewVersion,
				objectExistsLocally);
	}

	private boolean getLastVersionLogEntryWasNewVersion() {
		return getFailsafeLogAction(this.getLastVersionLogEntry());
	}

	private boolean getFailsafeLogAction(LogEntry le) {
		if (le == null) {
			return false;
		} else {
			boolean delete = le.getHow() == null || le.getHow().isEmpty();
			return !delete;
		}
	}

	private User getNullsafeMember(LogEntry le) {
		if (le == null) {
			return null;
		} else {
			return le.getWho();
		}
	}

	public LogEntry getLastVersionLogEntry() {
		return this.lastVersionLogEntry;
	}

	/**
	 * Returns the Last Editor of this Object. Failsafe variant.
	 * 
	 * @return returns null of no LogEntry
	 */
	public User getLastVersionEditor() {
		return getNullsafeMember(getLastVersionLogEntry());
	}

	public Existence getExistence() {
		return this.existence;
	}

	public SyncStatus getSyncStatus() {
		return this.syncStatus;
	}

	/**
	 * @return does requesting a pull make any sense?
	 */
	public boolean isPullable() {
		return getSyncStatus() != SyncStatus.SYNC;
	}

	/**
	 * @return does releasing a new version make any sense?
	 */
	public boolean isAnnouncable() {
		return (getExistence() == Existence.EXISTS_LOCAL || getExistence() == Existence.EXISTS_ON_BOTH)
				&& getSyncStatus() != SyncStatus.SYNC;
	}

	/**
	 * @return does deleting make any sense?
	 */
	public boolean isDeletable() {
		return getExistence() != Existence.NON_EXISTANT;
	}

	/**
	 * Checks if the object is in conflict.
	 * 
	 * @return
	 */
	public boolean isInConflict() {
		return getSyncStatus() == SyncStatus.CONFLICT;
	}

	public boolean isOnlyLocal() {
		return getExistence() == Existence.EXISTS_LOCAL;
	}

	public boolean isOnlyRemote() {
		return getExistence() == Existence.EXISTS_REMOTE;
	}

	public boolean isLocalLatest() {
		return isLocalAndRemote() && isInSync();
	}

	public boolean isInSync() {
		return getSyncStatus() == SyncStatus.SYNC;
	}

	public boolean isLocalAndRemote() {
		return getExistence() == Existence.EXISTS_ON_BOTH;
	}

	public boolean isLocal() {
		return isOnlyLocal() || isLocalAndRemote();
	}

	public boolean isModifiedLocally() {
		return getSyncStatus() == SyncStatus.MODIFIED_LOCALLY;
	}

	public boolean isModifiedRemote() {
		return getSyncStatus() == SyncStatus.MODIFIED_REMOTELY;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + getExistence() + ":"
				+ getSyncStatus();
	}
}
