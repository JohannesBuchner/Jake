package com.jakeapp.gui.swing.actions.file;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;

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
		setEnabled(isSingleFileSelected());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO:fix
		//ResolveConflictDialog.showDialog(getProject(), getSelectedFile());
	}
}