package com.jakeapp.jake.ics.impl.xmpp.status;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.ServiceDiscoveryManager;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.impl.mock.MockUserId;
import com.jakeapp.jake.ics.impl.xmpp.XmppConnectionData;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.impl.xmpp.helper.RosterPresenceChangeListener;
import com.jakeapp.jake.ics.impl.xmpp.helper.XmppCommons;
import com.jakeapp.jake.ics.status.IStatusService;


public class XmppStatusService implements IStatusService {

	private static final Logger log = Logger.getLogger(XmppStatusService.class);

	private XmppConnectionData con;

	public XmppStatusService(XmppConnectionData connection) {
		this.con = connection;
	}

	private void addDiscoveryFeature() {
		// Obtain the ServiceDiscoveryManager associated with my XMPPConnection
		ServiceDiscoveryManager discoManager = ServiceDiscoveryManager
				.getInstanceFor(this.con.getConnection());

		// Register that a new feature is supported by this XMPP entity
		discoManager.addFeature(this.con.getNamespace());
	}

	@Override
	public String getFirstname(UserId userid) throws NoSuchUseridException,
			OtherUserOfflineException {
		// TODO replace with real implementation
		if (!new XmppUserId(userid).isOfCorrectUseridFormat())
			throw new NoSuchUseridException();

		if (!userid.getUserId().contains(".")) {
			return "";
		}
		return userid.getUserId().substring(0, userid.getUserId().indexOf("."));
	}

	@Override
	public String getLastname(UserId userid) throws NoSuchUseridException,
			OtherUserOfflineException {
		// TODO replace with real implementation
		if (!new MockUserId(userid).isOfCorrectUseridFormat())
			throw new NoSuchUseridException();

		if (!userid.getUserId().contains(".")) {
			return "";
		}
		return userid.getUserId().substring(
				userid.getUserId().indexOf(".") + 1,
				userid.getUserId().indexOf("@"));
	}

	@Override
	public UserId getUserId(String userid) {
		UserId ui = new XmppUserId(userid);
		if (ui.isOfCorrectUseridFormat())
			return ui;
		else
			return null;
	}

	@Override
	public UserId getUserid() throws NotLoggedInException {
		if (!isLoggedIn())
			throw new NotLoggedInException();

		return this.con.getUserId();
	}

	@Override
	public Boolean isLoggedIn() {
		return XmppCommons.isLoggedIn(this.con.getConnection());
	}

	@Override
	public Boolean isLoggedIn(UserId userid) throws NoSuchUseridException,
			NetworkException, NotLoggedInException, TimeoutException {
		if (!new XmppUserId(userid).isOfCorrectUseridFormat())
			throw new NoSuchUseridException();
		if (userid.equals(getUserid()))
			return isLoggedIn();
		if (!isLoggedIn())
			throw new NotLoggedInException();

		Presence p = getRoster().getPresence(userid.toString());
		
		if (p.isAvailable())
			return true;
		else
			return false;
	}
	
	private Roster getRoster() throws NotLoggedInException {
		if (!this.con.getService().getStatusService().isLoggedIn())
			throw new NotLoggedInException();
		return this.con.getConnection().getRoster();
	}

	@Override
	public Boolean login(UserId userid, String pw) throws NetworkException,
			TimeoutException {
		if (!new XmppUserId(userid).isOfCorrectUseridFormat())
			throw new NoSuchUseridException();
		if (isLoggedIn())
			logout();

		XMPPConnection connection;
		try {
			connection = XmppCommons.login(userid.getUserId(), pw);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
		if (connection == null) {
			return false;
		}
		this.con.setConnection(connection);
		addDiscoveryFeature();
		registerForEvents();

		getRoster().setSubscriptionMode(Roster.SubscriptionMode.accept_all);

		return true;
	}

	private void registerForEvents() throws NotLoggedInException {
		getRoster().addRosterListener(new RosterPresenceChangeListener() {

			public void presenceChanged(Presence presence) {
				final String xmppid = presence.getFrom();
				XmppStatusService.log.debug("presenceChanged: " + xmppid + " - " + presence);
				try {
					XmppStatusService.this.con.getService().getUsersService().requestOnlineNotification(new XmppUserId(xmppid));
				} catch (NotLoggedInException e) {
					log.debug("Shouldn't happen", e);
				}
			}
		});
	}


	@Override
	public void logout() throws NetworkException, TimeoutException {
		XmppCommons.logout(this.con.getConnection());
		this.con.setConnection(null);
	}

}
