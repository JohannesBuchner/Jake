package com.jakeapp.jake.ics.filetransfer;


public interface FileTransferRequest {

	public IIncomingFileTransfer accept();

	public String getFileName();

	public long getFileSize();

	public String getRequestor();

	public void reject();
	
}
