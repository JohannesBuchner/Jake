package com.jakeapp.jake.ics.filetransfer;

import java.io.File;
import java.io.InputStream;

import com.jakeapp.jake.ics.UserId;

/**
 * for javadoc, see Smacks IncomingFileTransfer.
 * 
 * @author johannes
 */
public interface IncomingFileTransfer {

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

	public InputStream recieveFile() throws TransferException;

	public void recieveFile(File arg0) throws TransferException;

}
