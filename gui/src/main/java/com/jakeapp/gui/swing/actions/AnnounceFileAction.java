package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.helpers.FolderObject;
import com.jakeapp.core.domain.FileObject;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

import org.jdesktop.swingx.JXTreeTable;
import org.apache.log4j.Logger;

import javax.swing.*;

public class AnnounceFileAction extends FileAction {
	private static final Logger log = Logger.getLogger(AnnounceFileAction.class);

	public AnnounceFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
			 getString("announceMenuItem.text");

		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {


		ArrayList<FileObject> files = new ArrayList<FileObject>();

		for (ProjectFilesTreeNode pf : getNodes()) {
			if (pf.isFile()) {
				files.add(pf.getFileObject());
			} else if (pf.isFolder()) {
				files.addAll(recurseNodes(pf.getFolderObject()));
			}
		}

		// FIXME: This should a) do something and b) use workers!
		for (FileObject fo : files) {
			log.warn("Announcing file '" + fo.getRelPath() + "' (IMPLEMENT ME!)");
		}
	}

	private List<FileObject> recurseNodes(FolderObject folder) {
		ArrayList<FileObject> files = new ArrayList<FileObject>();

		for (FileObject f : folder.getFileChildren()) {
			files.add(f);
		}

		for (FolderObject f : folder.getFolderChildren()) {
			files.addAll(recurseNodes(f));
		}

		return files;
	}
}