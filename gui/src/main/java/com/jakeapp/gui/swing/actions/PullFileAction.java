package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class PullFileAction extends FileAction {
	public PullFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
				  getString("pullMenuItem.text");

		putValue(Action.NAME, actionStr);

		updateAction();
	}

	@Override
	public void updateAction() {
		setEnabled(getSelectedRowCount() > 0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			// TODO: save object, or rely on events?
			JakeMainApp.getCore().pullJakeObject(this.getSelectedFile());
		} catch (FileOperationFailedException ex) {
			ExceptionUtilities.showError(ex);
		}
	}
}