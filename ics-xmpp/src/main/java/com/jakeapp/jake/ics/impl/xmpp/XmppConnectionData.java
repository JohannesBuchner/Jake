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

	private XmppICService service;

	private String namespace;

	private String groupname;

	public XmppConnectionData(XmppICService service, String namespace,
			String groupname) {
		super();
		this.service = service;
		this.namespace = namespace;
		this.groupname = groupname;
	}

	public XMPPConnection getConnection() {
		return this.connection;
	}

	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}

	/**
	 * reflect ourselves to combine the services
	 * @return
	 */
	public XmppICService getService() {
		return this.service;
	}

	/**
	 * The XML namespace to use to identify users online with a compatible
	 * client
	 * @return
	 */
	public String getNamespace() {
		return this.namespace;
	}

	/**
	 * The Roster group name to use
	 * @return
	 */
	public String getGroupname() {
		return this.groupname;
	}

	/* short functions */
	
	public XmppUserId getUserId() {
		return new XmppUserId(this.connection.getUser());
	}
}
