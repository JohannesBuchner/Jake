package com.jakeapp.core.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.JakeMessage;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.UserIdFormatException;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

/**
 * Implementation of the MessageService for the XMPP Messaging Protocol
 */
public class XMPPMsgService extends MsgService<com.jakeapp.core.domain.UserId> {

	private static Logger log = Logger.getLogger(XMPPMsgService.class);

	public static final String namespace = "http://jakeapp.com/protocols/xmpp/versions/1";

	private XmppICService mainIcs = new XmppICService(namespace, "Jake");

	private XmppUserId icsXmppUserId;

	private String host;

	public XMPPMsgService() {

	}

	@Override
	protected boolean doCredentialsCheck() {
		ServiceCredentials cred = this.getServiceCredentials();
		log.debug("got credentials: " + cred.getUserId() + " pwl: "
				+ cred.getPlainTextPassword().length());
		this.icsXmppUserId = new XmppUserId(new XmppUserId(cred.getUserId())
				.getUserIdWithOutResource()
				+ "/Jake");
		if (!this.icsXmppUserId.isOfCorrectUseridFormat()) {
			this.icsXmppUserId = null;
			return false;
		}
		this.host = cred.getServerAddress();
		if (this.host == null || this.host.isEmpty()) {
			this.host = this.icsXmppUserId.getHost();
		}
		if (cred.getPlainTextPassword().isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean doLogin() throws NetworkException {
		log.debug("got credentials: "
				+ this.getServiceCredentials().getUserId() + " pwl: "
				+ this.getServiceCredentials().getPlainTextPassword().length());
		boolean success = this.mainIcs.getStatusService().login(this.icsXmppUserId,
				this.getServiceCredentials().getPlainTextPassword());


		if (success) {
			log.debug("login success");
			/*
			try {
				com.jakeapp.core.domain.UserId result = getUserIdDao().get(
						this.getUser());
				this.setUserId(XMPPUserId.createFromUserId(result));
			} catch (NoSuchUserIdException e) {
				e.printStackTrace();
			} catch (InvalidUserIdException e) {
				XMPPUserId xmppResult = new XMPPUserId(this
						.getServiceCredentials(), UUID.fromString(this
						.getServiceCredentials().getUuid()), this
						.getServiceCredentials().getUser(), this
						.getServiceCredentials().getUser(), "", "");

				try {
					getUserIdDao().create(xmppResult);
					this.setUserId(xmppResult);
				} catch (InvalidUserIdException e1) {
					e1.printStackTrace();
				}
			}
			*/
		}

		return success;
	}

	@Override
	protected void doLogout() throws Exception {
		log.debug("XMPPMsgService -> logout");

		this.mainIcs.getStatusService().logout();
	}

	@Override
	public void sendMessage(JakeMessage message) {
		// TODO
	}

	@Override
	public List<com.jakeapp.core.domain.UserId> getUserList() {
		List<com.jakeapp.core.domain.UserId> result = new ArrayList<com.jakeapp.core.domain.UserId>();

		// List<com.jakeapp.core.domain.UserId> dbInput;
		// List<String> userIds = new LinkedList<String>();
		//
		//
		// try {
		// dbInput = getUserIdDao().getAll(this.getServiceCredentials());
		//
		// for (com.jakeapp.core.domain.UserId user : result) {
		// userIds.add(user.getUser());
		// }
		//
		// for (UserId user : this.ics.getUsersService().getUsers()) {
		//
		// if (!userIds.contains(user.getUser()))
		// try {
		// result.add(
		// XMPPUserId.createFromUserId(
		// getUserIdDao().create(
		//
		// new XMPPUserId(
		// this.getServiceCredentials(),
		// UUID.randomUUID(),
		// user.getUser(),
		// user.getUser(), "", ""
		// )
		// )
		// )
		// );
		// userIds.add(user.getUser());
		// } catch (InvalidUserIdException e) {
		// e.printStackTrace();
		// }
		// }
		//
		//
		// } catch (InvalidCredentialsException e) {
		// e.printStackTrace();
		// } catch (NotLoggedInException e) {
		// e.printStackTrace();
		// }

		return result;
	}

	@Override
	public UserId getUserId(String userId) throws UserIdFormatException {
		log.debug("calling getUser");

		UserId result = new UserId(ProtocolType.XMPP, userId);
		return result;

		//
		// if (super.getUser() == null) {
		// log.debug("current userid is null");
		//
		// try {
		// setUserId(
		// XMPPUserId.createFromUserId(getUserIdDao().get(UUID.fromString(this.getServiceCredentials().getUuid())))
		//
		// );
		// return this.userId;
		// } catch (InvalidUserIdException e) {
		// log.debug("InvalidUserIdException couldn't get UserId");
		// e.printStackTrace();
		// } catch (NoSuchUserIdException e) {
		// log.debug("NoSuchUserIdException couldn't get UserId");
		// e.printStackTrace();
		// }
		//
		// if (super.getUser() == null) {
		// log.debug("userid is still null");
		// XMPPUserId result = new XMPPUserId(this.getServiceCredentials(), UUID
		// .randomUUID(), "TODO test", "todo nickname", "todo firstname",
		// "todo surname");
		// return result;
		// }
		// else
		// {
		// log.debug("userid is not null");
		// return this.userId;
		// }
		//
		// }
		//
		// return this.userId;

	}

	@Override
	public UserId getUserId() {
		return this.userId;
	}

	@Override
	protected boolean checkFriends(UserId friend) {
		return false; // TODO
	}

	@Override
	public List<UserId> findUser(String pattern) {
		List<UserId> result = new ArrayList<UserId>();
		// TODO
		return result;
	}

	@Override
	public String getServiceName() {
		return "XMPP";
	}

	@Override
	public void createAccount() throws NetworkException {
		this.mainIcs.getStatusService().createAccount(icsXmppUserId,
				this.getServiceCredentials().getPlainTextPassword());
	}

	@Override
	protected ICService getMainIcs() {
		return this.mainIcs;
	}
}
