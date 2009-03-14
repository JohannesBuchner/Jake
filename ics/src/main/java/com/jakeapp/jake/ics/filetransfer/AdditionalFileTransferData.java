package com.jakeapp.jake.ics.filetransfer;

import java.io.File;

/**
 * contains additional data the filetransfer or user of the filetransfer might
 * want to store
 * 
 * At the moment, only the local file. 
 * 
 * @author johannes
 * 
 */
final public class AdditionalFileTransferData {

	public AdditionalFileTransferData(File f) {
		setDataFile(f);
	}

	private File dataFile;

	public File getDataFile() {
		return this.dataFile;
	}

	public void setDataFile(File outputFile) {
		this.dataFile = outputFile;
	}

}