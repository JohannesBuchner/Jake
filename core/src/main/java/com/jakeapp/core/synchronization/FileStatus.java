package com.jakeapp.core.synchronization;

/**
 * struct for giving over to the gui
 * 
 * @author johannes
 */
public class FileStatus {

	private String filename;

	private boolean inConflict;

	private long lastModification;

	private boolean locallyModified;

	
	public FileStatus(String filename, boolean inConflict, long lastModification,
			boolean locallyModified) {
		super();
		this.filename = filename;
		this.inConflict = inConflict;
		this.lastModification = lastModification;
		this.locallyModified = locallyModified;
	}


	public String getFilename() {
		return this.filename;
	}

	
	public boolean isInConflict() {
		return this.inConflict;
	}

	
	public long getLastModification() {
		return this.lastModification;
	}

	
	public boolean isLocallyModified() {
		return this.locallyModified;
	}

}
