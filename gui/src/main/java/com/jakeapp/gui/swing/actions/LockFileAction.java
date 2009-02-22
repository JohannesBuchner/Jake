package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
		// TODO: fix
		//JakeMainApp.getCore().setSoftLock(fo, !JakeMainApp.getCore().isSoftLocked(fo), "");
	}
}