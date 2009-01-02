package com.jakeapp.jake.ics.filetransfer;

import java.io.File;

import com.jakeapp.jake.ics.UserId;

public abstract class TransferData {

	private String filename;

	private UserId partner;

	private File dataFile;

	private IFileTransfer transfer;

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public IFileTransfer getTransfer() {
		return this.transfer;
	}

	public void setTransfer(IFileTransfer transfer) {
		this.transfer = transfer;
	}


	public UserId getPartner() {
		return this.partner;
	}


	public void setPartner(UserId partner) {
		this.partner = partner;
	}


	public File getDataFile() {
		return this.dataFile;
	}


	public void setDataFile(File outputFile) {
		this.dataFile = outputFile;
	}

	/**
	 * @return null if yet undefined (not started), true if receiving, false if
	 *         sending
	 */
	abstract public Boolean isReceiving();

}