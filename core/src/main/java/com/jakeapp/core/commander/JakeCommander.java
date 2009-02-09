package com.jakeapp.core.commander;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jakeapp.core.commander.commandline.CmdManager;
import com.jakeapp.core.commander.commandline.Command;
import com.jakeapp.core.commander.commandline.LazyCommand;
import com.jakeapp.core.commander.commandline.StoppableCmdManager;
import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.synchronization.JakeObjectSyncStatus;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;

/**
 * Test client accepting cli input
 */
public class JakeCommander {

	@SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(JakeCommander.class);

	private final CmdManager cmd = StoppableCmdManager.getInstance();

	private String sessionId;

	private IFrontendService frontend;

	private IProjectsManagingService pms;

	private IFriendlySyncService sync;

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
		frontend = (IFrontendService) applicationContext.getBean("frontendService");

		try {
			sessionId = frontend.authenticate(new HashMap<String, String>());
			pms = frontend.getProjectsManagingService(sessionId);
			sync = frontend.getSyncService(sessionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private abstract class LazyAvailabilityCommand<T> extends LazyCommand implements
			AvailabilityListener<T> {

		public LazyAvailabilityCommand(String command, String syntax, String help) {
			super(command, syntax, help);
		}

		public LazyAvailabilityCommand(String command, String syntax) {
			super(command, syntax);
		}

		public LazyAvailabilityCommand(String command) {
			super(command);
		}

	}

	private abstract class LazyProjectDirectoryCommand extends LazyCommand {

		public LazyProjectDirectoryCommand(String command, String help) {
			super(command, command + " <Folder>", help);
		}

		public LazyProjectDirectoryCommand(String command) {
			super(command, command + " <Folder>");
		}

		@Override
		final public boolean handleArguments(String[] args) {
			if (args.length != 2)
				return false;
			if (project != null)
				return false;
			File projectFolder = new File(args[1]);
			if (!(projectFolder.exists() && projectFolder.isDirectory())) {
				System.out.println("not a directory");
				return true;
			}
			handleArguments(projectFolder);
			return true;
		}

		protected abstract void handleArguments(File folder);

	}

	private abstract class LazyNoParamsCommand extends LazyCommand {

		public LazyNoParamsCommand(String command, String help) {
			super(command, command, help);
		}

		public LazyNoParamsCommand(String command) {
			super(command, command);
		}

		@Override
		final public boolean handleArguments(String[] args) {
			if (args.length != 1)
				return false;
			handleArguments();
			return true;
		}

		protected abstract void handleArguments();

	}

	class CreateAccountCommand extends LazyAvailabilityCommand<Void> {

		public CreateAccountCommand() {
			super("createAccount", "createAccount <xmppid> <password>", "Creates a new Account on a Jabber Server and stores it in the DB.");
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
//				System.out.println("got the MsgService");
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

	class AddAccountCommand extends LazyCommand {

		public AddAccountCommand() {
			super("addAccount", "addAccount <xmppid> <password>", "Adds an _existing_ jabber Account to Jakes database.");
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
				frontend.addAccount(sessionId, cred);
                System.out.println("Adding succeeded. Please login with: ");
                System.out.println("login "+ cred.getUserId() );
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	};




    class ListAccountsCommand extends LazyCommand
    {
         public ListAccountsCommand()
         {
             super("listAccounts", "listAccounts", "returns a list of available accounts to work with.");
         }


        @Override
        public boolean handleArguments(String[] args) {

            List<MsgService> services = frontend.getMsgServices(sessionId);
            if(services.size() > 0)
            {
                System.out.println("The following accounts currently exist: ");
                int x = 0;
                String userid;
                boolean saved;
                for(MsgService service : services)
                {
                    userid = service.getUserId().toString();
                    saved = service.isPasswordSaved();

                    System.out.printf("%d. %s [%s] \n",
                            x, userid,  ((saved)?"[Password SAVED]" : "[Password NOT SAVED]" )
                    );

                    x++;
                }                            
            }

            return true;
        }

    }

    class LoginCommand extends LazyCommand {

		public LoginCommand() {
			super("login", "login <xmppid> [<password> [savePassword 0/1] ", "Logs in to an account & provides a MsgService");
		}

		@Override
		public boolean handleArguments(String[] args) {
			if (args.length < 2)
				return false;

			String id;
            String password = "";
            boolean savePass = false;
            MsgService matching = null;

            id = args[1];
            if(args.length >= 3) {
                password = args[2];
            }

            if(args.length >= 4) {
                savePass = args[3].equals("1");
            }
            List<MsgService> services = frontend.getMsgServices(sessionId);

            for(MsgService service : services)
            {
                System.out.println("checking " + service.getUserId().getUserId());
                if(service.getUserId().getUserId().equals(id))
                {
                    matching = service;
                    break;
                }
            }

            if(matching == null)
            {
                System.out.println("No Account with that name found");
                return false;
            }

            ServiceCredentials cred;
            boolean success;
            if(!password.isEmpty())
            {
                cred = new ServiceCredentials(id, password);
                cred.setSavePassword(savePass);
                matching.setServiceCredentials(cred);
                matching.setServiceCredentials(cred);

                try {
                    System.out.println("Found an account and try to login with provided credentials");
                    success = matching.login();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            else
            {
                try
                {
                    if(!matching.isPasswordSaved())
                    {
                        System.out.println("No password saved for that account an none given");
                        return false;
                    }
                    System.out.println("Found an account and try to login with saved credentials. ");
                    success = matching.login();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }


            msg = matching;
            if(success)
            {
                System.out.println("Successfully logged in on the message service. " +
                        "You can now use all activities available when being online");
            }
            else
            {
                System.out.println("Failed to login on the message service. " +
                        "We stored the MsgService so you can create projects, but you will not be able to " +
                        "use online features");
            }
            return true;


		}
	};
    


	class LogoutCommand extends LazyNoParamsCommand {

		public LogoutCommand() {
			super("logout", "Logout from the MsgService & removes it.");
		}

		@Override
		public void handleArguments() {
			try {
                if(msg != null)
                    msg.logout();

                msg = null;

				frontend.logout(sessionId);

				System.out.println("MsgService logged out and deleted;");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};


    class CoreAttachCommand extends LazyNoParamsCommand {

		public CoreAttachCommand() {
			super("coreAttach", "Attaches to the core. (Instantiate and authenticate)");
		}

		@Override
		public void handleArguments() {
			try {
                startupCore();
				System.out.println("Core started up.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};


    class CoreDetachCommand extends LazyNoParamsCommand {

		public CoreDetachCommand() {
			super("coreDetach", "Remvoes the MsgService and detaches from the core (destroy session).");
		}

		@Override
		public void handleArguments() {
			try {
                msg = null;
				frontend.logout(sessionId);
				System.out.println("MsgService deleted; Detached from core.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};


// please don't do it that way   
//	class LoginCommand extends LazyNoParamsCommand {
//
//		public LoginCommand() {
//			super("login", "needs a MsgService");
//		}
//
//		@Override
//		public void handleArguments() {
//			if (msg == null) {
//				System.out.println("do a coreLogin first");
//			}
//			try {
//				if (msg.login()) {
//					System.out.println("logged in");
//				} else {
//					System.out.println("login returned false");
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	};

//	class LogoutCommand extends LazyNoParamsCommand {
//
//		public LogoutCommand() {
//			super("LogoutCommand", "needs a MsgService");
//		}
//
//		@Override
//		public void handleArguments() {
//			try {
//				System.out.println("logging out ...");
//				msg.logout();
//				System.out.println("logging out done");
//			} catch (Exception e) {
//				System.out.println("logging out failed");
//				e.printStackTrace();
//			}
//		}
//	};

	class CreateProjectCommand extends LazyProjectDirectoryCommand {

		public CreateProjectCommand() {
			super("createProject", "needs a MsgService; provides a open project");
		}

		@Override
		public void handleArguments(File projectFolder) {
			if (msg == null) {
				System.out.println("needs a MsgService!");
				return;
			}
			try {
				System.out.println("creating project ...");
				project = pms.createProject(projectFolder.getName(), projectFolder
						.getAbsolutePath(), msg);

// this is not required, when creating a project with
// an assigned msgService. The method "assignUserToProject 
// should only be used when the project is created without a msgService!

//				pms.assignUserToProject(project, msg.getUserId());


				System.out.println("creating project done");
			} catch (Exception e) {
				System.out.println("creating project failed");
				e.printStackTrace();
			}
		}
	};

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
				pms.deleteProject(project, deleteFiles);
				System.out.println("deleting project done");
			} catch (Exception e) {
				System.out.println("deleting project failed");
				e.printStackTrace();
			}
			return true;
		}
	};

	class CloseProjectCommand extends LazyNoParamsCommand {

		public CloseProjectCommand() {
			super("closeProject", "needs a open project");
		}

		@Override
		public void handleArguments() {
			try {
				System.out.println("closing project ...");
				pms.closeProject(project);
				System.out.println("closing project done");
			} catch (Exception e) {
				System.out.println("listing projects failed");
				e.printStackTrace();
			}
		}
	};

	class ListProjectsCommand extends LazyNoParamsCommand {

		public ListProjectsCommand() {
			super("listProjects");
		}

		@Override
		public void handleArguments() {
			try {
				System.out.println("listing projects:");
				for (Project p : pms.getProjectList()) {
					System.out.println("\t" + p);
				}
				System.out.println("listing projects done");
			} catch (Exception e) {
				System.out.println("listing projects failed");
				e.printStackTrace();
			}
		}
	};

	class OpenProjectCommand extends LazyProjectDirectoryCommand {

		public OpenProjectCommand() {
			super("openProject", "provides a open project");
		}

		@Override
		public void handleArguments(File projectFolder) {
			try {
				System.out.println("opening project ...");
				for (Project p : pms.getProjectList()) {
					if (new File(p.getRootPath()).equals(projectFolder))
						project = p;
				}
				if (project == null) {
					System.out.println("no such project");
					return;
				}
				pms.openProject(project);
				System.out.println("opening project done");
			} catch (Exception e) {
				System.out.println("opening project failed");
				project = null;
				e.printStackTrace();
			}
		}
	};

	class StatusCommand extends LazyNoParamsCommand {

		public StatusCommand() {
			super("status", "shows whether a msgservice/project/etc. is opened");
		}

		@Override
		public void handleArguments() {
			System.out.println("Project available: " + (project != null));
			System.out.println("MsgService available: " + (msg != null));
		}
	};

	class InviteCommand extends LazyCommand {

		public InviteCommand() {
			super("invite", "invite <xmppid>", "needs MsgService; needs Project");
		}

		@Override
		public boolean handleArguments(String[] args) {
			if (args.length != 2)
				return false;
			try {
				System.out.println("inviting ...");
				pms.invite(project, args[1]);
				System.out.println("inviting done");
			} catch (Exception e) {
				System.out.println("inviting failed");
				e.printStackTrace();
			}
			return true;
		}
	};

	class AcceptInviteCommand extends LazyProjectDirectoryCommand {

		public AcceptInviteCommand() {
			super("acceptInvite", "needs MsgService; accepts first invited project");
		}

		@Override
		public void handleArguments(File projectFolder) {
			for (Project p : pms.getProjectList(InvitationState.INVITED)) {
				project = p;
				break;
			}
			if (project == null) {
				System.out.println("no projects where we are invited found.");
				return;
			}
			try {
				System.out.println("joining ...");
				pms.joinProject(project, null);
				System.out.println("joining done");
			} catch (Exception e) {
				System.out.println("joining failed");
				e.printStackTrace();
			}
		}
	};

	class RejectInviteCommand extends LazyNoParamsCommand {

		public RejectInviteCommand() {
			super("rejectInvite", "needs MsgService; reject first invited project");
		}

		@Override
		public void handleArguments() {
			for (Project p : pms.getProjectList(InvitationState.INVITED)) {
				project = p;
				break;
			}
			if (project == null) {
				System.out.println("no projects where we are invited found.");
				return;
			}
			try {
				System.out.println("joining ...");
				pms.joinProject(project, null);
				System.out.println("joining done");
			} catch (Exception e) {
				System.out.println("joining failed");
				e.printStackTrace();
			}
		}
	};

	class ListObjectsCommand extends LazyNoParamsCommand {

		public ListObjectsCommand() {
			super("listObjects", "needs Project");
		}

		@Override
		public void handleArguments() {
			if (project == null) {
				System.out.println("no project");
				return;
			}
			try {
				for (JakeObjectSyncStatus f : sync.getNotes(project)) {
					System.out.println("\t" + f);
				}
				for (JakeObjectSyncStatus f : sync.getFiles(project)) {
					System.out.println("\t" + f);
				}
			} catch (FrontendNotLoggedInException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	class AnnounceCommand extends LazyCommand {

		public AnnounceCommand() {
			super("announce", "announce <UUID>", "needs Project");
		}

		@Override
		public boolean handleArguments(String[] args) {
			if (args.length != 1)
				return false;
			UUID uuid;
			try {
				uuid = UUID.fromString(args[1]);
			} catch (IllegalArgumentException e) {
				return false;
			}
			if (project == null) {
				System.out.println("no project");
				return true;
			}
			JakeObject jo = null;
			try {
				for (JakeObjectSyncStatus f : sync.getNotes(project)) {
					System.out.println("\t" + f);
					if (uuid.equals(f.getJakeObject().getUuid()))
						jo = f.getJakeObject();
				}
				for (JakeObjectSyncStatus f : sync.getFiles(project)) {
					System.out.println("\t" + f);
					if (uuid.equals(f.getJakeObject().getUuid()))
						jo = f.getJakeObject();
				}
			} catch (FrontendNotLoggedInException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (jo == null) {
				System.out.println("JakeObject not found");
				return true;
			}

			try {
				System.out.println("announcing ... ");
				sync.announce(jo, new LogEntry<JakeObject>(null, LogAction.JAKE_OBJECT_NEW_VERSION,
						new Date(), project, jo), "something new, something blue");
				System.out.println("announcing done");
			} catch (Exception e) {
				System.out.println("announcing failed");
				e.printStackTrace();
				return true;
			}

			return true;
		}
	};

	private void addCommands() {
		// we are so cool, we use reflection
		for (Class<?> c : JakeCommander.class.getDeclaredClasses()) {
			Command command;
			try {
				Constructor<Command> constructor = (Constructor<Command>) c.getConstructor(this
						.getClass());
				command = constructor.newInstance(this);
			} catch (Exception e) {
				continue;
			}
			this.cmd.registerCommand(command);
		}
	}
}
