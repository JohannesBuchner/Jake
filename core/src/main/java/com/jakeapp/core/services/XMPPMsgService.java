package com.jakeapp.core.services;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.UserFormatException;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

/**
 * Concrete implementation of the <code>MessageService</code> for the XMPP Messaging Protocol.
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

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doLogin() throws NetworkException {
		String pass = this.getServiceCredentials().getPlainTextPassword();
		String host = this.getServiceCredentials().getServerAddress();
		long port = this.getServiceCredentials().getServerPort();
		log.debug("got credentials: " + this.getServiceCredentials());

		// this needs to be done before loging in.
		this.mainIcs.getMsgService().registerReceiveMessageListener(this.invitationHandler);

		this.mainIcs.getStatusService().login(this.getMainUserId(),
				pass, host, port);

		this.mainIcs.getMsgService().registerReceiveMessageListener(this.invitationHandler);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doLogout() throws NetworkException {
		log.debug("XMPPMsgService -> logout");

		this.mainIcs.getStatusService().logout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User getUserId(String userId) throws UserFormatException {
		return new User(ProtocolType.XMPP, userId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User getUserId() {
		return this.userId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createAccount() throws NetworkException {
		this.mainIcs.getStatusService().createAccount(getMainUserId(),
				this.getServiceCredentials().getPlainTextPassword());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ICService getMainIcs() {
		return this.mainIcs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public XmppUserId getMainUserId() {
		return new XmppUserId(new XmppUserId(this.getServiceCredentials().getUserId())
				.getUserIdWithOutResource()
				+ "/Jake");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected com.jakeapp.jake.ics.UserId getIcsUser(ICService ics,
													 com.jakeapp.core.services.MsgService.ICData listeners) {
		return new XmppUserId(this.getMainUserId().getUserIdWithOutResource() + "/"
				+ listeners.name);
	}
}
