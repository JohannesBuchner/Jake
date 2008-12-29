package com.jakeapp.jake.ics.impl.xmpp;

import org.jivesoftware.smack.XMPPConnection;

/**
 * A structure shared between the XMPP Services (most prominently, containing
 * the connection)
 * 
 * @author johannes
 * 
 */
public class XmppConnectionData {

	private XMPPConnection connection;

	public XmppConnectionData() {
		super();
	}

	public XMPPConnection getConnection() {
		return this.connection;
	}

	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}

	public XmppUserId getUserId() {
		return new XmppUserId(this.connection.getUser());
	}

}
