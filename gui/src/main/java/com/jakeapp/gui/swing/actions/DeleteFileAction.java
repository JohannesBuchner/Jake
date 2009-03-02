package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.dialogs.generic.SheetEvent;
import com.jakeapp.gui.swing.dialogs.generic.SheetListener;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.helpers.Translator;
import com.jakeapp.gui.swing.panels.FilePanel;
import com.jakeapp.gui.swing.worker.DeleteJakeObjectsTask;
import com.jakeapp.gui.swing.worker.JakeExecutor;

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
	
	//TODO replace core calls with ObjectCache-Calls
	@Override
	public void actionPerformed(ActionEvent e) {
		log.debug("omg, we will delete something...");
		
		//final List<String> cache = new ArrayList<String>();
		
		final List<FileObject> files = new ArrayList<FileObject>();

		UserId currentUser = JakeMainApp.getProject().getUserId();

		ResourceMap map = FilePanel.getInstance().getResourceMap();
		String[] options = {map.getString("confirmDeleteFile.ok"), map.getString(
						"genericCancel")};
		String text;
		LogEntry<? extends ILogable> lockEntry = null;
		Attributed<FileObject> af;
		Project p = JakeMainApp.getProject();
		
		log.debug("getting files to delete");

 		//get all files to be deleted
		for (ProjectFilesTreeNode node : getNodes()) {
			if (node.isFile()) {
				files.add(node.getFileObject());
			} else if (node.isFolder()) {
				files.addAll(node.getFolderObject().flattenFolder());
			}
		}
		
		log.debug("got n files to delete, n="+files.size());
		
		//check locks
		for (FileObject f : files) {
			af = JakeMainApp.getCore().getAttributed(p,f);
			if (af.isLocked() && !af.getLockLogEntry().getMember().equals(currentUser)) {
				log.debug("File " + f.getRelPath() + " is locked!");
				lockEntry = af.getLockLogEntry();
				break;
			}
		}
		
		log.debug("checked locks, locked="+(lockEntry!=null));
		
		/*
		 * decide, which user-interaction is appropriate
		 * There are different confirm-messages for different lock-counts
		 */
		text = "";
		if (files.size()==1) {
			if (lockEntry!=null) {
				text = Translator
					.get(map, "confirmDeleteLockedFile.text", lockEntry.getMember().getUserId());
			} else {
				text = map.getString("confirmDeleteFile.text");
			}
		} else {
			if (lockEntry!=null) {
				text = map.getString("confirmDeleteLockedFiles.text");
			} else {
				text = Translator
					.get(map, "confirmDeleteFiles.text", String.valueOf(files.size()));
			}
		}
		
		log.debug("User-interaction text is: "+ text);
		
		//ask user and do the real work with a Worker!
		JSheet.showOptionSheet(FilePanel.getInstance(), text, JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, options[0],
						new SheetListener() {

							@Override
							public void optionSelected(SheetEvent evt) {
								if (evt.getOption() == 0) {
									log.debug("Deleting now!!!");
									JakeExecutor.exec(new DeleteJakeObjectsTask(
										JakeMainApp.getProject(),
										new ArrayList<JakeObject>(files)
									));
									/*for (String item : cache) {
										JakeMainApp.getCore()
														.deleteToTrash(JakeMainApp.getProject(), item);
									}
									*/
								}
								
							}
						});
	}

}