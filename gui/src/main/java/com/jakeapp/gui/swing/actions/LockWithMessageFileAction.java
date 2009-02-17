package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class LockWithMessageFileAction extends FileAction {
	public LockWithMessageFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
						getString("lockWithMessageMenuItem.text");

		putValue(Action.NAME, actionStr);

		// only enable if exact one element is selected AND that element is NOT a folder.
		updateAction();
	}

	@Override
	public void updateAction() {
		// only enable if exact one element is selected AND that element is NOT a folder.
		boolean enabled = (getSelectedRowCount() == 1 && getSingleNode().isFile());
		setEnabled(enabled);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: Implement me!
	}
}