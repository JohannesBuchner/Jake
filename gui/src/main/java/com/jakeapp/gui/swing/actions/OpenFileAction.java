package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.GuiUtilities;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.*;

public class OpenFileAction extends FileAction {
	public OpenFileAction(List<ProjectFilesTreeNode> nodes) {
		super(nodes);

		String actionStr = JakeMainView.getMainView().getResourceMap().
			 getString("openMenuItem.text");

		putValue(Action.NAME, actionStr);

		// only enable if exactly one element is selected.
		setEnabled(getSelectedRowCount() == 1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (getSingleNode().isFile()) {
			GuiUtilities.selectFileInFileViewer(getSingleNode().getFileObject().getAbsolutePath().getAbsolutePath());
		} else {
			GuiUtilities.selectFileInFileViewer(JakeMainApp.getApp().getProject().getRootPath() + getSingleNode().getFolderObject().getRelPath());
		}
	}
}
