package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.LogAction;

/**
 * @author johannes
 */
public class JakeObjectStatus {

	private LockStatus lockStatus;

	private Existance existance;

	private SyncStatus syncStatus;

	public JakeObjectStatus(LogAction lastVersionLogAction,
													LogAction lastLockLogAction, boolean objectExistsLocally,
													boolean checksumDifferentFromLastNewVersionLogEntry,
													boolean hasUnprocessedLogEntries,
													LogAction lastProcessedLogAction) {
		this.lockStatus = LockStatus.getLockStatus(lastLockLogAction);
		this.existance = Existance
						.getExistance(objectExistsLocally, lastVersionLogAction);
		this.syncStatus = SyncStatus
						.getSyncStatus(checksumDifferentFromLastNewVersionLogEntry,
										hasUnprocessedLogEntries, lastProcessedLogAction,
										objectExistsLocally);
	}


	public LockStatus getLockStatus() {
		return this.lockStatus;
	}


	public Existance getExistance() {
		return this.existance;
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
		return (getExistance() == Existance.EXISTS_LOCAL || getExistance() == Existance.EXISTS_ON_BOTH) && getSyncStatus() != SyncStatus.SYNC;
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
	public boolean isLockable() {
		return getLockStatus() == LockStatus.OPEN;
	}

	/**
	 * @return does unlocking make any sense?
	 */
	public boolean isUnLockable() {
		return getLockStatus() == LockStatus.CLOSED;
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
		return getExistance() == Existance.EXISTS_LOCAL;
	}

	public boolean isOnlyRemote() {
		return getExistance() == Existance.EXISTS_REMOTE;
	}

	public boolean isLocalLatest() {
		return isLocalAndRemote() && isInSync();
	}

	private boolean isInSync() {
		return getSyncStatus() == SyncStatus.SYNC;
	}

	private boolean isLocalAndRemote() {
		return getExistance() == Existance.EXISTS_ON_BOTH;
	}

	public boolean isLocalModified() {
		return isLocalAndRemote() && isModifiedLocally();
	}

	public boolean isModifiedLocally() {
		return getSyncStatus() == SyncStatus.MODIFIED_LOCALLY;
	}

	public boolean isModifiedRemote() {
		return getSyncStatus() == SyncStatus.MODIFIED_REMOTELY;
	}


	@Override
	public String toString() {
		return getClass()
						.getSimpleName() + ":" + getExistance() + ":" + getSyncStatus() + ":" + getLockStatus();
	}
}
