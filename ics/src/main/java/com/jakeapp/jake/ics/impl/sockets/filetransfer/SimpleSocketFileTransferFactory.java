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

	public static final int DEFAULT_PORT = 0;

	public static final int DEFAULT_MAXIMAL_REQUEST_AGE_SECONDS = 60;

	private int port = DEFAULT_PORT;

	private int maximalRequestAgeSeconds = DEFAULT_MAXIMAL_REQUEST_AGE_SECONDS;

	static Logger log = Logger.getLogger(SimpleSocketFileTransferFactory.class);

	public SimpleSocketFileTransferFactory() {
		//
	}

	public SimpleSocketFileTransferFactory(int maximalRequestAgeSeconds) {
		this.maximalRequestAgeSeconds = maximalRequestAgeSeconds;
	}

	public SimpleSocketFileTransferFactory(int maximalRequestAgeSeconds, int port) {
		this(maximalRequestAgeSeconds);
		this.port = port;
	}

	@Override
	public ITransferMethod getTransferMethod(IMsgService negotiationService, UserId user)
			throws NotLoggedInException {
		return new SimpleSocketFileTransferMethod(this.maximalRequestAgeSeconds,
				this.port, negotiationService,
				user);
	}
}
