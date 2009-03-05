package com.jakeapp.jake.ics.impl.xmpp.helper;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;


public class XmppCommons {

	static private Logger log = Logger.getLogger(XmppCommons.class);

	private static void setupEncryption() {
		/* connection is encrypted already, we don't need password hashing too */
		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
	}

	/**
	 * provides unified login that works both for gmail-alike (username:
	 * foo@gmail.com at host:gmail.com) and standard-providers (username: bar at
	 * host:jabber.org)
	 *
	 * @param xmppid	 xmpp id
	 * @param pw			 xmpp password
	 * @param port
	 * @param host
	 * @param resource @return the connection on success, or null on login failure
	 * @throws IOException
	 */
	public static XMPPConnection login(String xmppid, String pw, String host,
					long port, String resource) throws IOException, XMPPException {

		String hostname = (host != null && host.length() > 0) ? host :
						xmppid.replaceAll("^.*@", "").replaceAll("/.*$", "");
		String usernameWithDomain = xmppid.replaceAll("/.*$", "");
		String username = xmppid.replaceAll("@.*$", "");

		log.debug("connecting/logging in with full xmppid (gmail et al) [may fail]");
		XMPPConnection connection = null;

		try {
			connection = XmppCommons.doLogin(usernameWithDomain, pw, hostname, port, resource);
		} catch (XMPPException e) {
			log.info("Received XMPPException while trying to log in with full hostname: " + e
							.getMessage());
		}

		if (connection == null) {
			log.info("ignore the exception above, it was expected");
			log.debug("connecting/logging in normally");
			connection = XmppCommons.doLogin(username, pw, hostname, port, resource);
		}
		return connection;
	}

	/**
	 * connect and login
	 *
	 * @param username
	 * @param pw
	 * @param hostname
	 * @param resource
	 * @return open connection or null on failure
	 * @throws IOException on connect failure
	 * @throws org.jivesoftware.smack.XMPPException
	 *                     general XMPP error
	 */
	private static XMPPConnection doLogin(String username, String pw, String hostname,
					long port, String resource) throws IOException, XMPPException {

		log.debug("connecting to host: '" + hostname + "'" + " with port " + port + " (port autosearch if 0)");
		ConnectionConfiguration cconfig;

		if (port > 0) {
			// hack for google!
			if(hostname.compareToIgnoreCase("talk.google.com") == 0) {
				cconfig = new ConnectionConfiguration(hostname, (int)port, "gmail.com");
			}else {
				cconfig = new ConnectionConfiguration(hostname, (int)port);
			}
		} else {
			cconfig = new ConnectionConfiguration(hostname);
		}
		XMPPConnection connection = new XMPPConnection(cconfig);

		try {
			connection.connect();
		} catch (XMPPException e) {
			throw new IOException(e.getMessage());
		}
		
		setupEncryption();
		log.debug("user: '" + username + "' passwd-length: " + pw
						.length() + " characters");
		// log.debug("user: '" + username + "' passwd: '" + pw + "'");
		try {
			log.debug("login ... ");
			if (resource == null)
				connection.login(username, pw);
			else
				connection.login(username, pw, resource);
			log.debug("login ok: " + connection.getUser());
			return connection;
		} catch (XMPPException e) {
			log.debug("login not ok: " + e.getMessage());

			try {
				if (connection != null)
					connection.disconnect();
			} catch (Exception e2) {
				// just cleanup, don't care
			}
			throw e;
		}
	}

	/**
	 * provides unified createAccount that works both for gmail-alike (username:
	 * foo@gmail.com at host:gmail.com) and standard-providers (username: bar at
	 * host:jabber.org)
	 *
	 * @param xmppid xmpp id
	 * @param pw		 xmpp password
	 * @return the connection on success, or null on login failure
	 * @throws IOException
	 */
	public static XMPPConnection createAccount(String xmppid, String pw)
					throws IOException {

		String hostname = xmppid.replaceAll(".*@", "");
		String username = xmppid.replaceAll("@.*", "");

		log.debug("connecting/logging in with full xmppid (gmail et al)");
		XMPPConnection connection = XmppCommons.createAccount(xmppid, pw, hostname);

		if (connection == null) {
			log.debug("connecting/logging in normally");
			connection = XmppCommons.createAccount(username, pw, hostname);
		}
		return connection;
	}

	/**
	 * connect and create account
	 *
	 * @param username
	 * @param pw
	 * @param hostname
	 * @return open connection or null on failure
	 * @throws IOException on connect failure
	 */
	public static XMPPConnection createAccount(String username, String pw,
					String hostname) throws IOException {

		log.debug("host: " + hostname);
		XMPPConnection connection = new XMPPConnection(hostname);

		try {
			connection.connect();
		} catch (XMPPException e) {
			throw new IOException(e.getMessage());
		}
		setupEncryption();
		log.debug("user: " + username + " passwd: " + pw);
		try {
			log.debug("login ... ");
			connection.getAccountManager().createAccount(username, pw);
			log.debug("login ok: " + connection.getUser());
			return connection;
		} catch (XMPPException e) {
			log.debug("login not ok ", e);
			connection.disconnect();
			connection = null;
			return null;
		}
	}

	/**
	 * idempotent disconnect/logout if neededs
	 *
	 * @param connection
	 */
	public static void logout(XMPPConnection connection) {
		if (connection != null) {
			log.debug("logging out.");
			connection.disconnect();
		}
	}

	/**
	 * Nullsafe method to test if the connection connected and authenticated.
	 *
	 * @param connection
	 * @return
	 */
	public static boolean isLoggedIn(XMPPConnection connection) {
		if (connection == null) {
			return false;
		}

		log.debug(connection.getUser() + " - auth: " + connection.isAuthenticated());
		return connection.isAuthenticated();
	}

}
