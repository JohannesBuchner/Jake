package com.jakeapp.core.services.futures;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;

/**
 * <code>AvailableLaterObject</code> importing a <code>List</code> of <code>{@link java.io.File}</code>s to
 * a certain <code>relPath</code> within a specific <code>IFSService</code>
 */
public class ImportFilesFuture extends AvailableLaterObject<Void> {
	private static final Logger log = Logger.getLogger(ImportFilesFuture.class);
	
	private IFSService fss;
	private List<File> files;
	private String destFolderRelPath;

	public ImportFilesFuture(IFSService fss, List<File> files,
					String destFolderRelPath) {
		this.fss = fss;
		this.files = files;
		this.destFolderRelPath = destFolderRelPath;
	}

	@Override
	public Void calculate() throws Exception {
		log.debug("importing n files, n="+files.size());
		for(File file : files) {
			log.debug("Importing a file!");
			this.fss.importFile(file, destFolderRelPath);
		}
		return null;
	}
}