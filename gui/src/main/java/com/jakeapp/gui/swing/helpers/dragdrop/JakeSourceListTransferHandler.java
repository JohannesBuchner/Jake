package com.jakeapp.gui.swing.helpers.dragdrop;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.DebugHelper;
import com.jakeapp.gui.swing.helpers.JakeExecutor;
import com.jakeapp.gui.swing.helpers.ProjectHelper;
import com.jakeapp.gui.swing.worker.ImportFileFolderWorker;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The Transfer Handler for drag&drop on the SourceList.
 * Either we create a new project, or add the data into an existing project.
 *
 * @author: studpete
 */
// TODO: stuff needs to be cleaned up very badly
public class JakeSourceListTransferHandler extends TransferHandler {
	private static final Logger log = Logger.getLogger(JakeSourceListTransferHandler.class);

	public boolean canImport(TransferHandler.TransferSupport support) {
		log.debug("support.isDrop: " + support.isDrop() + "support.stringdata?:" + support.isDataFlavorSupported(DataFlavor.stringFlavor));

		// for the demo, we'll only support drops (not clipboard paste)
		if (!support.isDrop()) {
			return false;
		}

		// we only import files 
		if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			return false;
		}

		// fetch the drop location (it's a JTree.DropLocation for JTree)
		JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();

		log.info("DropLocation: ChildIndex: " + dl.getChildIndex() + " Path: " + dl.getPath());


		/* fetch the Transferable */
		Transferable t = support.getTransferable();

		log.info("DataFlavors: " + DebugHelper.arrayToString(t.getTransferDataFlavors()));

		// TODO: we do not get any data from the transferable

		/* fetch the data from the Transferable */
		Object data = null;
		try {
			data = t.getTransferData(DataFlavor.javaFileListFlavor);
			log.info("data=" + data);

			//Object data2 = t.getTransferData(DataFlavor.);
			//log.info("data2= " + data2);
		} catch (Exception e) {
			log.warn("Error on drag&drop-action: ", e);
		}

		/* data of type javaFileListFlavor is a list of files */
		List<File> files = (List<File>) data;
		log.info("FileList: " + files);

		// insert = new project
		if (isNewProject(dl, files)) {

			support.setDropAction(TransferHandler.LINK);

			/*if (fileList != null && fileList.size() == 1 && fileList.get(0).isDirectory()) {
				log.info("Conditions for new project are met!");
				support.setDropAction(TransferHandler.LINK);
				return true;
			}*/
		} else {

			support.setDropAction(TransferHandler.COPY);

			/*if (fileList != null && fileList.size() > 0) {
				support.setDropAction(TransferHandler.COPY);
				return true;
			}*/
		}
		return true;


/*	// check if the source actions (a bitwise-OR of supported actions)
		// contains the COPY action
		boolean copySupported =
				  (TransferHandler.COPY & support.getSourceDropActions()) == TransferHandler.COPY;

		// if COPY is supported, choose COPY and accept the transfer
		if (copySupported) {
			//support.setDropAction(TransferHandler.COPY);
			return true;
		}

		// COPY isn't supported, so reject the transfer
		return false;
		*/
	}

	public boolean importData(TransferHandler.TransferSupport support) {
		log.info("Import Data.");

		if (!canImport(support)) {
			return false;
		}

		// fetch the drop location (it's a JTree.DropLocation for JTree)
		JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();

		log.info("DropLocation: ChildIndex: " + dl.getChildIndex() + " Path: " + dl.getPath());

		/* fetch the Transferable */
		Transferable t = support.getTransferable();

		try {
			/* fetch the data from the Transferable */
			Object data = t.getTransferData(DataFlavor.javaFileListFlavor);

			/* data of type javaFileListFlavor is a list of files */
			List<File> files = (List<File>) data;

			if (isNewProject(dl, files)) {
				log.info("generate new project!");
				File newProjectFile = files.get(0);
				JakeMainApp.getCore().createProject(
						  ProjectHelper.createDefaultPath(newProjectFile.getAbsolutePath()), newProjectFile.getAbsolutePath(), JakeMainApp.getMsgService());
			} else if (isAddToProject(dl, files)) {
				log.info("add to project!");

				JakeExecutor.exec(new ImportFileFolderWorker(files, ""));
			}

			/* loop through the files in the file list (debug) */
			for (File file : files) {
				log.info("file: " + file.getAbsoluteFile());
			}

		} catch (UnsupportedFlavorException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	/**
	 * Checks if drop location should create a new project
	 *
	 * @param dl
	 * @param files
	 * @return
	 */
	private boolean isNewProject(JTree.DropLocation dl, List<File> files) {
		// add as new project
		if (dl.getChildIndex() >= 0) {

			// just say ok if there is no file list
			if (files == null) {
				return true;
			} else {
				return files.size() == 1 && files.get(0).isDirectory();
			}
		}
		return false;
	}

	/**
	 * Checks if drop locaton want to add files/folders to a project.
	 *
	 * @param dl
	 * @param files
	 * @return
	 */
	private boolean isAddToProject(JTree.DropLocation dl, List<File> files) {
		// add as new project
		if (dl.getChildIndex() == -1) {

			// just say ok if there is no file list
			if (files == null) {
				return true;
			} else {
				return files.size() > 0;
			}
		}
		return false;
	}
}

