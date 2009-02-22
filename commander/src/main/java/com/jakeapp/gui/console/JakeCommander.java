package com.jakeapp.gui.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.gui.console.commandline.CmdManager;
import com.jakeapp.gui.console.commandline.Command;
import com.jakeapp.gui.console.commandline.LazyCommand;
import com.jakeapp.gui.console.commandline.StoppableCmdManager;

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

	private abstract class LazyJakeObjectCommand extends LazyCommand {

		public LazyJakeObjectCommand(String command, String help) {
			super(command, command + " <UUID>", "needs Project;" + help);
		}

		public LazyJakeObjectCommand(String command) {
			super(command, command + " <UUID>");
		}

		@Override
		final public boolean handleArguments(String[] args) {
			if (args.length != 2)
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
				for (Attributed f : sync.getNotes(project)) {
					if (uuid.equals(f.getJakeObject().getUuid()))
						jo = f.getJakeObject();
				}
				for (Attributed f : sync.getFiles(project)) {
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
			if (jo.getProject() == null)
				jo.setProject(project);
			handleArguments(jo);
			return true;
		}

		protected abstract void handleArguments(JakeObject jo);

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

	class CoreLogoutCommand extends LazyNoParamsCommand {

		public CoreLogoutCommand() {
			super("coreLogout", "removes the MsgService");
		}

		@Override
		public void handleArguments() {
			try {
				frontend.logout(sessionId);
				msg = null;
				System.out.println("MsgService deleted");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	class LoginCommand extends LazyNoParamsCommand {

		public LoginCommand() {
			super("login", "needs a MsgService");
		}

		@Override
		public void handleArguments() {
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
		}
	};

	class LogoutCommand extends LazyNoParamsCommand {

		public LogoutCommand() {
			super("logout", "needs a MsgService");
		}

		@Override
		public void handleArguments() {
			try {
				System.out.println("logging out ...");
				msg.logout();
				System.out.println("logging out done");
			} catch (Exception e) {
				System.out.println("logging out failed");
				e.printStackTrace();
			}
		}
	};

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
				pms.assignUserToProject(project, msg.getUserId());

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
				System.out.println("\t" + project);
				pms.openProject(project);
				// workaround for bug 32 + bug 33
				// TODO: remove after it these are fixed
				try {
					pms.assignUserToProject(project, msg.getUserId());
				} catch (IllegalAccessException e) {
					// we ignore that, just like the gui does
				}

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
			System.out.println("Project available: " + project);
			System.out.println("MsgService available: " + msg);
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
				for (Attributed f : sync.getNotes(project)) {
					System.out.println("\t" + f);
				}
				for (Attributed f : sync.getFiles(project)) {
					System.out.println("\t" + f);
				}
			} catch (FrontendNotLoggedInException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	class AnnounceCommand extends LazyJakeObjectCommand {

		public AnnounceCommand() {
			super("announce");
		}

		@Override
		public void handleArguments(JakeObject jo) {
			try {
				System.out.println("announcing ... ");
				sync.announce(jo, LogAction.JAKE_OBJECT_NEW_VERSION, "something new, something blue");
				System.out.println("announcing done");
			} catch (Exception e) {
				System.out.println("announcing failed");
				e.printStackTrace();
			}
		}
	};

	class DeleteCommand extends LazyJakeObjectCommand {

		public DeleteCommand() {
			super("delete");
		}

		@Override
		public void handleArguments(JakeObject jo) {
			try {
				System.out.println("deleting ... ");
				sync.announce(jo, LogAction.JAKE_OBJECT_DELETE, "something new, something red");
				System.out.println("deleting done");
			} catch (Exception e) {
				System.out.println("deleting failed");
				e.printStackTrace();
			}
		}
	};

	class LockCommand extends LazyJakeObjectCommand {

		public LockCommand() {
			super("lock");
		}

		@Override
		public void handleArguments(JakeObject jo) {
			try {
				System.out.println("locking ... ");
				sync.announce(jo, LogAction.JAKE_OBJECT_LOCK,
						"I'm working on this. Please wait for me to finish or contact me");
				System.out.println("locking done");
			} catch (Exception e) {
				System.out.println("locking failed");
				e.printStackTrace();
			}
		}
	};

	class UnLockCommand extends LazyJakeObjectCommand {

		public UnLockCommand() {
			super("unlock");
		}

		@Override
		public void handleArguments(JakeObject jo) {
			try {
				System.out.println("unlocking ... ");
				sync.announce(jo, LogAction.JAKE_OBJECT_UNLOCK, "I'm done working on this.");
				System.out.println("unlocking done");
			} catch (Exception e) {
				System.out.println("unlocking failed");
				e.printStackTrace();
			}
		}
	};

	class LogCommand extends LazyJakeObjectCommand {

		public LogCommand() {
			super("log", "show all logentries for this JakeObject");
		}

		@Override
		public void handleArguments(JakeObject jo) {
			try {
				System.out.println("listing log ... ");
				for (LogEntry<?> le : pms.getLog(jo)) {
					System.out.println("\t" + le);
				}
				System.out.println("listing log done");
			} catch (Exception e) {
				System.out.println("listing log failed");
				e.printStackTrace();
			}
		}
	};

	class JakeObjectStatusCommand extends LazyJakeObjectCommand {

		public JakeObjectStatusCommand() {
			super("objectStatus", "prints the synchronisation+lock status of a JakeObject");
		}

		@Override
		public void handleArguments(JakeObject jo) {
			try {
				System.out.println("getting JakeObject status ... ");
				Attributed status = sync.getJakeObjectSyncStatus(jo);
				System.out.println("\t" + jo);
				System.out.println("\t\t" + status);
				System.out.println("\t\t" + sync.getLock(jo));
				System.out.println("getting JakeObject status done");
			} catch (Exception e) {
				System.out.println("getting JakeObject status failed");
				e.printStackTrace();
			}
		}
	};

	class ModifyCommand extends LazyJakeObjectCommand {

		public ModifyCommand() {
			super("modify", "changes the content of the JakeObject");
		}

		@Override
		public void handleArguments(JakeObject jo) {
			try {
				System.out.println("modifying the JakeObject ... ");
				if (jo instanceof NoteObject) {
					NoteObject no = (NoteObject) jo;
					no.setContent(no.getContent() + "\n" + "more content ...");
					pms.saveNote(no);
				} else {
					FileWriter fw = new FileWriter(sync.getFile((FileObject) jo), true);
					fw.append('.');
					fw.close();
				}
				System.out.println("modifying the JakeObject done");
			} catch (Exception e) {
				System.out.println("modifying the JakeObject failed");
				e.printStackTrace();
			}
		}
	};

	class ProjectLogCommand extends LazyNoParamsCommand {

		public ProjectLogCommand() {
			super("projectLog", "show all logentries for the Projetc");
		}

		@Override
		public void handleArguments() {
			try {
				System.out.println("listing project log ... ");
				for (LogEntry<?> le : pms.getLog(project)) {
					System.out.println("\t" + le);
				}
				System.out.println("listing project log done");
			} catch (Exception e) {
				System.out.println("listing project log failed");
				e.printStackTrace();
			}
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
