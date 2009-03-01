package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;

import java.io.File;
import java.util.List;

public class ImportFileFolderWorker
				extends SwingWorkerWithAvailableLaterObject<Void> {
	private List<File> files;
	private String destFolderRelPath;

	public ImportFileFolderWorker(List<File> files, String destFolderRelPath) {
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
}
