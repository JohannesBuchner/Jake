package com.jakeapp.core.services.futures;

import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

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