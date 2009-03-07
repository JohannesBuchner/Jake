package com.jakeapp.gui.swing.helpers.dragdrop;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.helpers.FileObjectHelper;
import com.jakeapp.gui.swing.worker.JakeExecutor;
import com.jakeapp.gui.swing.worker.ImportFileFolderTask;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The File Drop Handler checks for Drag&Drop-Actions on a global basis.
 * e.g. you can drag your file anywhere to be imported.
 */
public class FileDropHandler extends TransferHandler {
	private static final Logger log = Logger.getLogger(FileDropHandler.class);

	@Override
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

		Project pr = JakeMainApp.getProject();

		return copySupported && pr != null && !pr.isInvitation();

	}

	@Override
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

			// get destination folder. Project root if nothing selected.
			String destFolder = "/";

			JakeExecutor.exec(new ImportFileFolderTask(JakeMainApp.getProject(), fileList, destFolder));

		} catch (UnsupportedFlavorException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		return true;
	}
}