package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.worker.JakeExecutor;
import com.jakeapp.gui.swing.worker.AnnounceJakeObjectWorker;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AnnounceFileAction extends FileAction {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(AnnounceFileAction.class);

	public AnnounceFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
				  getString("announceMenuItem.text");

		putValue(Action.NAME, actionStr);
		updateAction();
	}

	@Override
	public void updateAction() {
		setEnabled(getSelectedRowCount() > 0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ArrayList<FileObject> files = getSelectedFiles();
		ArrayList<JakeObject> jos = new ArrayList<JakeObject>(files.size());
		jos.addAll(files);
		JakeExecutor.exec(new AnnounceJakeObjectWorker(jos,null));
	}
}