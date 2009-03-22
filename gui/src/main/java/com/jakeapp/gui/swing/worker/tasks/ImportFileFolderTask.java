package com.jakeapp.gui.swing.worker.tasks;

import java.io.File;
import java.util.List;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.xcore.EventCore;

/**
 * Import File Folder Worker
 */
public class ImportFileFolderTask extends AbstractTask<Void> {
	private Project p;
	private List<File> files;
	private String destFolderRelPath;

	public ImportFileFolderTask(Project p, List<File> files,
					String destFolderRelPath) {
		this.p = p;
		this.files = files;
		this.destFolderRelPath = destFolderRelPath;
	}


	@Override
	protected AvailableLaterObject<Void> calculateFunction() {
		return JakeMainApp.getCore()
						.importExternalFileFolderIntoProject(JakeContext.getProject(),
										files,
										destFolderRelPath);
	}

	@Override
	protected void onDone() {
		EventCore.get().fireFilesChanged(p);
	}
}
