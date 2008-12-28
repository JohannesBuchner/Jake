package com.jakeapp.jake.ics.impl.xmpp.filetransfer;

import java.io.File;
import java.io.InputStream;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;

import com.jakeapp.jake.ics.UserId;


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

	public InputStream recieveFile() throws XMPPException;

	public void recieveFile(File arg0) throws XMPPException;

}
