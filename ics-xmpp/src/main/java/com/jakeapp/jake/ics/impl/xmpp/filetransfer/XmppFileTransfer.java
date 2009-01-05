package com.jakeapp.jake.ics.impl.xmpp.filetransfer;

import java.io.File;

import org.jivesoftware.smackx.filetransfer.FileTransfer;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;


public class XmppFileTransfer implements IFileTransfer {
	private FileTransfer ft;

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getAmountWritten() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getError() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getFileSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public UserId getPeer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getProgress() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FileRequest getFileRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getLocalFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isReceiving() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
