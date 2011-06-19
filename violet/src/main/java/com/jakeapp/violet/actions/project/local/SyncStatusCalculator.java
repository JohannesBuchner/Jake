package com.jakeapp.violet.actions.project.local;

import com.jakeapp.violet.model.attributes.SyncStatus;

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
	 * @param lastWasNewVersion
	 *            false if no logentry for this object, true if the last
	 *            logentry was a normal commit, false if it was a delete
	 * @param objectExistsLocally
	 *            File is in FileSystem, Note is associated to Project
	 */
	public SyncStatusCalculator(
			boolean checksumDifferentFromLastNewVersionLogEntry,
			boolean hasUnprocessedLogEntries, Boolean lastWasNewVersion,
			boolean objectExistsLocally) {
		super();
		this.isChecksumDifferentFromLastNewVersionLogEntry = checksumDifferentFromLastNewVersionLogEntry;
		this.hasUnprocessedLogEntries = hasUnprocessedLogEntries;
		this.lastWasNewVersion = lastWasNewVersion;
		this.objectExistsLocally = objectExistsLocally;
	}

	private boolean objectExistsLocally;

	private boolean isChecksumDifferentFromLastNewVersionLogEntry;

	private boolean lastWasNewVersion;

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
		if (lastWasNewVersion == true)
			return !objectExistsLocally
					|| isChecksumDifferentFromLastNewVersionLogEntry;
		return objectExistsLocally;
	}
}
