package com.jakeapp.gui.swing.actions.file;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ShowInBrowserFileAction extends FileAction {
	private static final Logger log = Logger.getLogger(ShowInBrowserFileAction.class);

	public ShowInBrowserFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
						getString("browseMenuItem.text");

		putValue(Action.NAME, actionStr);

		updateAction();
	}

	@Override
	public void updateAction() {
		setEnabled(getSelectedRowCount() == 1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!isSingleFileSelected()) {
			log.warn("Cannot launch browser: need single file to be selected.");
			return;
		}

		try {
			FileUtilities.selectFileInFileViewer(
							JakeMainApp.getCore().getFile(getSelectedFile()).getAbsolutePath());
		} catch (FileOperationFailedException ex) {
			ExceptionUtilities.showError("Unable to start browser", ex);
		}
	}
}