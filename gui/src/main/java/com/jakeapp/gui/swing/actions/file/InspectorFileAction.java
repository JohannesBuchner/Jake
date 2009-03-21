package com.jakeapp.gui.swing.actions.file;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class InspectorFileAction extends FileAction {
	public InspectorFileAction() {
		super();

		putValue(Action.NAME, getName());

		setEnabled(true);
	}

	@Override
	public void updateAction() {
		// TODO: direct hook on expector change -> make event!
		putValue(Action.NAME, getName());
	}

	private String getName() {
		return JakeMainView.getMainView().getResourceMap().
						getString(JakeMainView.getMainView().isInspectorEnabled() ?
										"hideInspectorMenuItem.text" : "showInspectorMenuItem.text");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// If it is visible, hide it, if it's not, show it!
		JakeMainView.getMainView()
						.setInspectorEnabled(!JakeMainView.getMainView().isInspectorEnabled());
	}
}