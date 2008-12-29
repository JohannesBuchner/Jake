package com.jakeapp.jake.ics;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.Ignore;

import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

@Ignore
public class TestEnvironment {
	private static final Logger log = Logger.getLogger(TestEnvironment.class);

	public static String host = "localhost";

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
			System.err.println(e.getMessage());
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
}
