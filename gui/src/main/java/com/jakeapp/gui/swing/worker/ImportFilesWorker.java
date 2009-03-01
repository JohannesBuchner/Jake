package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.xcore.EventCore;

import java.io.File;
import java.util.List;

public class ImportFilesWorker extends SwingWorkerWithAvailableLaterObject<Void> {
	private Project project;
	private List<File> files;
	private String destFolderRootPath;

	public ImportFilesWorker(Project project, List<File> files,
					String destFolderRootPath) {
		this.project = project;
		this.files = files;
		this.destFolderRootPath = destFolderRootPath;
	}

	@Override
	protected AvailableLaterObject<Void> calculateFunction() {
		return JakeMainApp.getCore()
						.importExternalFileFolderIntoProject(project, files, destFolderRootPath);
	}


	@Override
	protected void done() {

		EventCore.get().fireFilesChanged();
	}


	@Override
	public void error(Exception e) {
		ExceptionUtilities.showError(e);
	}
}