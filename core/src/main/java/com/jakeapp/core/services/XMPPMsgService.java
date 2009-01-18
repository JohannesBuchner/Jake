package com.jakeapp.core.services;

import com.jakeapp.core.domain.JakeMessage;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.XMPPUserId;
import com.jakeapp.core.domain.exceptions.UserIdFormatException;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

/**
 * Implementation of the MessageService for the XMPP Messaging Protocol
 */
public class XMPPMsgService extends MsgService<XMPPUserId> {


	public static final String namespace = "http://jakeapp.com/protocols/xmpp/versions/1";

	private XmppICService ics = new XmppICService(namespace, "Jake");

     private XmppUserId icsXmppUserId;

	private String host;


    public XMPPMsgService() {
        this.setUserId(new XMPPUserId(this.getServiceCredentials(), UUID.randomUUID(), "todo useridstring", "todo nickname", "todo firstname", "todo surname")); 

    }

    @Override
	protected boolean doCredentialsCheck() {
		ServiceCredentials cred = this.getServiceCredentials();
		this.icsXmppUserId = new XmppUserId(cred.getUserId());
		if (!this.icsXmppUserId.isOfCorrectUseridFormat()) {
			this.icsXmppUserId = null;
			return false;
		}
		this.host = cred.getServerAddressString();
		if (this.host == "" || this.host == null) {
			this.host = this.icsXmppUserId.getHost();
		}
		return true;
	}

	@Override
	protected boolean doLogin() throws Exception {
		return this.ics.getStatusService().login(this.icsXmppUserId,
				  this.getServiceCredentials().getPlainTextPassword());
	}

	@Override
	protected void doLogout() throws Exception {
		this.ics.getStatusService().logout();
	}

	@Override
	public void sendMessage(JakeMessage message) {
		// TODO
	}

	@Override
	public List<XMPPUserId> getUserList() {
        List<XMPPUserId> result = new ArrayList<XMPPUserId>();
                      // TODO

        return result;
	}

	@Override
	public XMPPUserId getUserId(String userId) throws UserIdFormatException {
		XMPPUserId result = new XMPPUserId(this.getServiceCredentials(), UUID.randomUUID(), "todo test",
                "todo nickname", "todo firstname", "todo surname");

        return result;
	}

	@Override
	protected boolean checkFriends(XMPPUserId friend) {
		return false; // TODO
	}

	@Override
	public List<XMPPUserId> findUser(String pattern) {
		List<XMPPUserId> result = new ArrayList<XMPPUserId>();
                       // TODO
        return result;
	}

	@Override
	public String getServiceName() {
		return "XMPP";
	}

	@Override
	public void createAccount() throws NetworkException {
		ics.getStatusService().createAccount(icsXmppUserId,
				  this.getServiceCredentials().getPlainTextPassword());
	}
}
