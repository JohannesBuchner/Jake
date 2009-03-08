package com.jakeapp.gui.swing.actions.file;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RenameFileAction extends FileAction {
	public RenameFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
			 getString("renameMenuItem.text");

		putValue(Action.NAME, actionStr);

		// only enable if exact one element is selected.
		updateAction();
	}

	@Override
	public void updateAction() {
		setEnabled(getSelectedRowCount() == 1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ProjectFilesTreeNode node = getSingleNode();

		String currentName = "";

		// TODO: This should be a name, not a relpath
		// even though this allows for easy moving of files, it might be confusing to novice users
		if (node.isFile()) {
			currentName = node.getFileObject().getRelPath();
		} else if (node.isFolder()) {
			currentName = node.getFolderObject().getRelPath();
		}

		String promptStr = JakeMainView.getMainView().getResourceMap().
			 getString("promptRenameFile");
		// FIXME: sheets
		String newName = JOptionPane.showInputDialog(promptStr, currentName);
		if (!newName.equals(currentName)) {
			if (node.isFile()) {
				JakeMainApp.getCore().rename(node.getFileObject(), newName);
			} else if (node.isFolder()) {
				JakeMainApp.getCore().rename(node.getFolderObject(), newName);
			}
		}
	}
}