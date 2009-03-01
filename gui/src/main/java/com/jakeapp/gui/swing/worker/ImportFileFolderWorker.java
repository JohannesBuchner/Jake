package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.xcore.EventCore;

import java.io.File;
import java.util.List;

public class ImportFileFolderWorker
				extends SwingWorkerWithAvailableLaterObject<Void> {
	private Project p;
	private List<File> files;
	private String destFolderRelPath;

	public ImportFileFolderWorker(Project p, List<File> files, String destFolderRelPath) {
		this.p = p;
		this.files = files;
		this.destFolderRelPath = destFolderRelPath;
	}

	@Override
	protected AvailableLaterObject<Void> calculateFunction() {
		return JakeMainApp.getCore()
						.importExternalFileFolderIntoProject(JakeMainApp.getProject(),
										files,
										destFolderRelPath);
	}

	@Override
	protected void done() {
		EventCore.get().fireFilesChanged(p);
	}

		@Override
	public void error(Exception e) {
		ExceptionUtilities.showError(e);
	}
}
