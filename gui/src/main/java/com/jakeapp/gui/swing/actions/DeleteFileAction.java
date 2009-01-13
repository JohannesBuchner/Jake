package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.FolderObject;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.core.domain.FileObject;

import java.awt.event.ActionEvent;

import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;

public class DeleteFileAction extends FileAction {
	public DeleteFileAction(JTable fileTable) {
		super(fileTable);

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
		int result = JOptionPane.showConfirmDialog(this.getFileTable(), confirmStr +
			 "(" + this.getFileTable().getSelectedRowCount() + ")",
			 deleteStr, JOptionPane.YES_NO_OPTION,
			 JOptionPane.WARNING_MESSAGE);
		if (result == JOptionPane.YES_OPTION) {
			for (int rowNum : this.getFileTable().getSelectedRows()) {

				ProjectFilesTreeNode node = (ProjectFilesTreeNode) this.getFileTable().getValueAt(rowNum, 0);
				if (node.isFile()) {
					JakeMainApp.getApp().getCore().deleteToTrash(node.getFileObject());
				} else if (node.isFolder()) {
					JakeMainApp.getApp().getCore().deleteToTrash(node.getFolderObject());
				}
			}
			this.getFileTable().tableChanged(new TableModelEvent(this.getFileTable().getModel()));
		}
	}
}