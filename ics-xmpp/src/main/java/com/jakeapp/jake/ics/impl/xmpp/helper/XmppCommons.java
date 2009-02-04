package com.jakeapp.jake.ics.impl.xmpp.helper;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;


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
	 * @param xmppid
	 *            xmpp id
	 * @param pw
	 *            xmpp password
	 * @return the connection on success, or null on login failure
	 * @throws IOException
	 * @see {@link XmppCommons#login(String, String, String)} for parameters and
	 *      return value
	 */
	public static XMPPConnection login(String xmppid, String pw, String resource)
			throws IOException {

		String hostname = xmppid.replaceAll("^.*@", "").replaceAll("/.*$", "");
		String username = xmppid.replaceAll("@.*$", "");

		log.debug("connecting/logging in with full xmppid (gmail et al) [may fail]");
		XMPPConnection connection = XmppCommons.login(xmppid, pw, hostname,
				resource);

		if (connection == null) {
			log.info("ignore the exception above, it was expected");
			log.debug("connecting/logging in normally");
			connection = XmppCommons.login(username, pw, hostname, resource);
		}
		return connection;
	}

	/**
	 * provides unified createAccount that works both for gmail-alike (username:
	 * foo@gmail.com at host:gmail.com) and standard-providers (username: bar at
	 * host:jabber.org)
	 * 
	 * @param xmppid
	 *            xmpp id
	 * @param pw
	 *            xmpp password
	 * @return the connection on success, or null on login failure
	 * @throws IOException
	 * @see {@link XmppCommons#login(String, String, String)} for parameters and
	 *      return value
	 */
	public static XMPPConnection createAccount(String xmppid, String pw)
			throws IOException {

		String hostname = xmppid.replaceAll(".*@", "");
		String username = xmppid.replaceAll("@.*", "");

		log.debug("connecting/logging in with full xmppid (gmail et al)");
		XMPPConnection connection = XmppCommons.createAccount(xmppid, pw,
				hostname);

		if (connection == null) {
			log.debug("connecting/logging in normally");
			connection = XmppCommons.createAccount(username, pw, hostname);
		}
		return connection;
	}

	/**
	 * connect and login
	 * 
	 * @param username
	 * @param pw
	 * @param hostname
	 * @return open connection or null on failure
	 * @throws IOException
	 *             on connect failure
	 */
	public static XMPPConnection login(String username, String pw,
			String hostname, String resource) throws IOException {

		log.debug("connecting to host: '" + hostname + "'");
		XMPPConnection connection = new XMPPConnection(hostname);

		try {
			connection.connect();
		} catch (XMPPException e) {
			throw new IOException(e.getMessage());
		}
		setupEncryption();
		log.debug("user: '" + username + "' passwd-length: " + pw.length()
				+ " characters");
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
			log.debug("login not ok ", e);
			connection.disconnect();
			connection = null;
			return null;
		}
	}

	/**
	 * connect and create account
	 * 
	 * @param username
	 * @param pw
	 * @param hostname
	 * @return open connection or null on failure
	 * @throws IOException
	 *             on connect failure
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
		if (connection == null)
			return false;
		log.debug(connection.getUser() + " - auth: "
				+ connection.isAuthenticated());
		return connection.isAuthenticated();
	}

}
