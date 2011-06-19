/**
 * 
 */
package com.jakeapp.violet.model.attributes;

import com.jakeapp.violet.actions.project.local.SyncStatusCalculator;

public enum SyncStatus {
	/** no difference **/
	SYNC,

	/**
	 * you did something. you can announce it.
	 */
	MODIFIED_LOCALLY,

	/**
	 * someone did something. you can pull it.
	 */
	MODIFIED_REMOTELY,

	/**
	 * you and someone else did something. you got to sort it out.
	 */
	CONFLICT;

	/**
	 * @param checksumDifferentFromLastNewVersionLogEntry
	 * @param hasUnprocessedLogEntries
	 * @param lastProcessedLogAction
	 * @param objectExistsLocally
	 * @see SyncStatusCalculator#SyncStatusCalculator(boolean, boolean,
	 *      LogAction, boolean)
	 * @return
	 */
	static SyncStatus getSyncStatus(
			boolean checksumDifferentFromLastNewVersionLogEntry,
			boolean hasUnprocessedLogEntries, boolean lastWasNewVersion,
			boolean objectExistsLocally) {
		return new SyncStatusCalculator(
				checksumDifferentFromLastNewVersionLogEntry,
				hasUnprocessedLogEntries, lastWasNewVersion,
				objectExistsLocally).getSyncStatus();
	}
}