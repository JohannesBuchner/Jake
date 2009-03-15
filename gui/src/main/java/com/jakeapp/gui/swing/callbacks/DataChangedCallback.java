package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.core.domain.Project;

import java.util.EnumSet;

/**
 * Callback that is fired when Data changes.
 * Get the data from the core, this is just a message that there's new stuff around.
 */
public interface DataChangedCallback {
	public enum DataReason {Projects, User, Files, Notes, LogEntries}

	public static EnumSet<DataReason> ALL = EnumSet.allOf(DataReason.class);

	/**
	 * Callback when new data is in the object cache
	 * @param dataReason
	 * @param p optional variable
	 */
	public void dataChanged(EnumSet<DataReason> dataReason, Project p);
}
