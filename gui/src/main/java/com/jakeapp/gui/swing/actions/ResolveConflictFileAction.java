package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.dialogs.ResolveConflictDialog;
import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

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
		ResolveConflictDialog.showDialog(getProject(), getSelectedFile());
	}
}