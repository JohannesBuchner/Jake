package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;

/**
 * @author johannes
 */
public class AttributedJakeObject extends JakeObjectStatus {

	/**
	 * 
	 * @see JakeObjectStatus#JakeObjectStatus(LogAction, LogAction, boolean,
	 *      boolean, boolean, LogAction)
	 * 
	 * @param jakeObject
	 * @param lastVersionLogAction
	 * @param lastLockLogAction
	 * @param objectExistsLocally
	 * @param checksumDifferentFromLastNewVersionLogEntry
	 * @param hasUnprocessedLogEntries
	 * @param lastProcessedLogAction
	 * @param lastModificationDate
	 */
	public AttributedJakeObject(JakeObject jakeObject, LogAction lastVersionLogAction,
			LogAction lastLockLogAction, boolean objectExistsLocally,
			boolean checksumDifferentFromLastNewVersionLogEntry,
			boolean hasUnprocessedLogEntries, LogAction lastProcessedLogAction,
			long lastModificationDate) {
		super(lastVersionLogAction, lastLockLogAction, objectExistsLocally,
				checksumDifferentFromLastNewVersionLogEntry, hasUnprocessedLogEntries,
				lastProcessedLogAction);
		this.jakeObject = jakeObject;
		this.lastModificationDate = lastModificationDate;
	}

	private JakeObject jakeObject;

	private long lastModificationDate;

	public JakeObject getJakeObject() {
		return this.jakeObject;
	}

	public long getLastModificationDate() {
		return this.lastModificationDate;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getJakeObject() + "] "
				+ super.toString();
	}

}
