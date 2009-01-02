package com.jakeapp.jake.ics.impl.sockets.filetransfer;


import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;

public abstract class FileTransfer implements IFileTransfer {

	private static Logger log = Logger.getLogger(FileTransfer.class);

	protected Status status;

	protected long amountWritten = 0;

	protected FileRequest request;

	protected String error = null;

	protected File localFile;

	protected UserId peer;


	public FileTransfer() {
		super();
	}

	@Override
	public long getAmountWritten() {
		return amountWritten;
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
		return status;
	}

	@Override
	public boolean isDone() {
		if (status == Status.in_progress || status == Status.negotiated)
			return false;
		else
			return true;
	}

	@Override
	public void cancel() {
		status = Status.cancelled;
	}

	@Override
	public String getError() {
		return error;
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
		return localFile;
	}

	@Override
	public UserId getPeer() {
		return null;
	}

	protected void setError(String error) {
		this.error = error;
		status = Status.error;
	}
	protected void setError(Exception e) {
		log.debug("an exception occured", e);
		setError(e.getMessage());
	}


}