package com.jakeapp.gui.swing.callbacks;

import java.util.EnumSet;

/**
 * Callback that is fired when Data changes.
 * Get the data from the core, this is just a message that there's new stuff around.
 */
public interface DataChanged {
	public enum Reason {Projects, User, Files, LogEntries}

	public static EnumSet<Reason> All = EnumSet.allOf(Reason.class);

	public void dataChanged(EnumSet<Reason> reason);
}
