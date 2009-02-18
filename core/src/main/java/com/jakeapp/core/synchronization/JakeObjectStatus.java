package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ProjectMember;

/**
 * @author johannes
 */
public class JakeObjectStatus {

	private LockStatus lockStatus;

	private Existance existance;

	private SyncStatus syncStatus;

	private ProjectMember lastVersionProjectMember;
	private ProjectMember lockOwner;

	/**
	 * * @param lastVersionLogAction
	 * @param lastVersionProjectMember
	 * @param lockOwner
	 * @param lastLockLogAction
	 * @param objectExistsLocally
	 * @param checksumDifferentFromLastNewVersionLogEntry
	 * @param hasUnprocessedLogEntries
	 * @param lastProcessedLogAction			@see {@link com.jakeapp.core.synchronization.LockStatus#getLockStatus(com.jakeapp.core.domain.LogAction)},
 *      {@link com.jakeapp.core.synchronization.Existance#getExistance(boolean, com.jakeapp.core.domain.LogAction)}
 *      {@link com.jakeapp.core.synchronization.SyncStatus#getSyncStatus(boolean, boolean, com.jakeapp.core.domain.LogAction, boolean)}
	 */
	public JakeObjectStatus(LogAction lastVersionLogAction, ProjectMember lastVersionProjectMember,
					ProjectMember lockOwner, LogAction lastLockLogAction, boolean objectExistsLocally,
					boolean checksumDifferentFromLastNewVersionLogEntry, boolean hasUnprocessedLogEntries, LogAction lastProcessedLogAction) {
		this.lockStatus = LockStatus.getLockStatus(lastLockLogAction);
		this.existance = Existance
				.getExistance(objectExistsLocally, lastVersionLogAction);
		this.syncStatus = SyncStatus.getSyncStatus(
				checksumDifferentFromLastNewVersionLogEntry, hasUnprocessedLogEntries,
				lastProcessedLogAction, objectExistsLocally);
		this.lastVersionProjectMember = lastVersionProjectMember;
		this.lockOwner = lockOwner;
	}

	public ProjectMember getLastVersionProjectMember() {
		return lastVersionProjectMember;
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
		return (getExistance() == Existance.EXISTS_LOCAL || getExistance() == Existance.EXISTS_ON_BOTH)
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
	 * @return ProjectMember aka lock owner, or null
	 */
	public ProjectMember getLockOwner() {
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
		return getExistance() == Existance.EXISTS_LOCAL;
	}

	public boolean isOnlyRemote() {
		return getExistance() == Existance.EXISTS_REMOTE;
	}

	public boolean isLocalLatest() {
		return isLocalAndRemote() && isInSync();
	}

	public boolean isInSync() {
		return getSyncStatus() == SyncStatus.SYNC;
	}

	public boolean isLocalAndRemote() {
		return getExistance() == Existance.EXISTS_ON_BOTH;
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
		return getClass().getSimpleName() + ":" + getExistance() + ":" + getSyncStatus()
				+ ":" + getLockStatus();
	}
}
