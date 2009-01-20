package com.jakeapp.gui.swing.actions;

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
		this.setEnabled(JakeMainView.getMainView().getContextViewPanel() != JakeMainView.ContextPanelEnum.Login);
	}
}