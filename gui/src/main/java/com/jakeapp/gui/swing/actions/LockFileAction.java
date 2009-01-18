package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.core.domain.FileObject;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;

public class LockFileAction extends FileAction {
	public LockFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
			 getString("lockMenuItem.text");

		putValue(Action.NAME, actionStr);

		// only enable if exact one element is selected AND that element is NOT a folder.
		boolean enabled = (getSelectedRowCount() == 1 &&
			 getSingleNode().isFile());
		setEnabled(enabled);
	}

	@Override
	public void updateAction() {
		boolean enabled = (getSelectedRowCount() == 1 &&
			 getSingleNode().isFile());
		setEnabled(enabled);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		FileObject fo = getSingleNode().getFileObject();
		JakeMainApp.getCore().setSoftLock(fo, !JakeMainApp.getCore().isSoftLocked(fo), "");
	}
}