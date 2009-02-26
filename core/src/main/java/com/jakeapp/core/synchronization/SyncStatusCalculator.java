package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.LogAction;

/**
 * struct for giving over to the gui
 * 
 * @author johannes
 */
public class SyncStatusCalculator {

	/**
	 * @param checksumDifferentFromLastNewVersionLogEntry
	 *            Looking at the last LogEntry is sufficient. LogEntry means
	 *            either {@link LogAction#JAKE_OBJECT_NEW_VERSION} or
	 *            {@link LogAction#JAKE_OBJECT_DELETE}. if no LogEntries ->
	 *            false.
	 * @param hasUnprocessedLogEntries
	 *            Last LogEntry means either
	 *            {@link LogAction#JAKE_OBJECT_NEW_VERSION} or
	 *            {@link LogAction#JAKE_OBJECT_DELETE}. if no LogEntries -> any
	 *            value
	 * @param lastProcessedLogAction
	 *            {@link LogAction#JAKE_OBJECT_NEW_VERSION} or
	 *            {@link LogAction#JAKE_OBJECT_DELETE} or null.
	 * @param objectExistsLocally
	 *            File is in FileSystem, Note is associated to Project
	 */
	public SyncStatusCalculator(boolean checksumDifferentFromLastNewVersionLogEntry,
			boolean hasUnprocessedLogEntries, LogAction lastProcessedLogAction,
			boolean objectExistsLocally) {
		super();
		this.isChecksumDifferentFromLastNewVersionLogEntry = checksumDifferentFromLastNewVersionLogEntry;
		this.hasUnprocessedLogEntries = hasUnprocessedLogEntries;
		this.lastProcessedLogEntryType = lastProcessedLogAction;
		this.objectExistsLocally = objectExistsLocally;
	}

	private boolean objectExistsLocally;

	private boolean isChecksumDifferentFromLastNewVersionLogEntry;

	private LogAction lastProcessedLogEntryType;

	private boolean hasUnprocessedLogEntries;

	public SyncStatus getSyncStatus() {
		if (isRemoteDifferent()) {
			if (locallyModified()) {
				return SyncStatus.CONFLICT;
			} else {
				return SyncStatus.MODIFIED_REMOTELY;
			}
		} else {
			if (locallyModified()) {
				return SyncStatus.MODIFIED_LOCALLY;
			} else {
				return SyncStatus.SYNC;
			}
		}
	}

	private boolean isRemoteDifferent() {
		return hasUnprocessedLogEntries;
	}

	private boolean locallyModified() {
		if (lastProcessedLogEntryType == LogAction.JAKE_OBJECT_NEW_VERSION)
			return !objectExistsLocally || isChecksumDifferentFromLastNewVersionLogEntry;
		else if (lastProcessedLogEntryType == LogAction.JAKE_OBJECT_DELETE)
			return objectExistsLocally;
		else if (lastProcessedLogEntryType == null)
			return objectExistsLocally;
		throw new IllegalStateException();
	}
}
