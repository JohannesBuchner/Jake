package com.jakeapp.jake.ics.filetransfer.runningtransfer;

import java.io.File;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;


/**
 * for javadoc, see Smacks FileTransfer.
 * 
 * @author johannes
 */
public interface IFileTransfer {

	public void cancel();

	public long getAmountWritten();

	/**
	 * @return null if no error occured, the error description otherwise 
	 */
	public String getError();

	public String getFileName();

	public File getLocalFile();

	public long getFileSize();

	public UserId getPeer();

	public double getProgress();

	public Status getStatus();

	public boolean isDone();
	
	public FileRequest getFileRequest();
	
	public Boolean isReceiving();
}
