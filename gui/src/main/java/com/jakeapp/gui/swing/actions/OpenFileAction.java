package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.helpers.GuiUtilities;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.core.domain.Project;

import java.awt.event.ActionEvent;

import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;

public class OpenFileAction extends FileAction {
	private JTable fileTable;
	private Project project;

	public OpenFileAction(JTable fileTable, Project project) {
		super(fileTable);
		this.fileTable = fileTable;
		this.project = project;

		String actionStr = JakeMainView.getMainView().getResourceMap().
			 getString("openMenuItem.text");

		putValue(Action.NAME, actionStr);

		// only enable if exactly one element is selected.
		setEnabled(fileTable.getSelectedRowCount() == 1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ProjectFilesTreeNode node = (ProjectFilesTreeNode) fileTable.getValueAt(fileTable.getSelectedRow(), 0);
		if (node.isFile()) {
			GuiUtilities.selectFileInFileViewer(node.getFileObject().getAbsolutePath().getAbsolutePath());
		} else {
			GuiUtilities.selectFileInFileViewer(project.getRootPath() + node.getFolderObject().getRelPath());
		}
	}
}
