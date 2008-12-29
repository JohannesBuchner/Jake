package com.jakeapp.jake.ics.impl.xmpp;

import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.impl.xmpp.filetransfer.XmppFileTransferService;
import com.jakeapp.jake.ics.impl.xmpp.msgservice.XmppMsgService;
import com.jakeapp.jake.ics.impl.xmpp.status.XmppStatusService;


public class XmppICService extends ICService {
	
	private XmppConnectionData connection = new XmppConnectionData();
	
	public XmppICService(){
		this.fileTransferService = new XmppFileTransferService(this.connection);
		this.msgService = new XmppMsgService(this.connection);
		this.statusService = new XmppStatusService(this.connection);
	}
	
	@Override
	public String getServiceName() {
		return "XMPP";
	}

}
