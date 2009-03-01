package com.jakeapp.core.services.futures;

import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;

import java.io.File;
import java.util.List;

public class ImportFilesFuture extends AvailableLaterObject<Void> {
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
		for(File file : files) {
			this.fss.importFile(file, destFolderRelPath);
		}
		return null;
	}
}