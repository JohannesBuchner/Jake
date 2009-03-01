package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.core.domain.Project;

import java.util.EnumSet;

/**
 * Callback that is fired when a Property changes.
 */
public interface PropertyChanged {
	public enum Reason {MsgService}

	public static EnumSet<Reason> All = EnumSet.allOf(Reason.class);

	/**
	 * Callback when a property was changed
	 * @param reason
	 * @param p optional variable
	 * @param data optional poperty
	 */
	public void propertyChanged(EnumSet<Reason> reason, Project p, Object data);
}