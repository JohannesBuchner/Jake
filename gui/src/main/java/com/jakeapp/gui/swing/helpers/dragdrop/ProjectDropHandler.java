package com.jakeapp.gui.swing.helpers.dragdrop;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.JakeHelper;
import com.jakeapp.gui.swing.helpers.ProjectHelper;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The Project Drop Handler checks for Drag&Drop-Actions
 * e.g. you can drag a folder anywhere to be added.
 */
public class ProjectDropHandler extends TransferHandler {
	private static final Logger log = Logger.getLogger(ProjectDropHandler.class);

	public boolean canImport(TransferSupport support) {
		/* for the demo, we'll only support drops (not clipboard paste) */
		if (!support.isDrop()) {
			return false;
		}

		/* return true if and only if the drop contains a list of files */
		if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			return false;
		}

		boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;
		if (copySupported) {
			support.setDropAction(TransferHandler.COPY);
		}
		return copySupported;
	}

	public boolean importData(TransferSupport supp) {
		if (!canImport(supp)) {
			return false;
		}

		/* fetch the Transferable */
		Transferable t = supp.getTransferable();

		try {
			/* fetch the data from the Transferable */
			Object data = t.getTransferData(DataFlavor.javaFileListFlavor);

			/* data of type javaFileListFlavor is a list of files */
			List<File> files = (List<File>) data;

			// add a project?
			if (files.size() == 1 && files.get(0).isDirectory()) {
				File newProjectFile = files.get(0);
				JakeMainApp.getCore().createProject(
						  ProjectHelper.createDefaultPath(newProjectFile.getAbsolutePath()),
						  newProjectFile.getAbsolutePath());
			} else {
				JakeHelper.showInfoMsg("Cannot create project. Drop in one folder.");
			}


		} catch (UnsupportedFlavorException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		return true;
	}
}