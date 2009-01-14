package com.jakeapp.jake.ics.impl.xmpp.filetransfer;


import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPConnection;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;
import com.jakeapp.jake.ics.impl.xmpp.XmppConnectionData;
import com.jakeapp.jake.ics.msgservice.IMsgService;

/**
 * {@link ITransferMethodFactory} for Xmpp.
 * 
 * @author johannes
 */
public class XmppFileTransferFactory implements ITransferMethodFactory {

	protected static final String START = "<filetransfer><![CDATA[";

	protected static final String END = "]]></filetransfer>";

	static Logger log = Logger.getLogger(XmppFileTransferFactory.class);

	private XmppConnectionData connection;

	public XmppFileTransferFactory(XmppConnectionData connection) {
		this.connection = connection;
	}

	@Override
	public ITransferMethod getTransferMethod(IMsgService negotiationService, UserId user)
			throws NotLoggedInException {
		return new XmppFileTransferMethod(connection.getConnection(), negotiationService,
				user);
	}
}
