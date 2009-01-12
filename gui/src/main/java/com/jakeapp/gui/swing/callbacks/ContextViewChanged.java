package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.gui.swing.JakeMainView;

/**
 * Interface for context view changed callback.
 * User: studpete
 * Date: Dec 30, 2008
 * Time: 8:44:22 PM
 */
public interface ContextViewChanged {
	public void setContextViewPanel(JakeMainView.ContextPanelEnum panel);
}