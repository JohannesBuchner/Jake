package com.jakeapp.gui.swing.actions.file;

import com.jakeapp.core.synchronization.attributes.SyncStatus;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.dialogs.ResolveConflictDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ResolveConflictFileAction extends FileAction {
	public ResolveConflictFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
						getString("resolveConflictMenuitem.text");

		putValue(Action.NAME, actionStr);

		updateAction();
	}

	@Override
	public void updateAction() {
		setEnabled(isSingleFileSelected() && JakeMainApp.getCore()
						.getAttributed(getSelectedFile())
						.getSyncStatus() == SyncStatus.CONFLICT);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ResolveConflictDialog.showDialog(getProject(), getSelectedFile());
	}
}