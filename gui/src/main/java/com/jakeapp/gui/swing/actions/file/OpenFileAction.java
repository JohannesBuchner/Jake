package com.jakeapp.gui.swing.actions.file;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.worker.JakeExecutor;
import com.jakeapp.gui.swing.worker.PullAndLaunchJakeObjectsTask;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class OpenFileAction extends FileAction {
	private static final Logger log = Logger.getLogger(OpenFileAction.class);


	public OpenFileAction() {
		super();

		updateAction();
	}

	@Override
	public void updateAction() {
		setEnabled(getSelectedRowCount() == 1);


		String actionStr = JakeMainView.getMainView().getResourceMap().
						getString("openMenuItem.text");

		// append "..." if we need to download the file first
		if(isSingleFileSelected() && getSelectedFileAttributed().isOnlyRemote()) {
			actionStr += "...";
		}

		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!isSingleFileSelected()) {
			log.warn("Cannot launch file: need single file to be selected.");
			return;
		}

		launchFile(getSelectedFile());
	}

	/**
	 * Launches the File. Starts a download if file is remote only.
	 * @param fo
	 */
	public static void launchFile(FileObject fo) {

		if(fo == null) {
			log.warn("Cannot launch: fileObject is null");
		}

		// detect if there is a soft lock set
		final Attributed<FileObject> aFo = JakeMainApp.getCore()
						.getAttributed(fo);


		if (aFo.isOnlyRemote()) {
			JakeExecutor.exec(new PullAndLaunchJakeObjectsTask(
							Arrays.asList((JakeObject) aFo.getJakeObject())));
		}

		launchFileDontTryPull(fo);
	}

	public static void launchFileDontTryPull(FileObject fo) {
		try {
			FileUtilities.launchFile(fo);
		} catch (FileOperationFailedException e) {
			//ExceptionUtilities.showError("Failed launching " + fo.getRelPath(), e);
			log.warn("Failed lauchning " + e.getMessage());
		}
	}
}