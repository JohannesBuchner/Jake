package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.helpers.JakeMainHelper;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;

import java.awt.event.ActionEvent;
import java.util.List;

import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;

public class InspectorFileAction extends FileAction {
	private String getName() {
		return JakeMainView.getMainView().getResourceMap().
			 getString(
				  JakeMainView.getMainView().isInspectorEnabled() ?
						"hideInspectorMenuItem.text" :
						"showInspectorMenuItem.text"
			 );
	}

	public InspectorFileAction() {
		super();

		putValue(Action.NAME, getName());

		// only enable if exact one element is selected.
		updateAction();
	}

	@Override
	public void updateAction() {
		setEnabled(getSelectedRowCount() == 1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// If it is visible, hide it, if it's not, show it!
		JakeMainView.getMainView().setInspectorEnabled(!JakeMainView.getMainView().isInspectorEnabled());
	}
}