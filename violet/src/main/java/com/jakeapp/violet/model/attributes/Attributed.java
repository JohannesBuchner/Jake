package com.jakeapp.violet.model.attributes;

import org.apache.log4j.Logger;

import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;

/**
 * Additional information about the synchronization status of a JakeObject
 * 
 * @author johannes
 */
public class Attributed extends JakeObjectStatus {

	static final Logger log = Logger.getLogger(Attributed.class);

	private JakeObject jakeObject;

	private long lastModificationDate;

	private long size;

	/**
	 * @param jakeObject
	 *            The Jakeobject whose status is described.
	 * @param lastVersionLogEntry
	 *            {@link JakeObjectStatus}
	 * @param lastLockLogEntry
	 *            {@link JakeObjectStatus}
	 * @param objectExistsLocally
	 *            {@link JakeObjectStatus}
	 * @param checksumDifferentFromLastNewVersionLogEntry
	 *            {@link JakeObjectStatus}
	 * @param hasUnprocessedLogEntries
	 *            {@link JakeObjectStatus}
	 * @param lastProcessedLogAction
	 *            {@link JakeObjectStatus}
	 * @param lastModificationDate
	 *            Date of the last modification.
	 * @param size
	 *            Size of the <code>JakeObject</code>. Either the length of the
	 *            content for notes or the filesize (in bytes) for files.
	 * */
	public Attributed(JakeObject jakeObject, LogEntry lastVersionLogEntry,
			boolean objectExistsLocally,
			boolean checksumDifferentFromLastNewVersionLogEntry,
			boolean hasUnprocessedLogEntries, long lastModificationDate,
			long size) {
		super(lastVersionLogEntry, objectExistsLocally,
				checksumDifferentFromLastNewVersionLogEntry,
				hasUnprocessedLogEntries);
		this.jakeObject = jakeObject;
		this.lastModificationDate = lastModificationDate;
		this.size = size;
	}

	public long getSize() {
		return this.size;
	}

	public JakeObject getJakeObject() {
		return this.jakeObject;
	}

	/**
	 * Get the last modification date
	 * 
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