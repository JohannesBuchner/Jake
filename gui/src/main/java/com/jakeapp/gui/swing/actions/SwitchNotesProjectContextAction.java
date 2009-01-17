package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.SwitchProjectContextAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author: studpete
 */
public class SwitchNotesProjectContextAction extends SwitchProjectContextAction {
	public SwitchNotesProjectContextAction() {
		putValue(Action.NAME, JakeMainView.getResouceMap().getString("showNotesMenuItem.text"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JakeMainView.getMainView().setProjectViewPanel(JakeMainView.ProjectViewPanelEnum.Notes);
	}
}