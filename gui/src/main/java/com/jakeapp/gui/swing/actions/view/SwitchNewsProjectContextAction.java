package com.jakeapp.gui.swing.actions.view;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.SwitchProjectContextAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author: studpete
 */
public class SwitchNewsProjectContextAction extends SwitchProjectContextAction {
	public SwitchNewsProjectContextAction() {
		putValue(Action.NAME, JakeMainView.getResouceMap().getString("showProjectMenuItem.text"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JakeMainView.getMainView().setProjectViewPanel(JakeMainView.ProjectView.News);
	}
}
