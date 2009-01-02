package com.jakeapp.jake.ics.filetransfer;

import java.io.File;

/**
 * pop in anything you want here ...
 * @author johannes
 *
 */
public abstract class AdditionalFileTransferData {

	private File dataFile;

	public File getDataFile() {
		return this.dataFile;
	}

	public void setDataFile(File outputFile) {
		this.dataFile = outputFile;
	}

}