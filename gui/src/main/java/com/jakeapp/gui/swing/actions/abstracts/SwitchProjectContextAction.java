package com.jakeapp.gui.swing.actions.abstracts;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.callbacks.ContextViewChangedCallback;

/**
 * @author: studpete
 */
public abstract class SwitchProjectContextAction extends JakeAction implements ContextViewChangedCallback {

	public SwitchProjectContextAction() {
		JakeMainView.getMainView().addContextViewChangedListener(this);
		updateAction();
	}

	@Override
	public void setContextViewPanel(JakeMainView.ContextPanelEnum panel) {
		updateAction();
	}

	public void updateAction() {
		this.setEnabled(JakeMainView.getMainView().getContextViewPanel() == JakeMainView.ContextPanelEnum.Project);
	}
}
