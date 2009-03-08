package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.gui.swing.JakeMainView;

/**
 * Interface for context view changed callback.
 */
public interface ContextViewChanged {
	public void setContextViewPanel(JakeMainView.ContextPanelEnum panel);
}