package com.jakeapp.violet.actions.project.local;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.StatusUpdate;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.violet.model.ProjectModel;

/**
 * <code>AvailableLaterObject</code> importing a <code>List</code> of
 * <code>{@link java.io.File}</code>s to a certain <code>relPath</code> within a
 * specific <code>IFSService</code>
 */
public class ImportFilesAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger.getLogger(ImportFilesAction.class);

	private ProjectModel model;

	private List<File> files;

	private String destFolderRelPath;

	private int nsteps;

	private int steps;

	public ImportFilesAction(ProjectModel model, List<File> files,
			String destFolderRelPath) {
		this.model = model;
		this.files = files;
		nsteps = files.size() + 1;
		steps = 1;
		this.destFolderRelPath = destFolderRelPath;
		this.setStatus(new StatusUpdate(nsteps * 1. / steps, "init"));
	}

	@Override
	public Void calculate() throws Exception {
		log.debug("importing n files, n=" + files.size());
		IFSService fss = this.model.getFss();
		for (File file : files) {
			log.debug("Importing a file!");
			fss.importFile(file, destFolderRelPath);
			steps++;
			this.setStatus(new StatusUpdate(nsteps * 1. / steps, "importing"));
		}
		return null;
	}
}