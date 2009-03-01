package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.gui.swing.ICoreAccess;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.helpers.FolderObject;
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
	
	//TODO replace core calls with ObjectCache-Calls
	@Override
	public void actionPerformed(ActionEvent e) {
		//final List<String> cache = new ArrayList<String>();
		
		List<FileObject> files = new ArrayList<FileObject>();
		List<Attributed<FileObject>> attributedFiles = new ArrayList<Attributed<FileObject>>();

		UserId currentUser = JakeMainApp.getProject().getUserId();

		ResourceMap map = FilePanel.getInstance().getResourceMap();
		String[] options = {map.getString("confirmDeleteFile.ok"), map.getString(
						"geILogablecel")};
		String text;
		LogEntry<? extends ILogable> lockEntry = null;
		Attributed<FileObject> af;
		Project p = JakeMainApp.getProject();
		
		//preconditions-check
		if (files==null || files.size()<1) return;

 		//get all files to be deleted
		for (ProjectFilesTreeNode node : getNodes()) {
			if (node.isFile()) {
				files.add(node.getFileObject());
			} else if (node.isFolder()) {
				files.addAll(node.getFolderObject().flattenFolder());
			}
		}

		//check locks
		for (FileObject f : files) {
			af = JakeMainApp.getCore().getAttributed(p,f);
			if (af.isLocked() && !af.getLockLogEntry().getMember().equals(currentUser)) {
				log.debug("File " + f.getRelPath() + " is locked!");
				lockEntry = af.getLockLogEntry();
				break;
			}
		}
		
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
		
		//TODO ask user and do the real work with a Worker!
		
/*
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
	*/}

}