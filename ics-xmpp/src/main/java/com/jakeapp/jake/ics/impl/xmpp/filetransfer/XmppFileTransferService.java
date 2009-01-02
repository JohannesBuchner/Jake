package com.jakeapp.jake.ics.impl.xmpp.filetransfer;

import java.io.InputStream;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.jake.ics.filetransfer.ITransferListener;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;
import com.jakeapp.jake.ics.impl.xmpp.XmppConnectionData;


public class XmppFileTransferService implements IFileTransferService {

	public XmppFileTransferService(XmppConnectionData connection) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerIncomingTransferListener(IncomingTransferListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public ITransferListener send(UserId user, InputStream content) {
		return null;
		// TODO Auto-generated method stub

	}

	@Override
	public void addTransferMethod(ITransferMethodFactory m) {
		// TODO Auto-generated method stub
		
	}

}
