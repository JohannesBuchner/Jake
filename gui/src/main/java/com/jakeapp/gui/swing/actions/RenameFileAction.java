package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;

import java.awt.event.ActionEvent;

import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;

public class RenameFileAction extends FileAction {
	public RenameFileAction(JTable fileTable) {
		super(fileTable);

		String actionStr = JakeMainView.getMainView().getResourceMap().
			 getString("renameMenuItem.text");

		putValue(Action.NAME, actionStr);

		// only enable if exact one element is selected.
		setEnabled(fileTable.getSelectedRowCount() == 1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ProjectFilesTreeNode node = (ProjectFilesTreeNode) this.getFileTable().
			 getValueAt(this.getFileTable().getSelectedRow(), 0);

		String currentName = "";

		// TODO: This should be a name, not a relpath
		// even though this allows for easy moving of files, it might be confusing to novice users
		if (node.isFile()) {
			currentName = node.getFileObject().getRelPath();
		} else if (node.isFolder()) {
			currentName = node.getFolderObject().getRelPath();
		}

		String promptStr = JakeMainView.getMainView().getResourceMap().
			 getString("promptEditFile.text");
		String newName = JOptionPane.showInputDialog(promptStr, currentName);
		if (!newName.equals(currentName)) {
			if (node.isFile()) {
				JakeMainApp.getApp().getCore().rename(node.getFileObject(), newName);
			} else if (node.isFolder()) {
				JakeMainApp.getApp().getCore().rename(node.getFolderObject(), newName);
			}
			this.getFileTable().tableChanged(new TableModelEvent(this.getFileTable().getModel()));
		}
	}
}