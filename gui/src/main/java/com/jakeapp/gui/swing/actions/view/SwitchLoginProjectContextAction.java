package com.jakeapp.gui.swing.actions.view;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.SwitchProjectContextAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author: studpete
 */
public class SwitchLoginProjectContextAction extends SwitchProjectContextAction {
	public SwitchLoginProjectContextAction() {
		putValue(Action.NAME, JakeMainView.getResouceMap().getString("showLoginMenuItem.text"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JakeMainView.getMainView().setContextViewPanel(JakeMainView.ContextPanelEnum.Login);
	}

	@Override
	public void updateAction() {
		// fixme: this is a bug, userpanel has too many states, extract "your current user-state"
		this.setEnabled(JakeMainView.getMainView().getContextViewPanel() != JakeMainView.ContextPanelEnum.Login);
	}
}