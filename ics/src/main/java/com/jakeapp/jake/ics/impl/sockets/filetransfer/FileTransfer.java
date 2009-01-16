package com.jakeapp.jake.ics.impl.sockets.filetransfer;


import java.io.File;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;

public abstract class FileTransfer implements IFileTransfer {

	private static Logger log = Logger.getLogger(FileTransfer.class);

	protected Status status = Status.initial;

	protected long amountWritten = 0;

	protected FileRequest request;

	protected String error = null;

	protected File localFile;

	protected UserId peer;

	@Override
	public String toString() {
		String s = "FileTransfer(";
		s+= "request=" + this.request + ",";
		s+= "localFile=" + (this.localFile == null ? null : this.localFile.getAbsolutePath()) + ",";
		s+= "status=" + this.status + ",";
		s+= "peer=" + this.peer + ",";
		return s + ")";
	}

	public FileTransfer() {
		super();
	}

	@Override
	public long getAmountWritten() {
		return this.amountWritten;
	}

	@Override
	public String getFileName() {
		return this.request.getFileName();
	}

	@Override
	public double getProgress() {
		return Double.valueOf(getAmountWritten()) / getFileSize();
	}

	@Override
	public Status getStatus() {
		return this.status;
	}

	@Override
	public boolean isDone() {
		if (this.status == Status.in_progress || this.status == Status.negotiated)
			return false;
		else
			return true;
	}

	@Override
	public void cancel() {
		log.debug("cancelling transfer");
		this.status = Status.cancelled;
	}

	@Override
	public String getError() {
		return this.error;
	}

	@Override
	public FileRequest getFileRequest() {
		return this.request;
	}

	@Override
	public long getFileSize() {
		return this.request.getFileSize();
	}

	@Override
	public File getLocalFile() {
		return this.localFile;
	}

	@Override
	public UserId getPeer() {
		return null;
	}

	protected void setError(String error) {
		this.error = error;
		this.status = Status.error;
	}

	protected void setError(Exception e) {
		log.debug("an exception occured", e);
		setError(e.getMessage());
	}


}