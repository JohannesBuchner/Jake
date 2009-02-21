package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.UserId;
import org.apache.log4j.Logger;

/**
 * This is the JakeObjectStatus - the base class for AttributedJakeObject.
 * Describing Infos about a domain object are saved here.
 * Primary User: FileObject, NoteObject.
 *
 * @author johannes, peter
 */
public class JakeObjectStatus {
	static final Logger log = Logger.getLogger(JakeObjectStatus.class);

	private Existence existence;
	private SyncStatus syncStatus;
	private LogEntry<? extends ILogable> lastVersionLogEntry;
	private LogEntry<? extends ILogable> lastLockLogEntry;

	/**
	 * * @param lastVersionLogAction
	 *
	 * @param lastVersionLogEntry
	 * @param lastLockLogEntry
	 * @param objectExistsLocally
	 * @param checksumDifferentFromLastNewVersionLogEntry
	 * @param hasUnprocessedLogEntries
	 * @param lastProcessedLogAction		 @see {@link com.jakeapp.core.synchronization.LockStatus#getLockStatus(com.jakeapp.core.domain.LogAction)}
	 *      ,
	 *      {@link com.jakeapp.core.synchronization.Existence#getExistance(boolean, com.jakeapp.core.domain.LogAction)}
	 *      {@link com.jakeapp.core.synchronization.SyncStatus#getSyncStatus(boolean, boolean, com.jakeapp.core.domain.LogAction, boolean)}
	 */
	public JakeObjectStatus(LogEntry<? extends ILogable> lastVersionLogEntry, LogEntry<? extends ILogable> lastLockLogEntry,
					boolean objectExistsLocally, boolean checksumDifferentFromLastNewVersionLogEntry,
					boolean hasUnprocessedLogEntries, LogAction lastProcessedLogAction) {
		log.debug("generating JakeObjectStatus: " + ": lastVersionLogEntry:"
				+ lastVersionLogEntry + " lastLockLogEntry:" + lastLockLogEntry + " objectExistsLocally:"
				+ objectExistsLocally + " checksumDifferentFromLastNewVersionLogEntry:"
				+ checksumDifferentFromLastNewVersionLogEntry
				+ " hasUnprocessedLogEntries:" + hasUnprocessedLogEntries
				+ " lastProcessedLogAction:" + lastProcessedLogAction);
		this.lastVersionLogEntry = lastVersionLogEntry;
		this.lastLockLogEntry = lastLockLogEntry;

		this.existence = Existence
				.getExistance(objectExistsLocally, getLastVersionLogEntryLogAction());
		this.syncStatus = SyncStatus.getSyncStatus(
				checksumDifferentFromLastNewVersionLogEntry, hasUnprocessedLogEntries,
				lastProcessedLogAction, objectExistsLocally);
		log.debug("result:" + this.toString());
	}

	private LogAction getLastVersionLogEntryLogAction() {
		return getFailsafeLogAction(this.getLastVersionLogEntry());
	}
	
	private LogAction getFailsafeLogAction(LogEntry<? extends ILogable> le) {
		if(le == null) {
			return null;
		}else{
			return le.getLogAction();
		}
	}
	
	private UserId getNullsafeMember(LogEntry<? extends ILogable> le) {
		if(le == null) {
			return null;
		}else{
			return le.getMember();
		}
	}


	public LogEntry<? extends ILogable> getLastVersionLogEntry() {
		return this.lastVersionLogEntry;
	}

	/**
	 * Returns the Last Editor of this Object.
	 * Failsafe variant.
	 * @return returns null of no LogEntry
	 */
	public UserId getLastVersionEditor() {
		return getNullsafeMember(getLastVersionLogEntry());
	}

	public LockStatus getLockStatus() {
		return LockStatus.getLockStatus(getLockEntryAction());
	}

	private LogAction getLockEntryAction() {
		return getFailsafeLogAction(getLockLogEntry());
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
	 * If locked, this returns the lock entry
	 * 
	 * @return lock entry or null
	 */
	public LogEntry<? extends ILogable> getLockLogEntry() {
		return this.lastLockLogEntry;
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
