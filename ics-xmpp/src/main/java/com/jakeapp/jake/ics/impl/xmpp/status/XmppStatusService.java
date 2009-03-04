package com.jakeapp.jake.ics.impl.xmpp.status;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.impl.xmpp.XmppConnectionData;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.impl.xmpp.helper.RosterPresenceChangeListener;
import com.jakeapp.jake.ics.impl.xmpp.helper.XmppCommons;
import com.jakeapp.jake.ics.status.ILoginStateListener;
import com.jakeapp.jake.ics.status.IStatusService;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.ServiceDiscoveryManager;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class XmppStatusService implements IStatusService {

	private static final Logger log = Logger.getLogger(XmppStatusService.class);

	private XmppConnectionData con;

	private List<ILoginStateListener> lsll = new LinkedList<ILoginStateListener>();

	private ConnectionListener connectionListener = new XmppConnectionListener();

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
		// TODO replace with real implementation (VCard)
		XmppUserId xid = new XmppUserId(userid);
		if (!xid.isOfCorrectUseridFormat())
			throw new NoSuchUseridException();

		if (!xid.getUsername().contains(".")) {
			return "";
		}
		return xid.getUsername().substring(0, xid.getUsername().indexOf("."));
	}

	@Override
	public String getLastname(UserId userid) throws NoSuchUseridException,
			  OtherUserOfflineException {
		// TODO replace with real implementation (VCard)
		XmppUserId xid = new XmppUserId(userid);
		if (!xid.isOfCorrectUseridFormat())
			throw new NoSuchUseridException();

		if (!xid.getUsername().contains(".")) {
			return "";
		}
		return xid.getUsername().substring(
				  xid.getUsername().indexOf(".") + 1);
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
	public XmppUserId getUserid() throws NotLoggedInException {
		if (!isLoggedIn())
			throw new NotLoggedInException();

		return this.con.getUserId();
	}

	@Override
	public Boolean isLoggedIn() {
		return XmppCommons.isLoggedIn(this.con.getConnection());
	}

	@Override
	public Boolean isLoggedIn(UserId userid) throws NetworkException, TimeoutException {
		if (!new XmppUserId(userid).isOfCorrectUseridFormat())
			throw new NoSuchUseridException();
		if (XmppUserId.isSameUser(getUserid(), userid))
			return isLoggedIn();
		if (!isLoggedIn())
			throw new NotLoggedInException();

		if (getRoster().getEntry(userid.toString()) != null) {
			log.debug("Type for " + userid + ": "
					  + getRoster().getEntry(userid.toString()).getType());
			log.debug("Status for " + userid + ": "
					  + getRoster().getEntry(userid.toString()).getStatus());
		}
		Presence p = getRoster().getPresence(userid.toString());
		log.debug("Presence for " + userid + ": " + p);
		return p.isAvailable();
	}

	private Roster getRoster() throws NotLoggedInException {
		if (!this.con.getService().getStatusService().isLoggedIn())
			throw new NotLoggedInException();
		return this.con.getConnection().getRoster();
	}

	@Override
	public void login(UserId userid, String pw) throws NetworkException {
		XmppUserId xuid = new XmppUserId(userid);
		if (!xuid.isOfCorrectUseridFormat())
			throw new NoSuchUseridException();

		if (isLoggedIn())
			logout();

		this.con.setConnection(null);

		XMPPConnection connection;
		fireConnectionStateChanged(ILoginStateListener.ConnectionState.CONNECTING);

		try {
			connection = XmppCommons.login(xuid.getUserId(), pw, xuid.getResource());
			connection.addConnectionListener(connectionListener);
		} catch (IOException e) {
			log.debug("connecting failed");
			throw new NetworkException(e);
		}

		connection.sendPacket(new Presence(Presence.Type.available));
		this.con.setConnection(connection);
		addDiscoveryFeature();
		registerForEvents();

		getRoster().setSubscriptionMode(Roster.SubscriptionMode.accept_all);
		fireConnectionStateChanged(ILoginStateListener.ConnectionState.LOGGED_IN);
	}


	private void registerForEvents() throws NotLoggedInException {
		getRoster().addRosterListener(new RosterPresenceChangeListener() {

			public void presenceChanged(Presence presence) {
				final String xmppid = presence.getFrom();
				XmppStatusService.log.debug("presenceChanged: " + xmppid
						  + " - " + presence);

				if (isLoggedIn()) {
					try {
						XmppStatusService.this.con.getService()
								  .getUsersService().requestOnlineNotification(
								  new XmppUserId(xmppid));
					} catch (NotLoggedInException e) {
						log.debug("Shouldn't happen", e);
					}
				} else {
					// skip. We don't want notifications after we logout.
				}
			}
		});
	}


	@Override
	public void logout() throws NetworkException {
		XmppCommons.logout(this.con.getConnection());
		if(this.con.getConnection() != null) {
			this.con.getConnection().removeConnectionListener(connectionListener);
		}
		this.con.setConnection(null);
		fireConnectionStateChanged(ILoginStateListener.ConnectionState.LOGGED_OUT);
	}


	/**
	 * Fires the new Connection state to all registered listeners.
	 * @param state
	 */
	private void fireConnectionStateChanged(ILoginStateListener.ConnectionState state) {
		for (ILoginStateListener lsl : lsll) {
			lsl.connectionStateChanged(state);
		}
	}

	@Override
	public void createAccount(UserId userid, String pw) throws NetworkException {
		if (!new XmppUserId(userid).isOfCorrectUseridFormat())
			throw new NoSuchUseridException();
		if (isLoggedIn())
			logout();
		this.con.setConnection(null);

		XMPPConnection connection;
		try {
			connection = XmppCommons.createAccount(userid.getUserId(), pw);
		} catch (IOException e) {
			log.debug("create failed: " + e.getMessage());
			throw new NetworkException(e);
		}
		if (connection == null) {
			throw new RuntimeException("Connection is null!");
		}
		XmppCommons.logout(connection);
	}

	@Override
	public void addLoginStateListener(ILoginStateListener lsl) {
		log.debug("Adding LoginStateListener");
		lsll.add(lsl);
	}


	/**
	 * Inner class to translate smack's connection events to our common interface
	 */
	private class XmppConnectionListener implements ConnectionListener {
		@Override public void connectionClosed() {
			fireConnectionStateChanged(ILoginStateListener.ConnectionState.LOGGED_OUT);
		}

		@Override public void connectionClosedOnError(Exception e) {
			fireConnectionStateChanged(ILoginStateListener.ConnectionState.LOGGED_OUT);
		}

		@Override public void reconnectingIn(int i) {
			fireConnectionStateChanged(ILoginStateListener.ConnectionState.RECONNECTING);
		}

		@Override public void reconnectionSuccessful() {
			fireConnectionStateChanged(ILoginStateListener.ConnectionState.LOGGED_IN);
		}

		@Override public void reconnectionFailed(Exception e) {
			fireConnectionStateChanged(ILoginStateListener.ConnectionState.LOGGED_OUT);
		}
	}
}