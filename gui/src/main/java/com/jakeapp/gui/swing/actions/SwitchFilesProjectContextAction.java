package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.SwitchProjectContextAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author: studpete
 */
public class SwitchFilesProjectContextAction extends SwitchProjectContextAction {
	public SwitchFilesProjectContextAction() {
		putValue(Action.NAME, JakeMainView.getResouceMap().getString("showFilesMenuItem.text"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JakeMainView.getMainView().setProjectViewPanel(JakeMainView.ProjectView.Files);
	}
}