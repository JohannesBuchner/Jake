package com.jakeapp.gui.console;

import java.io.IOException;

import com.jakeapp.core.services.XMPPMsgService;
import com.jakeapp.gui.console.commandline.LazyCommand;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;


/**
 * Test client accepting cli input
 */
public class XmppCommander extends Commander {

	public XmppCommander(String[] args) {
		super(args);
	}

	public static void main(String[] args) {
		new XmppCommander(args);
	}

	@Override
	protected void onShutdown() {
		//
	}

	@Override
	protected void onStartup() {
		//
	}

	private XmppICService ics = null;

	class Login extends LazyCommand {

		public Login() {
			super("login", "login <xmppid> <passwd> <groupname>",
					"xmppid = user@host/resource where resource is 'Jake' or the "
							+ "projectid, groupname = projectname (group in roster)");
		}

		@Override
		public boolean handleArguments(String[] args) {
			if (args.length != 4)
				return false;
			XmppUserId id = new XmppUserId(args[1]);
			XmppCommander.this.ics = new XmppICService(XMPPMsgService.namespace, args[3]);
			try {
				XmppCommander.this.ics.getStatusService().login(id, args[2]);
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (NetworkException e) {
				e.printStackTrace();
			}
			return true;
		}

	}

	class Logout extends LazyCommand {

		public Logout() {
			super("logout", "logout", "groupname = projectname (group in roster)");
		}

		@Override
		public boolean handleArguments(String[] args) {
			try {
				XmppCommander.this.ics.getStatusService().logout();
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (NetworkException e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	class ListUsers extends LazyCommand {

		public ListUsers() {
			super("listUsers");
		}

		@Override
		public boolean handleArguments(String[] args) {
			try {
				for (UserId u : XmppCommander.this.ics.getUsersService().getUsers()) {
					System.out.println(u + " - capable? "
							+ XmppCommander.this.ics.getUsersService().isCapable(u));
				}
			} catch (NetworkException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
	}

}
