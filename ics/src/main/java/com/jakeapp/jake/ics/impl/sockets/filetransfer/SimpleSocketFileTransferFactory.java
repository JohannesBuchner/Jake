package com.jakeapp.jake.ics.impl.sockets.filetransfer;


import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;
import com.jakeapp.jake.ics.msgservice.IMsgService;

public class SimpleSocketFileTransferFactory implements ITransferMethodFactory {

	protected static final String START = "<filetransfer><![CDATA[";

	protected static final String END = "]]></filetransfer>";

	public static final int PORT = 43214;

	static Logger log = Logger.getLogger(SimpleSocketFileTransferFactory.class);

	public SimpleSocketFileTransferFactory() {
		//
	}

	@Override
	public ITransferMethod getTransferMethod(IMsgService negotiationService, UserId user) throws NotLoggedInException {
		return new SimpleSocketFileTransferMethod(negotiationService, user);
	}
}
