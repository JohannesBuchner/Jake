package com.jakeapp.core.services;

import java.util.List;

import com.jakeapp.core.domain.JakeMessage;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.XMPPUserId;
import com.jakeapp.core.domain.exceptions.UserIdFormatException;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

/**
 * Implementation of the MessageService for the XMPP Messaging Protocol
 */
public class XMPPMsgService extends MsgService<XMPPUserId> {

	public static final String namespace = "http://jakeapp.com/protocols/xmpp/versions/1";

	private XmppICService ics = new XmppICService(namespace, "Jake");

	private XmppUserId user;

	private String host;

	private long port;

	@Override
	protected boolean doCredentialsCheck() {
		ServiceCredentials cred = this.getServiceCredentials();
		user = new XmppUserId(cred.getUserId());
		if (!user.isOfCorrectUseridFormat()) {
			user = null;
			return false;
		}
		host = cred.getServerAddressString();
		if (host == "" || host == null) {
			host = user.getHost();
		}
		return true;
	}

	@Override
	protected boolean doLogin() throws Exception {
		return ics.getStatusService().login(user,
				this.getServiceCredentials().getPlainTextPassword());
	}

	@Override
	protected void doLogout() throws Exception {
		ics.getStatusService().logout();
	}

	@Override
	public void sendMessage(JakeMessage message) {
		// TODO
	}

	@Override
	public List<XMPPUserId> getUserList() {
		return null; // TODO
	}

	@Override
	public XMPPUserId getUserId(String userId) throws UserIdFormatException {
		return null; // TODO
	}

	@Override
	protected boolean checkFriends(XMPPUserId friend) {
		return false; // TODO
	}

	@Override
	public List<XMPPUserId> findUser(String pattern) {
		return null; // TODO
	}

	@Override
	public String getServiceName() {
		return null; // TODO
	}

	@Override
	public boolean createAccount() throws Exception {
		return ics.getStatusService().createAccount(user,
				this.getServiceCredentials().getPlainTextPassword());
	}
}
