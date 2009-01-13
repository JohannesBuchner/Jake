package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;

import java.awt.event.ActionEvent;

import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;

public class InspectorFileAction extends FileAction {
	public InspectorFileAction(JTable fileTable) {
		super(fileTable);

		String actionStr = JakeMainView.getMainView().getResourceMap().
			 getString("showHideInspectorMenuItem.text");

		putValue(Action.NAME, actionStr);

		// only enable if exact one element is selected.
		setEnabled(fileTable.getSelectedRowCount() == 1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: Implement me!
	}
}