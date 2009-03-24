package com.jakeapp.gui.swing.actions.file;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.worker.JakeDownloadMgr;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.EnumSet;

public class PullFileAction extends FileAction {
	public PullFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
						getString("pullMenuItem.text");

		putValue(Action.NAME, actionStr);

		updateAction();
	}

	@Override
	public void updateAction() {
		setEnabled(getSelectedRowCount() > 0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		ArrayList<JakeObject> jos = getSelectedFilesAsJakeObjects();
		if (jos.size() > 0) {
			JakeDownloadMgr.getInstance().queueDownload((FileObject) jos.get(0),
							EnumSet.of(JakeDownloadMgr.DlOptions.None));
			//JakeExecutor.exec(new PullJakeObjectsTask(jos));
		}else {
			ExceptionUtilities.showError("no pullable Objects selected");
		}
	}
}