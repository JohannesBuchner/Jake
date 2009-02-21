package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.synchronization.AttributedJakeObject;
import com.jakeapp.gui.swing.ICoreAccess;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.dialogs.generic.SheetEvent;
import com.jakeapp.gui.swing.dialogs.generic.SheetListener;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.helpers.Translator;
import com.jakeapp.gui.swing.panels.FilePanel;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class DeleteFileAction extends FileAction {
	private static final Logger log = Logger.getLogger(DeleteFileAction.class);

	public DeleteFileAction() {
		String actionStr = JakeMainView.getMainView().getResourceMap().
						getString("deleteMenuItem.text");

		putValue(Action.NAME, actionStr);

		updateAction();
	}

	@Override
	public void updateAction() {
		setEnabled(getSelectedRowCount() > 0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final List<String> cache = new ArrayList<String>();
		List<AttributedJakeObject<FileObject>> files = new ArrayList<AttributedJakeObject<FileObject>>();

		ICoreAccess core = JakeMainApp.getCore();
		UserId currentUser = JakeMainApp.getProject().getUserId();

		ResourceMap map = FilePanel.getInstance().getResourceMap();
		String[] options = {map.getString("confirmDeleteFile.ok"), map.getString(
						"genericCancel")};
		String text;

		for (ProjectFilesTreeNode node : getNodes()) {
			if (node.isFile()) {
				cache.add(node.getFileObject().getRelPath());
				files.add(node.getFileObject());
			} else if (node.isFolder()) {
				cache.add(node.getFolderObject().getRelPath());
			}
		}


		if (files.size() == 1) { //single delete
			if (files.get(0)) { //is locked
				UserId lockOwner = core.getLockOwner(files.get(0));

				if (lockOwner.getUserId().equals(currentUser)) { //local member is owner
					text = map.getString("confirmDeleteFile.text");
				} else {
					text = Translator
									.get(map, "confirmDeleteLockedFile.text", lockOwner.getNickname());
				}
			} else {
				text = map.getString("confirmDeleteFile.text");
			}
		} else { //batch delete
			log.debug(
							"batch delete -------------------------------------------------------------");
			boolean locked = false;
			for (FileObject f : files) {
				if (core.isJakeObjectLocked(f) && !core.getLockOwner(f).getUserId()
								.equals(currentUser)) {
					log.debug("File " + f.getRelPath() + " is locked!");
					locked = true;
					break;
				}
			}
			if (locked) {
				log.debug("at least one file is locked");
				text = map.getString("confirmDeleteLockedFiles.text");
			} else {
				log.debug("no file is locked or locked by the local user");
				text = Translator
								.get(map, "confirmDeleteFiles.text", String.valueOf(cache.size()));
			}
		}

		JSheet.showOptionSheet(FilePanel.getInstance(), text, JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, options[0],
						new SheetListener() {
							@Override
							public void optionSelected(SheetEvent evt) {
								if (evt.getOption() == 0) {
									for (String item : cache) {
										JakeMainApp.getCore()
														.deleteToTrash(JakeMainApp.getProject(), item);
									}
								}
							}
						});
	}
}