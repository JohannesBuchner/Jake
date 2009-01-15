package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.dialogs.generic.SheetEvent;
import com.jakeapp.gui.swing.dialogs.generic.SheetListener;
import com.jakeapp.gui.swing.helpers.FileObjectHelper;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class ImportFileAction extends FileAction {
	private static final Logger log = Logger.getLogger(ImportFileAction.class);

	public ImportFileAction(JTable fileTable) {
		super(fileTable);

		String actionStr = JakeMainView.getMainView().getResourceMap().
				  getString("importMenuItem.text");

		putValue(Action.NAME, actionStr);

		// is always enabled.
		boolean enabled = true;
		setEnabled(enabled);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: Implement me!

		//FileDialog dialog = new FileDialog(JakeMainView.getMainView().getFrame(), "Select one or more files to Import...", FileDialog.LOAD);
		//dialog.setVisible(true);

		JFileChooser dialog = new JFileChooser();
		dialog.setMultiSelectionEnabled(true);
		dialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		JSheet.showOpenSheet(dialog, JakeMainApp.getFrame(), new SheetListener() {

			@Override
			public void optionSelected(SheetEvent evt) {

			}
		});


		log.info("number files selected: " + dialog.getSelectedFiles().length);

		// get destination folder. root if nothing selected.
		String destFolder = "/";

		if (this.getFileTable().getSelectedRow() != -1) {
			ProjectFilesTreeNode node = (ProjectFilesTreeNode) this.getFileTable().
					  getValueAt(this.getFileTable().getSelectedRow(), 0);

			if (node.isFile()) {
				destFolder = FileObjectHelper.getPath(node.getFileObject().getAbsolutePath());
			} else if (node.isFolder()) {
				destFolder = node.getFolderObject().getRelPath();
			}
		}

		log.info("calling core: importExternalFileFolderIntoProject: to " + destFolder);

		// TODO: progress bar? async!?
		for (File file : dialog.getSelectedFiles()) {
			log.debug("importing file/folder: " + file);
			JakeMainApp.getCore().importExternalFileFolderIntoProject(file.getAbsolutePath(), destFolder);
		}
	}
}