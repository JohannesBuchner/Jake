package com.jakeapp.core.commander;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jakeapp.core.commander.commandline.CmdManager;
import com.jakeapp.core.commander.commandline.LazyCommand;
import com.jakeapp.core.commander.commandline.StoppableCmdManager;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.util.availablelater.AvailabilityListener;

/**
 * Test client accepting cli input
 */
public class JakeCommander {

	@SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(JakeCommander.class);

	private final CmdManager cmd = StoppableCmdManager.getInstance();

	private String sessionid;

	private IFrontendService frontend;

	@SuppressWarnings("unchecked")
	private MsgService msg;

	public static void main(String[] args) {
		boolean help = false;
		InputStream instream;
		if (args.length == 1) {
			try {
				instream = new FileInputStream(args[0]);
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
				return;
			}
		} else {
			instream = System.in;
			help = true;
		}
		new JakeCommander(instream, help);
	}

	public JakeCommander(InputStream instream) {
		this(instream, false);
	}

	public JakeCommander(InputStream instream, boolean startwithhelp) {
		startupCore();
		addCommands();
		try {
			if (startwithhelp)
				cmd.help();

			cmd.handle(instream);
		} catch (IOException e) {
		}
	}

	private void startupCore() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "/com/jakeapp/core/applicationContext.xml" });
		frontend = (IFrontendService) applicationContext
				.getBean("frontendService");

		try {
			sessionid = frontend.authenticate(new HashMap<String, String>());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private abstract class LazyAvailabilityCommand<T> extends LazyCommand
			implements AvailabilityListener<T> {

		public LazyAvailabilityCommand(String command, String syntax,
				String help) {
			super(command, syntax, help);
		}

		public LazyAvailabilityCommand(String command, String syntax) {
			super(command, syntax);
		}

		public LazyAvailabilityCommand(String command) {
			super(command);
		}

	}

	private void addCommands() {
		class CreateAccountCommand extends LazyAvailabilityCommand<Void> {

			public CreateAccountCommand() {
				super("createAccount", "createAccount <xmppid> <password>");
			}

			@Override
			public boolean handleArguments(String[] args) {
				if (args.length != 3)
					return false;
				String id = args[1];
				String password = args[2];
				ServiceCredentials cred = new ServiceCredentials(id, password);
				cred.setProtocol(ProtocolType.XMPP);
				try {
					frontend.createAccount(sessionid, cred).setListener(this);
					System.out.println("got the MsgService");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}

			@Override
			public void error(Exception t) {
				System.out.println(t.getMessage());
			}

			@Override
			public void finished(Void o) {
				System.out.println("done.");
			}

			@Override
			public void statusUpdate(double progress, String status) {
				System.out.println("status: " + status + " - " + progress);
			}

		};
		cmd.registerCommand(new CreateAccountCommand());
		class NewProjectCommand extends LazyCommand {

			public NewProjectCommand() {
				super("newProject", "newProject <Folder>");
			}

			@Override
			public boolean handleArguments(String[] args) {
				if (args.length != 2)
					return false;
				File projectFolder = new File(args[1]);
				if (!(projectFolder.exists() && projectFolder.isDirectory())) {
					System.out.println("not a directory");
					return true;
				}

				return true;
			}
		};
		cmd.registerCommand(new NewProjectCommand());

		class CoreLoginCommand extends LazyCommand {

			public CoreLoginCommand() {
				super("coreLogin", "coreLogin <xmppid> <password>");
			}

			@Override
			public boolean handleArguments(String[] args) {
				if (args.length != 3)
					return false;

				String id = args[1];
				String password = args[2];
				ServiceCredentials cred = new ServiceCredentials(id, password);
				cred.setProtocol(ProtocolType.XMPP);
				try {
					msg = frontend.addAccount(sessionid, cred);
					System.out.println("got the MsgService");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		};
		cmd.registerCommand(new CoreLoginCommand());

		class CoreLogoutCommand extends LazyCommand {

			public CoreLogoutCommand() {
				super("coreLogout");
			}

			@Override
			public boolean handleArguments(String[] args) {
				try {
					frontend.logout(sessionid);
					msg = null;
					System.out.println("MsgService deleted");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		};
		cmd.registerCommand(new CoreLogoutCommand());
		class LoginCommand extends LazyCommand {

			public LoginCommand() {
				super("login");
			}

			@Override
			public boolean handleArguments(String[] args) {
				if (msg == null) {
					System.out.println("do a coreLogin first");
				}
				try {
					if (msg.login()) {
						System.out.println("logged in");
					} else {
						System.out.println("login returned false");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		};
		cmd.registerCommand(new LoginCommand());
		class LogoutCommand extends LazyCommand {

			public LogoutCommand() {
				super("LogoutCommand");
			}

			@Override
			public boolean handleArguments(String[] args) {
				if (msg == null) {
					System.out.println("do a coreLogin first");
				}
				try {
					msg.logout();
					System.out.println("logged out");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		};
		cmd.registerCommand(new LogoutCommand());
	}
}
