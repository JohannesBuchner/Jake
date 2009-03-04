package com.jakeapp.jake.ics.impl.xmpp;

import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.impl.xmpp.filetransfer.XmppFileTransferFactory;
import com.jakeapp.jake.ics.impl.xmpp.msgservice.XmppMsgService;
import com.jakeapp.jake.ics.impl.xmpp.status.XmppStatusService;
import com.jakeapp.jake.ics.impl.xmpp.users.XmppUsersService;


/**
 * Every Project and the Master Connection do have a ICService.
 */
public class XmppICService extends ICService {

	protected XmppConnectionData connection;

	public XmppICService(String namespace, String groupname) {
		this.connection = new XmppConnectionData(this, namespace, groupname);
		this.fileTransferMethodFactory = new XmppFileTransferFactory(this.connection);
		this.statusService = createStatusService();
		this.msgService = new XmppMsgService(this.connection);
		this.usersService = new XmppUsersService(this.connection);
	}

	/**
	 * Creates the Status Service. Can be overridden.
	 * @return
	 */
	protected XmppStatusService createStatusService() {
		return new XmppStatusService(this.connection);
	}

	@Override
	public String getServiceName() {
		return "XMPP";
	}
}
