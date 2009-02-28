package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action to lock and unlock a file. Only one file at a time, no batch support, no locking comment.
 * @author Simon
 *
 */
public class LockFileAction extends FileAction {

	private static final long serialVersionUID = 3816960847280746811L;

	public LockFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
			 getString("lockMenuItem.text");

		putValue(Action.NAME, actionStr);

		// only enable if exact one element is selected AND that element is NOT a folder.
		boolean nuEnabledState = (getSelectedRowCount() == 1 &&
			 getSingleNode().isFile());
		setEnabled(nuEnabledState);
	}

	@Override
	public void updateAction() {
		boolean nuEnabledState = (getSelectedRowCount() == 1 &&
			 getSingleNode().isFile());
		setEnabled(nuEnabledState);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		FileObject fo = getSingleNode().getFileObject();
		
//		boolean newLockingState = 
		// TODO: fix
		//JakeMainApp.getCore().setSoftLock(fo, !JakeMainApp.getCore().isSoftLocked(fo), "");
	}
}