package com.jakeapp.core.services;

import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.util.ApplicationContextFactory;
import com.jakeapp.jake.fss.FSService;
import com.jakeapp.jake.fss.IFSService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.UUID;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;


public class ProjectsManagingServiceImpl implements IProjectsManagingService {
	private static final Logger log = Logger.getLogger(ProjectsManagingServiceImpl.class);

	private List<Project> projectList = new ArrayList<Project>();
	
	private Map<Project,IFSService> fileServices = new HashMap<Project,IFSService>();

	private ApplicationContextFactory applicationContextFactory;

	private IProjectDao projectDao;

	private IFrontendSession frontendSession;

	public ProjectsManagingServiceImpl() {
	}

	/************** GETTERS & SETTERS *************/

	private void setFileServices(Map<Project,IFSService> fileServices) {
		this.fileServices = fileServices;
	}

	private Map<Project,IFSService> getFileServices() {
		return fileServices;
	}

	public IProjectDao getProjectDao() {
		return projectDao;
	}

	public void setProjectDao(IProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	public ApplicationContextFactory getApplicationContextFactory() {
		return applicationContextFactory;
	}

	public void setApplicationContextFactory(
			ApplicationContextFactory applicationContextFactory) {
		this.applicationContextFactory = applicationContextFactory;
	}
	
	@Override
	public IFSService getFileServices(Project p) {
		return this.getFileServices().get(p);
	}

	private List<Project> getInternalProjectList() {
		return this.projectList;
	}

	/******** STARTING IMPLEMENTATIONS **************/
	@Override
	public List<Project> getProjectList() {
		return Collections.unmodifiableList(this.getInternalProjectList());
	}

	@Override
	public List<Project> getProjectList(InvitationState state) {
		if (state == InvitationState.ACCEPTED) {
			List<Project> projects = new ArrayList<Project>();


			Project pr1 = new Project("Desktop", new UUID(212, 383), null, new File("")
			// new File(FileUtilities.getUserHomeDirectory() +
			// FileUtilities.getPathSeparator() + "Desktop")
			);
			pr1.setStarted(true);
			pr1.setInvitationState(InvitationState.ACCEPTED);
			projects.add(pr1);

			Project pr2 = new Project("Downloads", new UUID(222, 373), null, new File("")
			// new File(FileUtilities.getUserHomeDirectory() +
			// FileUtilities.getPathSeparator() + "Downloads")
			);
			pr2.setInvitationState(InvitationState.ACCEPTED);
			projects.add(pr2);

			Project pr3 = new Project("Jake", new UUID(232, 363), null, new File("")
			// new File(FileUtilities.getUserHomeDirectory() +
			// FileUtilities.getPathSeparator() + "Jake")
			);
			pr3.setInvitationState(InvitationState.ACCEPTED);
			projects.add(pr3);

			// Yes, we need a windows testing project too...
			Project pr4 = new Project("Windows test", new UUID(242, 353), null, new File(
					"C:\\test"));
			pr4.setInvitationState(InvitationState.ACCEPTED);
			projects.add(pr4);
			return projects;


		} else if (state == InvitationState.INVITED) {
			List<Project> invitedProjects = new ArrayList<Project>();
			invitedProjects.clear(); // TMP RESET PROJECTS

			Project ipr1 = new Project("DEMO INVITATION 1", new UUID(212, 33), null,
					new File(""));
			ipr1.setInvitationState(InvitationState.INVITED);
			invitedProjects.add(ipr1);

			Project ipr2 = new Project("DEMO INVITATION 2", new UUID(222, 33), null,
					new File(""));
			ipr2.setInvitationState(InvitationState.INVITED);
			invitedProjects.add(ipr2);

			Project ipr3 = new Project("DEMO INVITATION 3", new UUID(232, 33), null,
					new File(""));
			ipr3.setInvitationState(InvitationState.INVITED);
			invitedProjects.add(ipr3);
			return invitedProjects;

		}


		return null;
	}

	@Override
	public Project createProject(String name, String rootPath, MsgService msgService)
			throws FileNotFoundException, IllegalArgumentException {
		ApplicationContext context;
		
		File projectRoot = new File(rootPath);
		
		//create a new, empty project
		Project project = new Project(name, UUID.randomUUID(), msgService, projectRoot);
		
		//create and initialize the Project's root folder
		/*
		 * Since the FSService would start to watch the directory immediately, it is not created yet.
		 */
		projectRoot.mkdirs();
		if (!projectRoot.isDirectory() || !projectRoot.canWrite()) {
			log.warn("Creating a Project's root path failed.");
			throw new FileNotFoundException();
		}
		
		try {
			this.initializeProjectFolder();
		} catch (IOException e) {
			throw new FileNotFoundException();
		}
		
		//create an applicationContext for the Project
		context = this.applicationContextFactory.getApplicationContext(project);
		
		//TODO retrieve the dao from the context
		
		//create the new Project in the global database
		try {
			project = this.getProjectDao().create(project);
		} catch (InvalidProjectException e) {
			log.debug("Creating a project failed.",e);
			project = null;
		}
		
		if (project!=null) {
			//add the project to the service's internal project list	
			this.getInternalProjectList().add(project);
		}
		
		//TODO create a new Project-local database
		
		return project; 
	}

	/**
	 * Initializes a project folder by putting special files
	 * (self-implemented trash, ...) in it.
	 */
	private void initializeProjectFolder() throws IOException {
		//empty implementation
	}

	@Override
	public boolean startProject(Project project, ChangeListener cl) throws IllegalArgumentException,
			FileNotFoundException, ProjectException {
		frontendSession.getSync().startServing(project, new TrustRequestHandlePolicy(project), cl);
		return false; // TODO
	}

	@Override
	public boolean stopProject(Project project) throws IllegalArgumentException,
			FileNotFoundException {
		frontendSession.getSync().stopServing(project);
		return false; // TODO
	}

	@Override
	public void closeProject(Project project) throws IllegalArgumentException,
			FileNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public Project openProject(String project) throws IllegalArgumentException,
			FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteProject(Project project) throws IllegalArgumentException,
			SecurityException, FileNotFoundException {
		return false; // TODO
	}

	@Override
	public Map<Project, List<LogEntry<? extends ILogable>>> getLog() {
		return null; // TODO
	}

	@Override
	public List<LogEntry<? extends ILogable>> getLog(Project project)
			throws IllegalArgumentException {
		return null; // TODO
	}

	@Override
	public List<LogEntry<? extends ILogable>> getLog(JakeObject jakeObject)
			throws IllegalArgumentException {
		return null; // TODO
	}

	@Override
	public void assignUserToProject(Project project, UserId userId)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO
	}

	@Override
	public void setTrust(Project project, UserId userid, TrustState trust) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<FileObject> getFiles(Project project, String relPath)
			throws IllegalArgumentException {
		return null; // TODO
	}

	@Override
	public List<NoteObject> getNotes(Project project) throws IllegalArgumentException,
			ProjectNotLoadedException {

		if (false) // TODO check if project is loaded
			throw new ProjectNotLoadedException("Project with uuid "
					+ project.getUserId() + " is not loaded");

		// todo replace with dao access
		List<NoteObject> list = new ArrayList<NoteObject>();
		list
				.add(new NoteObject(new UUID(1, 1), project, "Project: "
						+ project.getName()));
		list
				.add(new NoteObject(
						new UUID(1, 1),
						project,
						"If you have five dollars and Chuck Norris has five dollars, Chuck Norris has more money than you"));
		list.add(new NoteObject(new UUID(2, 1), project,
				"Apple pays Chuck Norris 99 cents every time he listens to a song."));
		list
				.add(new NoteObject(
						new UUID(3, 1),
						project,
						"Chuck Norris is suing Myspace for taking the name of what he calls everything around you."));
		list
				.add(new NoteObject(
						new UUID(4, 1),
						project,
						"Chuck Norris destroyed the periodic table, because he only recognizes the element of surprise."));
		list.add(new NoteObject(new UUID(4, 1), project,
				"Chuck Norris can kill two stones with one bird."));
		list
				.add(new NoteObject(
						new UUID(5, 1),
						project,
						"The leading causes of death in the United States are: 1. Heart Disease 2. Chuck Norris 3. Cancer."));
		list.add(new NoteObject(new UUID(6, 1), project,
				"Chuck Norris does not sleep. He waits."));
		list
				.add(new NoteObject(new UUID(7, 1), project,
						"There is no theory of evolution. Just a list of animals Chuck Norris allows to live. "));
		list.add(new NoteObject(new UUID(8, 1), project,
				"Guns don't kill people, Chuck Norris does."));
		return list;
	}
}
