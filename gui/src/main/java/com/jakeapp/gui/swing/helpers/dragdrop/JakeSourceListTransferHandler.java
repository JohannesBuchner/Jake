package com.jakeapp.gui.swing.helpers.dragdrop;

import com.jakeapp.gui.swing.helpers.DebugHelper;
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


		/* fetch the data from the Transferable */
		Object data = null;
		try {
			data = t.getTransferData(DataFlavor.javaFileListFlavor);
			log.info("data=" + data);

			Object data2 = t.getTransferData(DataFlavor.stringFlavor);
			log.info("data2= " + data2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* data of type javaFileListFlavor is a list of files */
		List<File> fileList = (List<File>) data;
		log.info("FileList: " + fileList);

		// insert = new project
		if (dl.getChildIndex() == -1) {

			if (fileList != null && fileList.size() == 1 && fileList.get(0).isDirectory()) {
				log.info("Conditions for new project are met!");
				support.setDropAction(TransferHandler.LINK);
				return true;
			}
		} else {
			if (fileList != null && fileList.size() > 0) {
				support.setDropAction(TransferHandler.COPY);
				return true;
			}
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
			List<File> fileList = (List<File>) data;

			/* loop through the files in the file list */
			for (File file : fileList) {
				/* This is where you place your code for opening the
									  * document represented by the "file" variable.
									  * For example:
									  * - create a new internal frame with a text area to
									  *   represent the document
									  * - use a BufferedReader to read lines of the document
									  *   and append to the text area
									  * - add the internal frame to the desktop pane,
									  *   set its bounds and make it visible
									  */
				log.info("file: " + file.getAbsoluteFile());

			}
		} catch (UnsupportedFlavorException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		return true;
	}
}

