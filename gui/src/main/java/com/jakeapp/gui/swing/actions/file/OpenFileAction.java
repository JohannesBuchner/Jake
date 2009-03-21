package com.jakeapp.gui.swing.actions.file;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class OpenFileAction extends FileAction {
	private static final Logger log = Logger.getLogger(OpenFileAction.class);


	public OpenFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
						getString("openMenuItem.text");

		putValue(Action.NAME, actionStr);

		updateAction();
	}

	@Override
	public void updateAction() {
		setEnabled(getSelectedRowCount() == 1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			FileUtilities.launchFile(getSingleNode().getFileObject());
		} catch (FileOperationFailedException ex) {
			ExceptionUtilities.showError(ex);
		}
	}
}
