package com.jakeapp.jake.ics.impl.xmpp.filetransfer;

import java.io.File;
import java.io.InputStream;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.TransferException;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;


public class XmppIncomingFileTransfer implements
		com.jakeapp.jake.ics.filetransfer.runningtransfer.IIncomingFileTransfer {

	private IncomingFileTransfer t;

	public XmppIncomingFileTransfer(IncomingFileTransfer t) {
		this.t = t;
	}

	public void cancel() {
		t.cancel();
	}

	public long getAmountWritten() {
		return t.getAmountWritten();
	}

	public String getError() {
		return t.getError().getMessage();
	}

	public String getFileName() {
		return t.getFileName();
	}

	public String getFilePath() {
		return t.getFilePath();
	}

	public long getFileSize() {
		return t.getFileSize();
	}

	public UserId getPeer() {
		return new XmppUserId(t.getPeer());
	}

	public double getProgress() {
		return t.getProgress();
	}

	public Status getStatus() {
		return Status.valueOf(t.getStatus().toString());
	}

	public boolean isDone() {
		return t.isDone();
	}

	public InputStream recieveFile() throws TransferException {
		try {
			return t.recieveFile();
		} catch (XMPPException e) {
			throw new TransferException(e);
		}
	}

	public void recieveFile(File arg0) throws TransferException {
		try {
			t.recieveFile(arg0);
		} catch (XMPPException e) {
			throw new TransferException(e);
		}
	}

}
