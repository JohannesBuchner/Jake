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
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
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

	private String sessionId;

	private IFrontendService frontend;

	@SuppressWarnings("unchecked")
	private MsgService msg;

	public Project project;

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
			sessionId = frontend.authenticate(new HashMap<String, String>());
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
				super("createAccount", "createAccount <xmppid> <password>", "provides a MsgService");
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
					frontend.createAccount(sessionId, cred).setListener(this);
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
		class CoreLoginCommand extends LazyCommand {

			public CoreLoginCommand() {
				super("coreLogin", "coreLogin <xmppid> <password>", "provides a MsgService");
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
					msg = frontend.addAccount(sessionId, cred);
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
				super("coreLogout", "coreLogout", "removes the MsgService");
			}

			@Override
			public boolean handleArguments(String[] args) {
				try {
					frontend.logout(sessionId);
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
				super("login", "login", "needs a MsgService");
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
				super("LogoutCommand", "LogoutCommand", "needs a MsgService");
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
		
		class CreateProjectCommand extends LazyCommand {

			public CreateProjectCommand() {
				super("createProject", "createProject <Folder>",
						"needs a MsgService; provides a open project");
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
				try {
					System.out.println("creating project ...");
					project = frontend.getProjectsManagingService(sessionId).createProject(projectFolder.getName(), 
							projectFolder.getAbsolutePath(), msg);
					System.out.println("creating project done");
				} catch (Exception e) {
					System.out.println("creating project failed");
					e.printStackTrace();
				}
				return true;
			}
		};
		cmd.registerCommand(new CreateProjectCommand());
		
		class DeleteProjectCommand extends LazyCommand {

			public DeleteProjectCommand() {
				super("deleteProject", "deleteProject [andFiles]", 
						"needs a open project; optionally deletes files in folder");
			}

			@Override
			public boolean handleArguments(String[] args) {
				boolean deleteFiles;
				if (args.length == 1)
					deleteFiles = false;
				else if (args.length == 2 && "andFiles".equals(args[1]))
					deleteFiles = true;
				else
					return false;
				try {
					System.out.println("deleting project ...");
					frontend.getProjectsManagingService(sessionId).deleteProject(project, deleteFiles);
					System.out.println("deleting project done");
				} catch (Exception e) {
					System.out.println("deleting project failed");
					e.printStackTrace();
				}
				return true;
			}
		};
		cmd.registerCommand(new DeleteProjectCommand());
		class CloseProjectCommand extends LazyCommand {

			public CloseProjectCommand() {
				super("closeProject", "closeProject", "needs a open project");
			}

			@Override
			public boolean handleArguments(String[] args) {
				try {
					System.out.println("closing project ...");
					frontend.getProjectsManagingService(sessionId).closeProject(project);
					System.out.println("closing project done");
				} catch (Exception e) {
					System.out.println("listing projects failed");
					e.printStackTrace();
				}
				return true;
			}
		};
		cmd.registerCommand(new CloseProjectCommand());
		class ListProjectsCommand extends LazyCommand {

			public ListProjectsCommand() {
				super("listProjects");
			}

			@Override
			public boolean handleArguments(String[] args) {
				try {
					System.out.println("listing projects:");
					for(Project p : frontend.getProjectsManagingService(sessionId).getProjectList()){
						System.out.println(p.getName() + " - " + p.getRootPath());
					}
					System.out.println("listing projects done");
				} catch (Exception e) {
					System.out.println("listing projects failed");
					e.printStackTrace();
				}
				return true;
			}
		};
		cmd.registerCommand(new ListProjectsCommand());
		class OpenProjectCommand extends LazyCommand {

			public OpenProjectCommand() {
				super("openProject", "openProject", "provides a open project");
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
				try {
					System.out.println("opening project ...");
					for(Project p : frontend.getProjectsManagingService(sessionId).getProjectList()){
						if(p.getRootPath().equals(projectFolder))
							project = p;
					}
					frontend.getProjectsManagingService(sessionId).openProject(project);
					System.out.println("opening project done");
				} catch (Exception e) {
					System.out.println("opening project failed");
					project = null;
					e.printStackTrace();
				}
				return true;
			}
		};
		cmd.registerCommand(new OpenProjectCommand());
		class StatusCommand extends LazyCommand {

			public StatusCommand() {
				super("status", "status", "shows wether a msgservice/project/etc. is opened");
			}

			@Override
			public boolean handleArguments(String[] args) {
				System.out.println("Project available: " + (project != null));
				System.out.println("MsgService available: " + (msg != null));
				return true;
			}
		};
		cmd.registerCommand(new StatusCommand());
	}
}
