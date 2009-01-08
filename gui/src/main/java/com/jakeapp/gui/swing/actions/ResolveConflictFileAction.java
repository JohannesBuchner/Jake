package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.dialogs.ResolveConflictDialog;
import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ResolveConflictFileAction extends FileAction {
	public ResolveConflictFileAction(JXTreeTable fileTable) {
		super(fileTable);

		String actionStr = JakeMainView.getMainView().getResourceMap().
				  getString("resolveConflictMenuitem.text");

		putValue(Action.NAME, actionStr);

		// only enable if exact one element is selected AND that element is NOT a folder.
		setEnabled(isSingleFileSelected());
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		ResolveConflictDialog.showDialog(getProject(), getSelectedFile());
	}
}