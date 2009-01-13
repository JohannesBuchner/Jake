package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;

import java.awt.event.ActionEvent;
import java.io.File;

import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;

public class LockFileAction extends FileAction {
	public LockFileAction(JTable fileTable) {
		super(fileTable);

		String actionStr = JakeMainView.getMainView().getResourceMap().
			 getString("lockMenuItem.text");

		putValue(Action.NAME, actionStr);

		// only enable if exact one element is selected AND that element is NOT a folder.
		boolean enabled = (fileTable.getSelectedRowCount() == 1 &&
			 ((ProjectFilesTreeNode) fileTable.getValueAt(fileTable.getSelectedRow(), 0)).isFile());
		setEnabled(enabled);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: Implement me!
	}
}