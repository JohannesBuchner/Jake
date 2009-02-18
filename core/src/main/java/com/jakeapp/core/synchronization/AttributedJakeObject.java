package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.ProjectMember;

/**
 * @author johannes
 */
public class AttributedJakeObject<T extends JakeObject> extends JakeObjectStatus {
	
	/**
	 * @param jakeObject
	 * @param lastVersionLogAction
	 * @param lastVersionProjectMember
	 * @param lockOwner
	 *@param lastLockLogAction
	 * @param objectExistsLocally
	 * @param checksumDifferentFromLastNewVersionLogEntry
*@param hasUnprocessedLogEntries
	 * @param lastProcessedLogAction
	 * @param lastModificationDate			 @see JakeObjectStatus#JakeObjectStatus(com.jakeapp.core.domain.LogAction,com.jakeapp.core.domain.ProjectMember,com.jakeapp.core.domain.LogAction,boolean,boolean,boolean,com.jakeapp.core.domain.LogAction)
	 */
	public AttributedJakeObject(T jakeObject, LogAction lastVersionLogAction,
					ProjectMember lastVersionProjectMember, ProjectMember lockOwner, LogAction lastLockLogAction,
					boolean objectExistsLocally,
					boolean checksumDifferentFromLastNewVersionLogEntry,
					boolean hasUnprocessedLogEntries, LogAction lastProcessedLogAction,
					long lastModificationDate, long size) {
		super(lastVersionLogAction, lastVersionProjectMember, lockOwner, lastLockLogAction,
						objectExistsLocally, checksumDifferentFromLastNewVersionLogEntry,
						hasUnprocessedLogEntries, lastProcessedLogAction);
		this.jakeObject = jakeObject;
		this.lastModificationDate = lastModificationDate;
		this.size = size;
	}

	private T jakeObject;

	private long lastModificationDate;

	private long size;

	public long getSize() {
		return this.size;
	}

	public T getJakeObject() {
		return this.jakeObject;
	}

	public long getLastModificationDate() {
		return this.lastModificationDate;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getJakeObject() + "] " + super
						.toString();
	}
}
