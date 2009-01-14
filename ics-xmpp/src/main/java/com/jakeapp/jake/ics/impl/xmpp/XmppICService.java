package com.jakeapp.jake.ics.impl.xmpp;

import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.impl.xmpp.filetransfer.XmppFileTransferFactory;
import com.jakeapp.jake.ics.impl.xmpp.msgservice.XmppMsgService;
import com.jakeapp.jake.ics.impl.xmpp.status.XmppStatusService;
import com.jakeapp.jake.ics.impl.xmpp.users.XmppUsersService;


public class XmppICService extends ICService {

	private XmppConnectionData connection;

	public XmppICService(String namespace, String groupname) {
		this.connection = new XmppConnectionData(this, namespace, groupname);
		this.fileTransferMethodFactory = new XmppFileTransferFactory(this.connection);
		this.msgService = new XmppMsgService(this.connection);
		this.statusService = new XmppStatusService(this.connection);
		this.usersService = new XmppUsersService(this.connection);
	}

	@Override
	public String getServiceName() {
		return "XMPP";
	}

}
