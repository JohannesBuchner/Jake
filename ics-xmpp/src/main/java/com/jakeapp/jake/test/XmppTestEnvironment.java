package com.jakeapp.jake.test;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.Ignore;

import com.googlecode.junit.ext.Checker;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

@Ignore
public class XmppTestEnvironment implements Checker {

	private static final Logger log = Logger
			.getLogger(XmppTestEnvironment.class);

	private static final String TESTSERVER_PROPERTY = "com.jakeapp.jake.ics.impl.xmpp.test.serverHostname";

	private static String host;
	static {
		String prop = System.getProperty(TESTSERVER_PROPERTY);
		if (prop == null || prop.isEmpty() || prop.equals("false")) {
			log.warn("Skipping tests against server. ");
			log.info("call with -D" + TESTSERVER_PROPERTY + "="
					+ "localhost if you want to perform all the tests");
			host = null;
		} else {
			host = prop;
		}
	}

	public static boolean serverIsAvailable() {
		if (host == null) {
			return false;
		} else {
			try {
				XMPPConnection c = new XMPPConnection(host);
				c.connect();
				c.disconnect();
				return true;
			} catch (XMPPException e) {
				log.warn("Server not working!");
				return false;
			}
		}
	}

	public static void assureUserExists(String hostname, String username,
			String password) throws XMPPException {
		assureUserExistsAndConnect(hostname, username, password).disconnect();
	}

	public static void assureUserIdExists(XmppUserId userid, String password)
			throws XMPPException {
		assureUserIdExistsAndConnect(userid, password).disconnect();
	}

	public static XMPPConnection assureUserExistsAndConnect(String hostname,
			String username, String password) throws XMPPException {
		log.debug(hostname);
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
		try {
			connection.login(username, password);
		} catch (XMPPException e) {
			log
					.debug("login failed, user probably doesn't exist, creating the user now. ["
							+ e.getMessage() + "]");
			Assert.assertTrue(connection.getAccountManager()
					.supportsAccountCreation());
			connection.getAccountManager().createAccount(username, password);
			connection.disconnect();
			connection.connect();
			SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			connection.login(username, password);
		}
		return connection;
	}

	public static XMPPConnection assureUserIdExistsAndConnect(
			XmppUserId userid, String password) throws XMPPException {
		String hostname = userid.getHost();
		String username = userid.getUsername();
		return assureUserExistsAndConnect(hostname, username, password);
	}

	public static void assureUserDeleted(XmppUserId userid, String password)
			throws XMPPException {
		String hostname = userid.getHost();
		String username = userid.getUsername();
		assureUserDeleted(hostname, username, password);
	}

	public static void assureUserDeleted(String host, String username,
			String password) throws XMPPException {
		assureUserExistsAndConnect(host, username, password)
				.getAccountManager().deleteAccount();
	}

	public static String getXmppId(String username) {
		return username + "@" + host;
	}

	public static String getHost() {
		return host;
	}

	@Override
	public boolean satisfy() {
		return host != null;
	}

}
