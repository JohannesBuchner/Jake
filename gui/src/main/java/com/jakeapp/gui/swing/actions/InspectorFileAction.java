package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.helpers.JakeMainHelper;

import java.awt.event.ActionEvent;

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

	public InspectorFileAction(JTable fileTable) {
		super(fileTable);

		putValue(Action.NAME, getName());

		// only enable if exact one element is selected.
		setEnabled(fileTable.getSelectedRowCount() == 1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// If it is visible, hide it, if it's not, show it!
		JakeMainView.getMainView().setInspectorEnabled(!JakeMainView.getMainView().isInspectorEnabled());
	}
}