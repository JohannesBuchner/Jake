package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.FolderObject;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.core.domain.FileObject;

import java.awt.event.ActionEvent;
import java.util.List;

import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;

public class DeleteFileAction extends FileAction {
	public DeleteFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
			 getString("deleteMenuItem.text");

		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String confirmStr = JakeMainView.getMainView().getResourceMap().
			 getString("confirmDeleteFile.text");
		String deleteStr = JakeMainView.getMainView().getResourceMap().
			 getString("confirmDeleteFileDelete.text");
		int result = JOptionPane.showConfirmDialog(JakeMainView.getMainView().getComponent(), confirmStr +
			 "(" + getSelectedRowCount() + ")",
			 deleteStr, JOptionPane.YES_NO_OPTION,
			 JOptionPane.WARNING_MESSAGE);
		if (result == JOptionPane.YES_OPTION) {
			for (ProjectFilesTreeNode node : getNodes()) {
				if (node.isFile()) {
					JakeMainApp.getApp().getCore().deleteToTrash(node.getFileObject());
				} else if (node.isFolder()) {
					JakeMainApp.getApp().getCore().deleteToTrash(node.getFolderObject());
				}
			}
		}
	}
}