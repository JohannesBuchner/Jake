package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.exceptions.InvalidNewFolderException;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.helpers.FileUtilities;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import org.jdesktop.swingx.JXTreeTable;
import org.apache.log4j.Logger;

import javax.swing.*;

public class NewFolderFileAction extends FileAction {
	private static final Logger log = Logger.getLogger(NewFolderFileAction.class);

	public NewFolderFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
			 getString("newFolderMenuItem.text");

		putValue(Action.NAME, actionStr);

		setEnabled(true);
	}

	@Override
	public void updateAction() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String path = getSingleNode().isFile() ?
			 FileUtilities.getPathFromPathWithFile(getSingleNode().getFileObject().getRelPath()) :
			 getSingleNode().getFolderObject().getRelPath();

		try {
			JakeMainApp.getCore().createNewFolderAt(JakeMainApp.getProject(), path, "blubb");
		} catch (InvalidNewFolderException e1) {
			log.error("Could not create new folder", e1);
		}
	}
}