package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AnnounceFileAction extends FileAction {
	private static final Logger log = Logger.getLogger(AnnounceFileAction.class);

	public AnnounceFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
				  getString("announceMenuItem.text");

		putValue(Action.NAME, actionStr);
		updateAction();
	}

	@Override
	public void updateAction() {
		setEnabled(getSelectedRowCount() > 0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ArrayList<FileObject> files = getSelectedFiles();

		try {
			JakeMainApp.getCore().announceFileObjects(files);
		} catch (FileOperationFailedException ex) {
			ExceptionUtilities.showError(ex);
		}
	}
}