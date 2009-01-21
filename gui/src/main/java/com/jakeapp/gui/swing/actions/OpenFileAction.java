package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class OpenFileAction extends FileAction {
	private static final Logger log = Logger
			  .getLogger(OpenFileAction.class);


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
			Desktop.getDesktop().open(getSingleNode().getFileObject().getAbsolutePath());
		} catch (IOException e1) {
			log.warn("failed opening", e1);
		}
		/*
		if (getSingleNode().isFile()) {
			GuiUtilities.selectFileInFileViewer(getSingleNode().getFileObject().getAbsolutePath().getAbsolutePath());
		} else {
			GuiUtilities.selectFileInFileViewer(JakeMainApp.getApp().getProject().getRootPath() + getSingleNode().getFolderObject().getRelPath());
		}*/
	}
}
