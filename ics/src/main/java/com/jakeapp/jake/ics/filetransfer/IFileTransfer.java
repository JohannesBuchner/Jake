package com.jakeapp.jake.ics.filetransfer;

import com.jakeapp.jake.ics.UserId;


/**
 * for javadoc, see Smacks FileTransfer.
 * 
 * @author johannes
 */
public interface IFileTransfer {

	public void cancel();

	public long getAmountWritten();

	public String getError();

	public String getFileName();

	public String getFilePath();

	public long getFileSize();

	public UserId getPeer();

	public double getProgress();

	public Status getStatus();

	public boolean isDone();
}
