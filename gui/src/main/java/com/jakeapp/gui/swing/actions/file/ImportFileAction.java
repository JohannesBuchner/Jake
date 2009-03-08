package com.jakeapp.gui.swing.actions.file;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.dialogs.generic.SheetEvent;
import com.jakeapp.gui.swing.dialogs.generic.SheetListener;
import com.jakeapp.gui.swing.helpers.FileObjectHelper;
import com.jakeapp.gui.swing.worker.JakeExecutor;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.worker.ImportFileFolderTask;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

@SuppressWarnings("serial")
public class ImportFileAction extends FileAction {
	private static final Logger log = Logger.getLogger(ImportFileAction.class);

	public ImportFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
				  getString("importMenuItem.text");

		putValue(Action.NAME, actionStr);

		// is always enabled.
		boolean enabled = true;
		setEnabled(enabled);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		openImportDialog();
	}

	public void openImportDialog() {
		JFileChooser dialog = new JFileChooser();
		dialog.setMultiSelectionEnabled(true);
		dialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		JSheet.showOpenSheet(dialog, JakeContext.getFrame(), new SheetListener() {
			@Override
			public void optionSelected(SheetEvent evt) {
			}
		});


		log.info("number files selected: " + dialog.getSelectedFiles().length);

		// get destination folder. Project root if nothing selected.
		String destFolder = "/";

		if (getSelectedRowCount() > 0) {
			ProjectFilesTreeNode node = getSingleNode();

			if (node.isFile()) {
				destFolder = FileObjectHelper.getPath(node.getFileObject());
			} else if (node.isFolder()) {
				destFolder = node.getFolderObject().getRelPath();
			}
		}

		log.info("calling core: importExternalFileFolderIntoProject: to " + destFolder);
		JakeExecutor.exec(new ImportFileFolderTask(JakeContext.getProject(),
				  Arrays.asList(dialog.getSelectedFiles()), destFolder));
	}
}