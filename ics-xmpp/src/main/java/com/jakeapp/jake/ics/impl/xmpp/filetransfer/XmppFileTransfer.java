package com.jakeapp.jake.ics.impl.xmpp.filetransfer;

import org.jivesoftware.smackx.filetransfer.FileTransfer;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.Status;


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
	public String getFilePath() {
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
	
}
