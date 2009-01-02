package com.jakeapp.jake.ics.impl.xmpp.filetransfer;

import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;


public class XmppFileTransferRequest implements com.jakeapp.jake.ics.filetransfer.FileTransferRequest {
	public com.jakeapp.jake.ics.filetransfer.IIncomingFileTransfer accept() {
		return new XmppIncomingFileTransfer(request.accept());
	}

	public String getFileName() {
		return request.getFileName();
	}

	public long getFileSize() {
		return request.getFileSize();
	}

	public String getRequestor() {
		return request.getRequestor();
	}

	public void reject() {
		request.reject();
	}

	FileTransferRequest request;
}
