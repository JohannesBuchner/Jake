package com.jakeapp.core.services;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.UserIdFormatException;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

/**
 * Implementation of the MessageService for the XMPP Messaging Protocol.
 * One per User.
 */
public class XMPPMsgService extends MsgService<User> {
	private static Logger log = Logger.getLogger(XMPPMsgService.class);

	public static final String namespace = "http://jakeapp.com/protocols/xmpp/versions/1";

	private XmppICService mainIcs;

	public XMPPMsgService() {
		super();
		this.protocolType = ProtocolType.XMPP;
		mainIcs = new XmppICService(namespace, "Jake");

		log.debug("IN constructor of XMPPMsgService and now registering InvitationListener");
		// the invitationhandler has to be registered before other things are done or the ics gets used
		mainIcs.getMsgService().registerReceiveMessageListener(this.invitationHandler);

	}

	@Override
	protected boolean doCredentialsCheck() {
		Account cred = this.getServiceCredentials();
		log.debug("got credentials: " + cred.getUserId() + " pwl: "
				+ cred.getPlainTextPassword().length());
		if (!this.getMainUserId().isOfCorrectUseridFormat()) {
			return false;
		}
		return !cred.getPlainTextPassword().isEmpty();
	}

	@Override
	protected void doLogin() throws NetworkException {
		String pass = this.getServiceCredentials().getPlainTextPassword();
		String host = this.getServiceCredentials().getServerAddress();
		long   port = this.getServiceCredentials().getServerPort();
		log.debug("got credentials: " + this.getServiceCredentials());

		// this needs to be done before loging in.
		this.mainIcs.getMsgService().registerReceiveMessageListener(this.invitationHandler);

		this.mainIcs.getStatusService().login(this.getMainUserId(),
				pass, host, port);

				this.mainIcs.getMsgService().registerReceiveMessageListener(this.invitationHandler);

	}

	@Override
	protected void doLogout() throws NetworkException {
		log.debug("XMPPMsgService -> logout");

		this.mainIcs.getStatusService().logout();
	}

	@Override
	public User getUserId(String userId) throws UserIdFormatException {
		return new User(ProtocolType.XMPP, userId);
	}

	@Override
	public User getUserId() {
		return this.userId;
	}

	@Override
	public void createAccount() throws NetworkException {
		this.mainIcs.getStatusService().createAccount(getMainUserId(),
				this.getServiceCredentials().getPlainTextPassword());
	}

	@Override
	protected ICService getMainIcs() {
		return this.mainIcs;
	}

	@Override
	protected XmppUserId getMainUserId() {
		return new XmppUserId(new XmppUserId(this.getServiceCredentials().getUserId())
				.getUserIdWithOutResource()
				+ "/Jake");
	}

	@Override
	protected com.jakeapp.jake.ics.UserId getIcsUser(ICService ics,
			com.jakeapp.core.services.MsgService.ICData listeners) {
		return new XmppUserId(this.getMainUserId().getUserIdWithOutResource() + "/"
				+ listeners.name);
	}
}
