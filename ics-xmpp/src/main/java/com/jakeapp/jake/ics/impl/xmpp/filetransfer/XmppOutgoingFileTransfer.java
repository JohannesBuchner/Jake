package com.jakeapp.jake.ics.impl.xmpp.filetransfer;

import java.io.File;
import java.io.InputStream;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.TransferException;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IOutgoingFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;


public class XmppOutgoingFileTransfer implements
		com.jakeapp.jake.ics.filetransfer.runningtransfer.IOutgoingFileTransfer {

	public OutgoingFileTransfer t;

	public void sendStream(InputStream in, String fileName, long fileSize,
			String description) {
		t.sendStream(in, fileName, fileSize, description);
	}

	public XmppOutgoingFileTransfer(OutgoingFileTransfer t) {
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
}
