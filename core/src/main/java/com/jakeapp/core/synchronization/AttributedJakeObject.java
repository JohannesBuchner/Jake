package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import org.apache.log4j.Logger;

/**
 * @author johannes
 */
public class AttributedJakeObject<T extends JakeObject> extends JakeObjectStatus {

	static final Logger log = Logger.getLogger(AttributedJakeObject.class);
	private T jakeObject;
	private long lastModificationDate;
	private long size;

	/**
	 * @param jakeObject
	 * @param lastVersionLogEntry
	 * @param lastLockLogEntry
	 * @param objectExistsLocally
	 * @param checksumDifferentFromLastNewVersionLogEntry
	 * @param hasUnprocessedLogEntries
	 * @param lastProcessedLogAction
	 * @param lastModificationDate
	 * @param size
	 * */
	public AttributedJakeObject(T jakeObject, LogEntry<? extends ILogable> lastVersionLogEntry,
			LogEntry<? extends ILogable> lastLockLogEntry, boolean objectExistsLocally,
			boolean checksumDifferentFromLastNewVersionLogEntry,
			boolean hasUnprocessedLogEntries, LogAction lastProcessedLogAction,
			long lastModificationDate, long size) {
		super(lastVersionLogEntry, lastLockLogEntry, objectExistsLocally,
				checksumDifferentFromLastNewVersionLogEntry, hasUnprocessedLogEntries,
				lastProcessedLogAction);
		this.jakeObject = jakeObject;
		this.lastModificationDate = lastModificationDate;
		this.size = size;
	}

	public long getSize() {
		return this.size;
	}

	/**
	 * Get JakeObject, typesave
	 * @return
	 */
	public T getJakeObject() {
		return this.jakeObject;
	}

	/**
	 * Get the last modification date
	 * @return
	 */
	public long getLastModificationDate() {
		return this.lastModificationDate;
	}


	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getJakeObject() + "] "
				+ super.toString();
	}
}