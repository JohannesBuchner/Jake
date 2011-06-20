package com.jakeapp.gui.console;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.gui.console.commandline.LazyCommand;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.violet.actions.global.CreateAccountAction;
import com.jakeapp.violet.actions.global.CreateDeleteProjectAction;
import com.jakeapp.violet.actions.global.GetProjectsAction;
import com.jakeapp.violet.actions.global.GoOnlineAction;
import com.jakeapp.violet.actions.global.LoginView;
import com.jakeapp.violet.actions.global.StartProjectAction;
import com.jakeapp.violet.actions.global.StopProjectAction;
import com.jakeapp.violet.actions.global.SuggestUsersToInviteAction;
import com.jakeapp.violet.actions.project.InviteUserAction;
import com.jakeapp.violet.actions.project.UserInfo;
import com.jakeapp.violet.actions.project.interact.AnnounceAction;
import com.jakeapp.violet.actions.project.interact.PokeAction;
import com.jakeapp.violet.actions.project.interact.SimpleUserOrderStrategy;
import com.jakeapp.violet.actions.project.interact.UserOrderStrategy;
import com.jakeapp.violet.actions.project.interact.pull.DownloadAction;
import com.jakeapp.violet.actions.project.interact.pull.PullAction;
import com.jakeapp.violet.actions.project.local.FileInfoAction;
import com.jakeapp.violet.actions.project.local.GetAllLogEntriesAction;
import com.jakeapp.violet.actions.project.local.GetLogEntriesAction;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.gui.JsonPasswords;
import com.jakeapp.violet.gui.JsonProjects;
import com.jakeapp.violet.gui.Passwords;
import com.jakeapp.violet.gui.Projects;
import com.jakeapp.violet.model.Context;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.model.attributes.Attributed;

/**
 * Test client accepting cli input
 */
public class JakeCommander extends Commander {

	private Context project;

	private ProjectDir projectDir;

	private User user;

	private String inviteProjectName;

	private UUID inviteProjectId;

	private User inviteProjectUser;

	private UserOrderStrategy userorder = new SimpleUserOrderStrategy();

	private Passwords wallet = new JsonPasswords(new File("jake.passwords"));

	private LoginView loginView = new LoginView() {

		@Override
		public void invited(User inviter, String name, UUID id) {
			inviteProjectName = name;
			inviteProjectId = id;
			inviteProjectUser = user;
		}

		@Override
		public void onlineStatusChanged(UserId userid) {
			System.out.println("online status of " + userid.getUserId()
					+ " changed");
		}

		@Override
		public void connectionStateChanged(ConnectionState le, Exception ex) {
			System.out.println("connection state changed to " + le);
			if (ex != null)
				ex.printStackTrace();
		}
	};

	public static final String jakeName = "Jake";

	@Override
	protected void onShutdown() {
		//
	}

	public static final String namespace = "http://jakeapp.com/protocols/xmpp/versions/2";

	@Override
	protected void onStartup() {
		DI.register(Projects.class, new JsonProjects(new File("jake.projects")));
		DI.register(ICService.class, new XmppICService(namespace, jakeName ));
	}


	public JakeCommander(String[] args) {
		super(args);
	}

	public JakeCommander(InputStream in) {
		super.run(in);
	}

	public JakeCommander(InputStream in, boolean startWithHelp) {
		super.run(in, startWithHelp);
	}

	public static void main(String[] args) {
		new JakeCommander(args);
	}

	@SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(JakeCommander.class);


	private abstract class LazyUserCommand extends LazyCommand {

		public LazyUserCommand(String command) {
			super(command, command + " <UserID>");
		}

		public LazyUserCommand(String command, String help) {
			super(command, command + " <UserID>", help);
		}

		public boolean handleArguments(String[] args) {
			if (args.length != 2)
				return false;
			if (project == null)
				return false;

			handleArguments(new User(args[1]));

			return true;
		}

		public abstract void handleArguments(User user);
	}

	private abstract class LazyJakeObjectCommand extends LazyCommand {

		public LazyJakeObjectCommand(String command) {
			this(command, "");
		}

		public LazyJakeObjectCommand(String command, String help) {
			super(command, command + " <file>", help);
		}

		public boolean handleArguments(String[] args) {
			if (args.length != 2)
				return false;
			if (project == null)
				return false;

			handleArguments(new JakeObject(args[1]));

			return true;
		}

		protected abstract void handleArguments(JakeObject jo);
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
			if (project != null) {
				System.out
						.println("this command doesn't work with a project set");
				return true;
			}
			ProjectDir projectFolder = new ProjectDir(args[1]);
			handleArguments(projectFolder);
			return true;
		}

		protected abstract void handleArguments(ProjectDir folder);

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
			if (project != null) {
				System.out
						.println("this command doesn't work with a project set");
				return true;
			}
			handleArguments();
			return true;
		}

		protected abstract void handleArguments();

	}


	private abstract class LazyProjectDirectoryCommandThatDoesNotNeedProject
			extends LazyCommand {

		public LazyProjectDirectoryCommandThatDoesNotNeedProject(
				String command, String help) {
			super(command, command + " <Folder>", help);
		}

		public LazyProjectDirectoryCommandThatDoesNotNeedProject(String command) {
			super(command, command + " <Folder>");
		}

		@Override
		final public boolean handleArguments(String[] args) {
			if (args.length != 2)
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

	class SetLoginCommand extends LazyCommand {

		public SetLoginCommand() {
			super("setLogin", "setLogin <xmppid> <password>",
					"provides a MsgService");
		}

		@Override
		public boolean handleArguments(String[] args) {
			if (args.length != 3)
				return false;

			String id = args[1];
			String password = args[2];

			try {
				wallet.storeForUser(id, password);
				user = new User(id);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	// createProject deleteProject getProjects goOnline startProject stopProject
	// suggestInvite

	class CreateAccountCommand extends LazyCommand {

		public CreateAccountCommand() {
			super("createAccount", "createAccount <xmppid>",
					"provides a MsgService");
		}

		@Override
		public boolean handleArguments(String[] args) {
			if (args.length != 2)
				return false;
			String id = args[1];
			String password = wallet.loadForUser(id);
			try {
				AvailableLaterWaiter.await(new CreateAccountAction(
						new User(id), password));
				user = new User(id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	class CreateProjectCommand extends LazyProjectDirectoryCommand {

		public CreateProjectCommand() {
			super("createProject",
					"needs a MsgService; provides a open project");
		}

		@Override
		public void handleArguments(ProjectDir projectFolder) {
			try {
				AvailableLaterWaiter.await(new CreateDeleteProjectAction(
						projectFolder, false));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class DeleteProjectCommand extends LazyProjectDirectoryCommand {

		public DeleteProjectCommand() {
			super("deleteProject", "deleteProject");
		}

		@Override
		public void handleArguments(ProjectDir projectFolder) {
			try {
				System.out.println("deleting project ...");
				AvailableLaterWaiter.await(new CreateDeleteProjectAction(
						projectFolder, false));
				System.out.println("deleting project done");
			} catch (Exception e) {
				System.out.println("deleting project failed");
				e.printStackTrace();
			}
		}
	}

	class CloseProjectCommand extends LazyNoParamsCommand {

		public CloseProjectCommand() {
			super("closeProject", "needs a open project");
		}

		@Override
		public void handleArguments() {
			try {
				System.out.println("closing project ...");
				AvailableLaterWaiter.await(new StopProjectAction(project
						.getModel()));
				project = null;
				System.out.println("closing project done");
			} catch (Exception e) {
				System.out.println("closing project failed");
				e.printStackTrace();
			}
		}
	}

	class OpenProjectCommand extends LazyNoParamsCommand {

		public OpenProjectCommand() {
			super("openProject", "provides a open project");
		}

		@Override
		protected void handleArguments() {
			try {
				System.out.println("opening project");
				project = AvailableLaterWaiter.await(new StartProjectAction(
						projectDir));
				System.out.println("opening project done");
			} catch (Exception e) {
				System.out.println("opening project failed");
				e.printStackTrace();
			}
		}
	}

	class GoOnlineCommand extends LazyNoParamsCommand {

		public GoOnlineCommand() {
			super("goOnline", "goes online");
		}

		@Override
		protected void handleArguments() {
			String pw = wallet.loadForUser(user.getUserId());
			try {
				AvailableLaterWaiter.await(new GoOnlineAction(user, pw, -1,
						false, loginView));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class GoOfflineCommand extends LazyNoParamsCommand {

		public GoOfflineCommand() {
			super("goOffline", "goes offline");
		}

		@Override
		protected void handleArguments() {
			try {
				AvailableLaterWaiter.await(new GoOnlineAction(user, null, -1,
						true, loginView));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class StartProjectCommand extends LazyNoParamsCommand {

		public StartProjectCommand() {
			super("startProject", "goes online with the project");
		}

		@Override
		protected void handleArguments() {
			try {
				System.out.println("opening project");
				project = AvailableLaterWaiter.await(new StartProjectAction(
						projectDir));
				System.out.println("opening project done");
			} catch (Exception e) {
				System.out.println("opening project failed");
				e.printStackTrace();
			}
		}
	}

	class StopProjectCommand extends LazyNoParamsCommand {

		public StopProjectCommand() {
			super("stopProject", "goes offline with the project");
		}

		@Override
		protected void handleArguments() {
			try {
				System.out.println("opening project");
				AvailableLaterWaiter.await(new StopProjectAction(project
						.getModel()));
				System.out.println("opening project done");
			} catch (Exception e) {
				System.out.println("opening project failed");
				e.printStackTrace();
			}
		}
	}

	class ListProjectsCommand extends LazyNoParamsCommand {

		public ListProjectsCommand() {
			super("listProjects");
		}

		@Override
		public void handleArguments() {
			try {
				System.out.println("listing projects:");
				Collection<ProjectDir> list = AvailableLaterWaiter
						.await(new GetProjectsAction());

				for (ProjectDir p : list) {
					System.out.println("\t" + p);
				}
				System.out.println("listing projects done");
			} catch (Exception e) {
				System.out.println("listing projects failed");
				e.printStackTrace();
			}
		}
	}

	class SelectFirstProjectCommand extends LazyNoParamsCommand {

		public SelectFirstProjectCommand() {
			super(
					"selectFirstProject",
					"Selects (\"opens\") the first project that exists"
							+ " locally - sort of like opening, but not really opening. Get it?");
		}

		@Override
		public void handleArguments() {
			try {
				Collection<ProjectDir> list = AvailableLaterWaiter
						.await(new GetProjectsAction());

				for (ProjectDir p : list) {
					projectDir = p;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}

			if (project == null) {
				System.err.println("No project found!");
				return;
			}
			System.out.println("Project " + projectDir + " is now selected!");
		}
	}

	class UnSelectProjectCommand extends LazyNoParamsCommand {

		public UnSelectProjectCommand() {
			super("unselectProject",
					"Unselects (\"closes\") the current project");
		}

		@Override
		public void handleArguments() {
			projectDir = null;
			System.out.println("Project is now null!");
		}
	}

	class StatusCommand extends LazyNoParamsCommand {

		public StatusCommand() {
			super("status", "shows which project/etc. is opened");
		}

		@Override
		public void handleArguments() {
			System.out.println("Project available: " + project);
		}
	}

	class WaitCommand extends LazyNoParamsCommand {

		public WaitCommand() {
			super("wait", "waits for stdin input");
		}

		@Override
		public void handleArguments() {
			try {
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class ListInvitableCommand extends LazyNoParamsCommand {

		public ListInvitableCommand() {
			super("listInvitable");
		}

		@Override
		public void handleArguments() {
			try {
				System.out.println("listing ...");
				Collection<UserInfo> users = AvailableLaterWaiter
						.await(new SuggestUsersToInviteAction(user));
				for (UserInfo p : users) {
					System.out.println("\t" + p.getUserid() + " - "
							+ p.getFirstName() + " " + p.getLastName() + " ("
							+ p.getNickName() + ")");
				}
				System.out.println("listing done");
			} catch (Exception e) {
				System.out.println("listing failed");
				e.printStackTrace();
			}
		}
	}

	class InviteCommand extends LazyCommand {

		public InviteCommand() {
			super("invite", "invite <xmppid>",
					"needs MsgService; needs Project");
		}

		@Override
		public boolean handleArguments(String[] args) {
			if (args.length != 2)
				return false;
			try {
				if (AvailableLaterWaiter.await(new InviteUserAction(project
						.getModel())))
					System.out.println("invitation sent");
				else
					System.out.println("invitation couldn't be sent");

			} catch (Exception e) {
				System.out.println("inviting failed");
				e.printStackTrace();
			}
			return true;
		}
	}

	class AnnounceCommand extends LazyJakeObjectCommand {

		public AnnounceCommand() {
			super("announce");
		}

		@Override
		public void handleArguments(JakeObject jo) {
			try {
				System.out.println("announcing ... ");
				AvailableLaterWaiter
						.await(new AnnounceAction(project.getModel(), jo,
								"something new, something blue", false));
				System.out.println("announcing done");
			} catch (Exception e) {
				System.out.println("announcing failed");
				e.printStackTrace();
			}
		}
	}

	class DeleteCommand extends LazyJakeObjectCommand {

		public DeleteCommand() {
			super("delete");
		}

		@Override
		public void handleArguments(JakeObject jo) {
			try {
				System.out.println("deleting ... ");
				AvailableLaterWaiter.await(new AnnounceAction(project
						.getModel(), jo, "something new, something red", true));
				System.out.println("deleting done");
			} catch (Exception e) {
				System.out.println("deleting failed");
				e.printStackTrace();
			}
		}
	}

	class PokeCommand extends LazyUserCommand {

		public PokeCommand() {
			super(
					"poke",
					"Poke another user (inform them that it would be a good time to fetch our logs). "
							+ "Needs an open project.");
		}

		@Override
		public void handleArguments(User other) {
			try {
				System.out.println("Poking " + other + "...");
				AvailableLaterWaiter.await(new PokeAction(project.getModel(),
						other));
				System.out.println("poking done");
			} catch (Exception e) {
				System.out.println("poking failed");
				e.printStackTrace();
			}
		}
	}

	class LogCommand extends LazyJakeObjectCommand {

		public LogCommand() {
			super("log", "show all logentries for this JakeObject");
		}

		@Override
		public void handleArguments(JakeObject jo) {
			try {
				System.out.println("listing log ... ");
				Collection<LogEntry> list = AvailableLaterWaiter
						.await(new GetLogEntriesAction(project.getModel(), jo));
				for (LogEntry le : list) {
					System.out.println("\t" + le);
				}
				System.out.println("listing log done");
			} catch (Exception e) {
				System.out.println("listing log failed");
				e.printStackTrace();
			}
		}
	}

	class JakeObjectStatusCommand extends LazyJakeObjectCommand {

		public JakeObjectStatusCommand() {
			super("objectStatus",
					"prints the synchronisation+lock status of a JakeObject");
		}

		@Override
		public void handleArguments(JakeObject jo) {
			try {
				System.out.println("getting JakeObject status ... ");
				Attributed status = AvailableLaterWaiter.await(
						new FileInfoAction(project.getModel(), Arrays
								.asList(jo))).get(0);
				System.out.println("\t" + jo);
				System.out.println("\t\t" + status);
				System.out.println("getting JakeObject status done");
			} catch (Exception e) {
				System.out.println("getting JakeObject status failed");
				e.printStackTrace();
			}
		}
	}

	class ModifyCommand extends LazyJakeObjectCommand {

		public ModifyCommand() {
			super("modify", "changes the content of the JakeObject");
		}

		@Override
		public void handleArguments(JakeObject jo) {
			try {
				System.out.println("modifying the JakeObject ... ");
				FileWriter fw = new FileWriter(project.getModel().getFss()
						.getFullpath(jo.getRelPath()), true);
				fw.append('.');
				fw.close();
				System.out.println("modifying the JakeObject done");
			} catch (Exception e) {
				System.out.println("modifying the JakeObject failed");
				e.printStackTrace();
			}
		}
	}

	class ProjectLogCommand extends LazyNoParamsCommand {

		public ProjectLogCommand() {
			super("projectLog", "show all logentries for the Projetc");
		}

		@Override
		public void handleArguments() {
			try {
				System.out.println("listing project log ... ");
				Collection<LogEntry> list = AvailableLaterWaiter
						.await(new GetAllLogEntriesAction(project.getModel()));
				for (LogEntry le : list) {
					System.out.println("\t" + le);
				}
				System.out.println("listing project log done");
			} catch (Exception e) {
				System.out.println("listing project log failed");
				e.printStackTrace();
			}
		}
	}

	class PullCommand extends LazyJakeObjectCommand {

		public PullCommand() {
			super("pull", "Pulls a JakeObject");
		}

		@Override
		protected void handleArguments(JakeObject jo) {
			System.out.println("Starting to pull...");
			try {
				AvailableLaterWaiter.await(new PullAction(project.getModel(),
						jo, userorder));
				System.out.println("pull succeeded...");
			} catch (Exception e) {
				System.out.println("pull failed!");
				e.printStackTrace();
			}
		}
	}


	class DownloadCommand extends LazyJakeObjectCommand {

		public DownloadCommand() {
			super("download",
					"Downloads a JakeObject without storing it in the project folder");
		}

		@Override
		protected void handleArguments(JakeObject jo) {
			System.out.println("Starting to download...");
			try {
				File f = AvailableLaterWaiter.await(new DownloadAction(project
						.getModel(), jo, userorder));
				System.out.println("downloaded succeeded... file provided at "
						+ f.getAbsolutePath());
			} catch (Exception e) {
				System.out.println("download failed!");
				e.printStackTrace();
			}
		}
	}

}
