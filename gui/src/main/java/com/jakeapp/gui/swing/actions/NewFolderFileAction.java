package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;

public class NewFolderFileAction extends FileAction {
	public NewFolderFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
			 getString("newFolderMenuItem.text");

		putValue(Action.NAME, actionStr);

		updateAction();
	}

	@Override
	public void updateAction() {
		// only enable if exact one element is selected AND that element is a folder.
		boolean enabled = (getSelectedRowCount() == 1 && getSingleNode().isFolder());
		setEnabled(enabled);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: Implement me!
	}
}