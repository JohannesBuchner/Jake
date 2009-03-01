package com.jakeapp.gui.console;

import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.core.services.IProjectInvitationListener;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.util.AvailableLaterWaiter;
import com.jakeapp.core.util.SpringThreadBroker;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.console.commandline.LazyCommand;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Test client accepting cli input
 */
public class JakeCommander extends Commander {

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

	@Override
	protected void onShutdown() {
		SpringThreadBroker.stopInstance();
	}

	@Override
	protected void onStartup() {
		startupCore();
	}

	private String sessionId;

	private IFrontendService frontend;

	private IProjectsManagingService pms;

	private IFriendlySyncService sync;

	@SuppressWarnings("unchecked")
	private MsgService msg;

	public Project project;
	public UserId invitingUser;

	private void startupCore() {
		SpringThreadBroker.getInstance().loadSpring(
				new String[] { "/com/jakeapp/core/applicationContext.xml" });
		frontend = (IFrontendService) SpringThreadBroker.getInstance().getBean(
				"frontendService");

		try {
			sessionId = frontend.authenticate(new HashMap<String, String>(), new PrintingChangeListener());
			pms = frontend.getProjectsManagingService(sessionId);
			sync = frontend.getSyncService(sessionId);
			
			pms.setInvitationListener(new IProjectInvitationListener(){

				@Override
				public void invited(UserId user, Project p) {
					System.out.println("got invitation from " + user + " to " + p);
					JakeCommander.this.project = p;
					JakeCommander.this.invitingUser = user;
				}

				@Override
				public void accepted(UserId user, Project p) {
					System.out.println("got accept from " + user + " to " + p);
				}

				@Override
				public void rejected(UserId user, Project p) {
					System.out.println("got reject from " + user + " to " + p);
				}
				
			});
			
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

	private abstract class LazyOtherUserCommand extends LazyCommand {
		public LazyOtherUserCommand(String command) {
			super(command, command + " <UserID>");
		}

		public LazyOtherUserCommand(String command, String help) {
			super(command, command + " <UserID>", help);
		}

		public boolean handleArguments(String[] args) {
			if(args.length != 2) return false;
			if(project == null) return false;

			handleArguments(args[1]);

			return true;
		}

		public abstract void handleArguments(String userid);
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
				System.out.println("this command doesn't work with a project set");
				return true;
			}
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

	private abstract class LazyProjectDirectoryCommandThatDoesNotNeedProject extends LazyCommand {

		public LazyProjectDirectoryCommandThatDoesNotNeedProject(String command, String help) {
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

	private abstract class LazyJakeObjectCommand extends LazyCommand {

		public LazyJakeObjectCommand(String command, String help) {
			super(command, command + " <UUID>", "needs Project;" + help);
		}

		public LazyJakeObjectCommand(String command) {
			super(command, command + " <UUID>");
		}

		protected class GetFileListener implements AvailabilityListener<List<FileObject>> {
			JakeObject jo;
			UUID uuid;
			
			public GetFileListener(JakeObject jo,UUID uuid) {
				super();
				this.jo = jo;
				this.uuid = uuid;
			}
			
			@Override
			public void error(Exception t) {
				t.printStackTrace();
			}

			@Override
			public void finished(List<FileObject> o) {
				for (FileObject f : o)
					if (uuid.equals(f.getUuid()))
						jo = f;
				
				if (jo == null)
					System.out.println("JakeObject not found");
				else {
					if (jo.getProject() == null)
						jo.setProject(project);
					handleArguments(jo);
				}
			}

			@Override
			public void statusUpdate(double progress, String status) {
				//empty implementation
			}
		}
		
		@Override
		final public boolean handleArguments(String[] args) {
			AvailableLaterObject<List<FileObject>> avail;
			
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
				for (NoteObject f : sync.getNotes(project)) {
					if (uuid.equals(f.getUuid()))
						jo = f;
				}
				
				avail = sync.getFiles(project);
				avail.setListener(new GetFileListener(jo,uuid));
				avail.start();
			} catch (FrontendNotLoggedInException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
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
			super("createAccount", "createAccount <xmppid> <password>",
					"provides a MsgService");
		}

		@Override
		public boolean handleArguments(String[] args) {
			if (args.length != 3)
				return false;
			String id = args[1];
			String password = args[2];
			ServiceCredentials cred = new ServiceCredentials(id, password,
					ProtocolType.XMPP);
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

	}

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
			ServiceCredentials cred = new ServiceCredentials(id, password,
					ProtocolType.XMPP);
			try {
				msg = frontend.addAccount(sessionId, cred);
				System.out.println("got the MsgService");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	class CoreLogoutCommand extends LazyNoParamsCommand {

		public CoreLogoutCommand() {
			super("coreLogout", "removes the MsgService");
		}

		@Override
		public void handleArguments() {
			try {
				frontend.logout(sessionId);
				msg = null;
				System.out.println("MsgService deleted. use stop now");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

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
				System.out.println("logging in ...");
				Boolean result = AvailableLaterWaiter.await(frontend.login(sessionId, msg));
				if (result) {
					System.out.println("logged in");
				} else {
					System.out.println("login returned false");
				}
				System.out.println("logging in done");
			} catch (Exception e) {
				System.out.println("logging in failed");
				e.printStackTrace();
			}
		}
	}

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
	}

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

				System.out.println("creating project done");
			} catch (Exception e) {
				System.out.println("creating project failed");
				e.printStackTrace();
			}
		}
	}

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
	}

	class CloseProjectCommand extends LazyNoParamsCommand {

		public CloseProjectCommand() {
			super("closeProject", "needs a open project");
		}

		@Override
		public void handleArguments() {
			try {
				System.out.println("closing project ...");
				pms.closeProject(project);
				project = null;
				System.out.println("closing project done");
			} catch (Exception e) {
				System.out.println("listing projects failed");
				e.printStackTrace();
			}
		}
	}

	class OpenProjectCommand extends LazyProjectDirectoryCommand {

		public OpenProjectCommand() {
			super("openProject", "provides a open project");
		}

		@Override
		protected void handleArguments(File folder) {
			try {
				System.out.println("opening project");
				project = new Project(folder.getName(), UUID.randomUUID(), msg, folder);
				pms.openProject(project);
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
				for (Project p : pms.getProjectList()) {
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
			super("selectFirstProject", "Selects (\"opens\") the first project that exists" +
					" locally - sort of like opening, but not really opening. Get it?");
		}

		@Override
		public void handleArguments() {
			try {
				for (Project p : pms.getProjectList()) {
					project = p;
					break;
				}
			} catch (Exception e) {
				System.err.println("EPIC FAIL trying to load project:");
				e.printStackTrace(System.err);
			}

			if(project == null) {
				System.err.println("THE PROJECT! IT DOES NOT EXIST! THE HUMANITY!");
				return;
			}

			System.out.println("Project " + project.getProjectId() + " is now selected!");
		}
	}

	class UnSelectProjectCommand extends LazyNoParamsCommand {
		public UnSelectProjectCommand() {
			super("unselectProject", "Unselects (\"closes\") the current project");
		}

		@Override
		public void handleArguments() {
			project = null;

			System.out.println("Project is now null!");
		}
	}

	class StartProjectCommand extends LazyNoParamsCommand {

		public StartProjectCommand() {
			super("startProject", "provides an started project");
		}

		@Override
		public void handleArguments() {
			try {
				System.out.println("starting project ...");
				if (project == null) {
					System.out.println("no project");
					return;
				}

				System.out.println("\t" + project);
				pms.startProject(project);
				System.out.println("starting project done");
			} catch (Exception e) {
				System.out.println("starting project failed");
				project = null;
				e.printStackTrace();
			}
		}
	}

	class StatusCommand extends LazyNoParamsCommand {

		public StatusCommand() {
			super("status", "shows whether a msgservice/project/etc. is opened");
		}

		@Override
		public void handleArguments() {
			System.out.println("Project available: " + project);
			System.out.println("MsgService available: " + msg);
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
				for(UserId p : pms.getSuggestedPeopleForInvite(project)) {
					System.out.println("\t" + p);
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
	}

	class AcceptInviteCommand extends LazyProjectDirectoryCommandThatDoesNotNeedProject {

		public AcceptInviteCommand() {
			super("acceptInvite", "needs MsgService; accepts first invited project");
		}

		@Override
		public void handleArguments(File projectFolder) {
			// TODO: this doesn't loop - bug?
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
				pms.joinProject(project, invitingUser);
				System.out.println("joining done");
			} catch (Exception e) {
				System.out.println("joining failed");
				e.printStackTrace();
			}
		}
	}

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
	}

	class ListObjectsCommand extends LazyNoParamsCommand {

		public ListObjectsCommand() {
			super("listObjects", "needs Project");
		}
		
		protected class GetFileListener implements AvailabilityListener<List<FileObject>> {
			public GetFileListener() {
				super();
			}
			
			@Override
			public void error(Exception t) {
				t.printStackTrace();
			}

			@Override
			public void finished(List<FileObject> o) {
				for (FileObject f : o)
					System.out.println("\t" + f);
			}

			@Override
			public void statusUpdate(double progress, String status) {
				//empty implementation
			}
		}

		@Override
		public void handleArguments() {
			AvailableLaterObject<List<FileObject>> avail;
			
			if (project == null) {
				System.out.println("no project");
				return;
			}
			try {
				for (NoteObject f : sync.getNotes(project)) {
					System.out.println("\t" + f);
				}
				avail = sync.getFiles(project);
				avail.setListener(new GetFileListener());
				avail.start();
			} catch (FrontendNotLoggedInException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
				sync.announce(jo, LogAction.JAKE_OBJECT_NEW_VERSION,
						"something new, something blue");
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
				sync.announce(jo, LogAction.JAKE_OBJECT_DELETE,
						"something new, something red");
				System.out.println("deleting done");
			} catch (Exception e) {
				System.out.println("deleting failed");
				e.printStackTrace();
			}
		}
	}

	class LockCommand extends LazyJakeObjectCommand {

		public LockCommand() {
			super("lock");
		}

		@Override
		public void handleArguments(JakeObject jo) {
			try {
				System.out.println("locking ... ");
				sync
						.announce(jo, LogAction.JAKE_OBJECT_LOCK,
								"I'm working on this. Please wait for me to finish or contact me");
				System.out.println("locking done");
			} catch (Exception e) {
				System.out.println("locking failed");
				e.printStackTrace();
			}
		}
	}

	class UnLockCommand extends LazyJakeObjectCommand {

		public UnLockCommand() {
			super("unlock");
		}

		@Override
		public void handleArguments(JakeObject jo) {
			try {
				System.out.println("unlocking ... ");
				sync.announce(jo, LogAction.JAKE_OBJECT_UNLOCK,
						"I'm done working on this.");
				System.out.println("unlocking done");
			} catch (Exception e) {
				System.out.println("unlocking failed");
				e.printStackTrace();
			}
		}
	}

	class PokeCommand extends LazyOtherUserCommand {
		public PokeCommand() {
			super("poke", "Poke another user (inform them that it would be a good time to fetch our logs). " +
					"Needs an open project.");
		}

		@Override
		public void handleArguments(String userid) {
			System.out.println("Poking " + userid + "...");
			sync.poke(project, new UserId(ProtocolType.XMPP, userid));
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
				for (LogEntry<?> le : pms.getLog(jo)) {
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
	}

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
	}

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
	}
}
