package com.jakeapp.jake.ics;

import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

import junit.framework.Assert;


public class TestEnvironment {

	public static String host = "jakeapp.com";

	public static XMPPConnection assureUserExists(String hostname, String username,
			String password) throws XMPPException {
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

	public static XMPPConnection assureUserIdExists(XmppUserId userid, String password) throws XMPPException {
		String hostname = userid.getHost();
		String username = userid.getUsername();
		return assureUserExists(hostname, username, password);
	}
	
	public static void assureUserDeleted(XmppUserId userid, String password) throws XMPPException {
		String hostname = userid.getHost();
		String username = userid.getUsername();
		assureUserDeleted(hostname, username, password);
	}
	public static void assureUserDeleted(String host, String username, String password) throws XMPPException {
		assureUserExists(host, username, password).getAccountManager().deleteAccount();
	}
}
