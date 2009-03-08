package com.jakeapp.gui.swing.callbacks;

import java.util.EnumSet;

/**
 * Callback that is fired when a Context changes.
 */
public interface ContextChanged {
	public enum Reason { MsgService, Project, Invitation }

	public static EnumSet<Reason> All = EnumSet.allOf(Reason.class);

	/**
	 * Callback when a context was changed
	 * @param reason
	 * @param context is optional
	 */
	public void contextChanged(EnumSet<Reason> reason, Object context);
}