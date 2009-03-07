package com.jakeapp.core.synchronization.attributes;

import com.jakeapp.core.domain.LogAction;

/**
 * @author johannes
 */
public enum LockStatus {
	OPEN,

	CLOSED, ;

	/**
	 * @param lastLockLogAction
	 *            {@link LogAction#JAKE_OBJECT_LOCK} or
	 *            {@link LogAction#JAKE_OBJECT_UNLOCK} or null.
	 * @return
	 */
	static LockStatus getLockStatus(LogAction lastLockLogAction) {
		if (lastLockLogAction == LogAction.JAKE_OBJECT_UNLOCK || lastLockLogAction == null)
			return OPEN;
		else if (lastLockLogAction == LogAction.JAKE_OBJECT_LOCK)
			return CLOSED;
		else
			throw new IllegalStateException();
	}
}
