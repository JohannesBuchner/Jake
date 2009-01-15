package com.jakeapp.core.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.TrustState;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.util.ApplicationContextFactory;
import com.jakeapp.jake.fss.FSService;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;

public class ProjectsManagingServiceImpl implements IProjectsManagingService {
	private static final Logger log = Logger.getLogger(ProjectsManagingServiceImpl.class);

	private List<Project> projectList = new ArrayList<Project>();
	
	private Map<Project,IFSService> fileServices;

	private ApplicationContextFactory applicationContextFactory;

	private IProjectDao projectDao;

	private InternalFrontendService frontendService;

	public ProjectsManagingServiceImpl() {
		this.setFileServices(new HashMap<Project,IFSService>());
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
	@Transactional
    @Override
	public List<Project> getProjectList() {
        log.debug("calling ProjectsManagingServiceImpl.getProjectList() ");



        List<Project> results =this.getProjectDao().getAll();
        if(results != null)
        {
            log.debug("found " + results.size() + " projects to return");
            return results;
        }
        log.warn("didn't got any results!!!!!");
        throw new RuntimeException(" this should return an empty list");
		//return Collections.unmodifiableList(this.getInternalProjectList());
	}

	@Transactional
    @Override
	public List<Project> getProjectList(InvitationState state) {
		
		//FIXME not very elegant: can't we do that via DB-Select?
        // Yes we can. -- domdorn

//        return this.getProjectList(); // TODO TMP by domdorn // FIXME
		List<Project> all = this.getProjectList();
		List<Project> result = new ArrayList<Project>();

		for (Project p : all)
			if (p.getInvitationState().equals(state))
				result.add(p);

		return result;
	}


    @Transactional
	@Override
	public Project createProject(String name, String rootPath, MsgService msgService)
			throws FileNotFoundException, IllegalArgumentException {

        log.debug("Creating a Project with name " + name + " and path "  +rootPath);
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
		
		//TODO create a new Project-local database
		
		try {
			log.debug("initializing project folder");
            this.initializeProjectFolder();
		} catch (IOException e) {
            log.debug("initializing project folder didn't work");
			throw new FileNotFoundException();
		}
		
		//Open the project
		this.openProject(projectRoot, name);
		
		
		//create an applicationContext for the Project
		//this.applicationContextFactory.getApplicationContext(project);

		
		project.setMessageService(msgService);
		
		
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
		//Check preconditions
		if (project == null) throw new IllegalArgumentException();
		if (!project.isOpen() || project.isStarted()) return false;
		
		try {
			this.getFileServices(project).setRootPath(project.getRootPath());
		} catch (Exception e) {
			throw new ProjectException(e);
		}
		
		frontendService.getSync().startServing(project, new TrustRequestHandlePolicy(project), cl);
		
		project.setStarted(true);	
		
		return true;
	}

	@Override
	public boolean stopProject(Project project) throws IllegalArgumentException,
			FileNotFoundException {
		//Check preconditions
		if (project == null) throw new IllegalArgumentException();
		if (!project.isOpen() || !project.isStarted()) return false;
		
		try {
			frontendService.getSync().stopServing(project);
			//stops monitoring the project
			this.getFileServices(project).unsetRootPath();
		}
		finally {
			project.setStarted(false);
		}
		
		return true;
	}

	@Override
	public void closeProject(Project project) throws IllegalArgumentException,
			FileNotFoundException {
		//Check preconditions
		if (project == null) throw new IllegalArgumentException();
		
		//Make sure project is stopped
		if (project.isStarted())
			this.stopProject(project);
		project.setOpen(false);
		
		//remove project from internal list
		this.getInternalProjectList().remove(project);
		//remove the project's file services
		this.getFileServices().remove(project);
		
		//Remove Project from the database
		try {
			this.getProjectDao().delete(project);
		} catch (NoSuchProjectException e) {
			log.warn("Project does not exist in DB and cannot be deleted from it.",e);
		}
	}

	@Override
	public Project openProject(File rootPath, String name) throws IllegalArgumentException,
			FileNotFoundException {
		Project result;
		
		//Check preconditions
		if (rootPath == null) throw new IllegalArgumentException();
		
		//Check if we can at least read the specified directory
		if (!rootPath.canRead()) {
			log.warn("Cannot read project folder: " + rootPath.toString());
			throw new FileNotFoundException();
		}
		//Check if rootpath is indeed a directory
		if (!rootPath.isDirectory()) {
			log.warn("Project folder is not a directory: " + rootPath.toString());
			throw new FileNotFoundException();
		}
		
		//TODO Check if the Project-internal Database can be read (and read it!) 
		
		//create Project
		result = new Project(
			name,
			UUID.randomUUID(),
			null, //msgService
			rootPath
		);
		
		//add Project to the global database
		try {
			this.getProjectDao().create(result);
		} catch (InvalidProjectException e) {
			log.error("Opening a project failed: Project was invalid");
			throw new IllegalArgumentException();
		}
		
		//add the project's file services
		 try {
			this.getFileServices().put(result, new FSService());
		} catch (NoSuchAlgorithmException e) {
			log.error("Fileservices are not supported.");
			//TODO rethrow something?
		}
		
		//add project to internal list
		this.getInternalProjectList().add(result);
		
		result.setOpen(true);
		
		return result;
	}

	@Override
	public boolean deleteProject(Project project) throws IllegalArgumentException,
			SecurityException, FileNotFoundException {
		boolean result = true;
		IFSService fss;
		FileNotFoundException t = null;
		
		//Check preconditions
		if (project == null) throw new IllegalArgumentException();
		
		//Remove the Project's root folder
		fss = this.getFileServices(project);
		if (fss!=null) {
			try {
				fss.trashFile(project.getRootPath());
			} catch (InvalidFilenameException e) {
				log.warn("Deleting Project with invalid rootpath.",e);
			} catch (FileNotFoundException e) {
				t = e;
			}
		}
		
		//Make sure project is stopped & closed
		if (project.isOpen())
			this.closeProject(project);
		
		if (t!=null) throw t;
		
		return result; 
	}

	@Override
	public List<LogEntry<? extends ILogable>> getLog(Project project)
			throws IllegalArgumentException {
		ApplicationContext context;
		ILogEntryDao dao;
		List<LogEntry<? extends ILogable>> result = null;
		
		if (project==null) throw new IllegalArgumentException();
		
		context = this.getApplicationContextFactory().getApplicationContext(project);
		
		//TODO retrieve Logentrydao out of context
		dao = null;
		
		//get Log via LogentryDao
		try {
			result = dao.getAll(project);
		} catch (NoSuchProjectException e) {
			throw new IllegalArgumentException(e);
		}
		
		return result;
	}

	@Override
	public List<LogEntry<? extends ILogable>> getLog(JakeObject jakeObject)
			throws IllegalArgumentException {
		ApplicationContext context;
		Project project;
		ILogEntryDao dao;
		List<LogEntry<JakeObject>> entries = null;
		List<LogEntry<? extends ILogable>> result = null;
		
		if (jakeObject==null) throw new IllegalArgumentException();
		project = jakeObject.getProject();
		if (project==null) throw new IllegalArgumentException();
		
		context = this.getApplicationContextFactory().getApplicationContext(project);
		
		//TODO retrieve Logentrydao out of context
		dao = null;	
		
		entries = dao.getAllOfJakeObject(jakeObject);
		
		//transform dao-result to resulttype
		result = new ArrayList<LogEntry<? extends ILogable>>();
		result.addAll(entries);
		
		return result;
	}

	@Override
	public void assignUserToProject(Project project, UserId userId)
			throws IllegalArgumentException, IllegalAccessException {
	
		//Check preconditions
		if (project == null) throw new IllegalArgumentException();
		if (userId == null) throw new IllegalArgumentException();
		if (project.getUserId() != null) throw new IllegalStateException();
		
		//connect userId and project
		project.setUserId(userId);
		
		//persist Project-changes
		try {
			this.getProjectDao().update(project);
		} catch (NoSuchProjectException e) {
			throw new IllegalArgumentException(e);
		}
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
