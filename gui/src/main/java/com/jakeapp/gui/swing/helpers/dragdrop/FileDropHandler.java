package com.jakeapp.gui.swing.helpers.dragdrop;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileDropHandler extends TransferHandler {
	private static final Logger log = Logger.getLogger(FileDropHandler.class);

	public boolean canImport(TransferSupport supp) {
		/* for the demo, we'll only support drops (not clipboard paste) */
		if (!supp.isDrop()) {
			return false;
		}

		/* return true if and only if the drop contains a list of files */
		if (!supp.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			return false;
		}

		boolean copySupported = (COPY & supp.getSourceDropActions()) == COPY;

		// TODO: determine if file can be imported
		// TODO: send event to core

		/* if COPY is supported, choose COPY and accept the transfer */
		if (copySupported) {
			supp.setDropAction(COPY);
			return true;
		}

		return false;
	}

	public boolean importData(TransferHandler.TransferSupport supp) {
		if (!canImport(supp)) {
			return false;
		}

		/* fetch the Transferable */
		Transferable t = supp.getTransferable();

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
