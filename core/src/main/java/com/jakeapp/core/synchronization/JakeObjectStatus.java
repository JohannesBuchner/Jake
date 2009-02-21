package com.jakeapp.core.synchronization;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.UserId;

/**
 * @author johannes
 */
public class JakeObjectStatus {

	static final Logger log = Logger.getLogger(JakeObjectStatus.class);

	private LockStatus lockStatus;

	private Existence existence;

	private SyncStatus syncStatus;

	private UserId lastVersionProjectMember;

	private UserId lockOwner;

	/**
	 * * @param lastVersionLogAction
	 * 
	 * @param lastVersionProjectMember
	 * @param lockOwner
	 * @param lastLockLogAction
	 * @param objectExistsLocally
	 * @param checksumDifferentFromLastNewVersionLogEntry
	 * @param hasUnprocessedLogEntries
	 * @param lastProcessedLogAction
	 * @see {@link com.jakeapp.core.synchronization.LockStatus#getLockStatus(com.jakeapp.core.domain.LogAction)}
	 *      ,
	 *      {@link com.jakeapp.core.synchronization.Existence#getExistance(boolean, com.jakeapp.core.domain.LogAction)}
	 *      {@link com.jakeapp.core.synchronization.SyncStatus#getSyncStatus(boolean, boolean, com.jakeapp.core.domain.LogAction, boolean)}
	 */
	public JakeObjectStatus(LogAction lastVersionLogAction,
			UserId lastVersionProjectMember, UserId lockOwner,
			LogAction lastLockLogAction, boolean objectExistsLocally,
			boolean checksumDifferentFromLastNewVersionLogEntry,
			boolean hasUnprocessedLogEntries, LogAction lastProcessedLogAction) {
		log.debug("generating JakeObjectStatus: " + ": lastVersionLogAction:"
				+ lastVersionLogAction + " lastVersionProjectMember:"
				+ lastVersionProjectMember + " lockOwner:" + lockOwner
				+ " lastLockLogAction:" + lastLockLogAction + " objectExistsLocally:"
				+ objectExistsLocally + " checksumDifferentFromLastNewVersionLogEntry:"
				+ checksumDifferentFromLastNewVersionLogEntry
				+ " hasUnprocessedLogEntries:" + hasUnprocessedLogEntries
				+ " lastProcessedLogAction:" + lastProcessedLogAction);
		this.lockStatus = LockStatus.getLockStatus(lastLockLogAction);
		this.existence = Existence
				.getExistance(objectExistsLocally, lastVersionLogAction);
		this.syncStatus = SyncStatus.getSyncStatus(
				checksumDifferentFromLastNewVersionLogEntry, hasUnprocessedLogEntries,
				lastProcessedLogAction, objectExistsLocally);
		this.lastVersionProjectMember = lastVersionProjectMember;
		this.lockOwner = lockOwner;
		log.debug("result:" + this.toString());
	}

	public UserId getLastVersionProjectMember() {
		return this.lastVersionProjectMember;
	}

	public LockStatus getLockStatus() {
		return this.lockStatus;
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
	public boolean isNewVersionable() {
		return (getExistence() == Existence.EXISTS_LOCAL || getExistence() == Existence.EXISTS_ON_BOTH)
				&& getSyncStatus() != SyncStatus.SYNC;
	}

	/**
	 * @return does deleting make any sense?
	 */
	public boolean isDeletable() {
		return true;
	}

	/**
	 * @return does locking make any sense?
	 */
	public boolean isUnlocked() {
		return getLockStatus() == LockStatus.OPEN;
	}

	/**
	 * @return does unlocking make any sense?
	 */
	public boolean isLocked() {
		return getLockStatus() == LockStatus.CLOSED;
	}

	/**
	 * If locked, this returns the lock owner.
	 * 
	 * @return ProjectMember aka lock owner, or null
	 */
	public UserId getLockOwner() {
		return lockOwner;
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
		return getClass().getSimpleName() + ":" + getExistence() + ":" + getSyncStatus()
				+ ":" + getLockStatus();
	}
}
