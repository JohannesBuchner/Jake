package local.xmpp;

import com.jakeapp.jake.ics.impl.xmpp.helper.XmppCommons;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;


public class XmppUserTool {

	static private Logger log = Logger.getLogger(XmppUserTool.class);

	static private XMPPConnection connection = null;

	static private String resourcename = "usertool";
	
	public static void usage() {
		System.out.println("SYNAPSIS: XmppUserTool <xmppid> <xmpppw> <action>");
		System.out.println("\txmppid, xmpppw\tcredentials for login");
		System.out.println("\taction\tone of: ");
		System.out.println("\t\t\tlogin - just login and logout");
		System.out.println("\t\t\tcreate - create the user");
		System.out.println("\t\t\tdelete - delete the user");
		System.out.println();
	}

	public static void main(String[] args) {
		String action;
		String xmppid;
		String xmpppw;
		if (args.length != 3) {
			log.error(args.length + " arguments given instead of 3.");
			usage();
			return;
		}
		xmppid = args[0];
		xmpppw = args[1];
		action = args[2];

		if (!xmppid.contains("@")) {
			usage();
			return;
		}

		if (action.equals("login") || action.equals("delete")) {
			try {
				try {
					connection = XmppCommons.login(xmppid, xmpppw, null, 0, resourcename);
				} catch (XMPPException e) {
					// ignore
				}
				if (connection == null) {
					log.error("login wasn't successful.");
					return;
				}
			} catch (IOException e) {
				log.debug(e);
				log.error("An error occured during login.");
				return;
			}
			log.info("logged in.");
			if (action.equals("delete")) {
				try {
					connection.getAccountManager().deleteAccount();
					log.info("account deleted.");
				} catch (XMPPException e) {
					log.debug(e);
					log.info("deleting account failed.");
				}
			}
		} else if (action.equals("create")) {
			String hostname = xmppid.replaceAll(".*@", "");
			String username = xmppid.replaceAll("@.*", "");

			connection = new XMPPConnection(hostname);
			try {
				connection.connect();
				log.info("connected.");
			} catch (XMPPException e) {
				log.debug(e);
				log.info("connecting failed.");
				return;
			}
			try {
				connection.getAccountManager().createAccount(username, xmpppw);
				log.info("account created.");
			} catch (XMPPException e) {
				log.debug(e);
				log.info("creating account failed.");
			}
		} else {
			log.error("no valid action specified.");
		}

		XmppCommons.logout(connection);
	}
}
