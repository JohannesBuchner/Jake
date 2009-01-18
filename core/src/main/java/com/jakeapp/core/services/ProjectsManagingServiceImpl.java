package com.jakeapp.core.services;

import com.jakeapp.core.dao.*;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.services.futures.AllProjectFilesFuture;
import com.jakeapp.core.services.futures.ProjectFileCountFuture;
import com.jakeapp.core.services.futures.ProjectSizeTotalFuture;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.core.synchronization.FriendlySyncService;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.util.ApplicationContextFactory;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;
import com.jakeapp.jake.fss.FSService;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ProjectsManagingServiceImpl implements IProjectsManagingService {

	private static final Logger log = Logger
			  .getLogger(ProjectsManagingServiceImpl.class);

	private List<Project> projectList = new ArrayList<Project>();

	private Map<Project, IFSService> fileServices;

	private ApplicationContextFactory applicationContextFactory;

	private IProjectDao projectDao;

	private IFriendlySyncService syncService;


	public ProjectsManagingServiceImpl() {
		this.setFileServices(new HashMap<Project, IFSService>());
	}


	public IFriendlySyncService getSyncService() {
		return syncService;
	}

	public void setSyncService(IFriendlySyncService syncService) {
		this.syncService = syncService;
	}

	/**
	 * *********** GETTERS & SETTERS ************
	 */

	private void setFileServices(Map<Project, IFSService> fileServices) {
		this.fileServices = fileServices;
	}

	private Map<Project, IFSService> getFileServices() {
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

	@SuppressWarnings("unused")
	private ApplicationContext getContext(Project p) {
		return this.getApplicationContextFactory().getApplicationContext(p);
	}

	private ILogEntryDao getLogEntryDao(Project project) {
		return this.getApplicationContextFactory().getLogEntryDao(project);
	}

	private IProjectMemberDao getProjectMemberDao(Project project) {
		return this.getApplicationContextFactory().getProjectMemberDao(project);
	}

	private INoteObjectDao getNoteObjectDao(Project project) {
		return this.getApplicationContextFactory().getNoteObjectDao(project);
	}

	private IFileObjectDao getFileObjectDao(Project project) {
		return this.getApplicationContextFactory().getFileObjectDao(project);
	}


	/**
	 * ***** STARTING IMPLEMENTATIONS *************
	 */
	@Transactional
	@Override
	public List<Project> getProjectList() {
		log.debug("calling ProjectsManagingServiceImpl.getProjectList() ");

		List<Project> results = this.getProjectDao().getAll();
		if (results != null) {
			log.debug("found " + results.size() + " projects to return");
			return results;
		}
		log.warn("didn't got any results!!!!!");
		throw new RuntimeException(" this should return an empty list");
		// return Collections.unmodifiableList(this.getInternalProjectList());
	}

	@Transactional
	@Override
	public List<Project> getProjectList(InvitationState state) {
		return this.getProjectDao().getAll(state);
	}


	@Transactional
	@Override
	public Project createProject(String name, String rootPath,
										  MsgService msgService) throws FileNotFoundException,
			  IllegalArgumentException {

		log.debug("Creating a Project with name " + name + ", path "
				  + rootPath + " and MsgService " + msgService);
		File projectRoot = new File(rootPath);

		// create a new, empty project
		Project project = new Project(name, UUID.randomUUID(), msgService,
				  projectRoot);

		// create and initialize the Project's root folder
		/*
		 * Since the FSService would start to watch the directory immediately,
		 * it is not created yet.
		 */
		projectRoot.mkdirs();
		if (!projectRoot.isDirectory() || !projectRoot.canWrite()) {
			log.warn("Creating a Project's root path failed.");
			throw new FileNotFoundException();
		}

		try {
			log.debug("initializing project folder");
			this.initializeProjectFolder(project);
		} catch (IOException e) {
			log.debug("initializing project folder didn't work");
			throw new FileNotFoundException();
		}

		// Open the project
		this.openProject(projectRoot, name);


		// create an applicationContext for the Project
		// this.applicationContextFactory.getApplicationContext(project);


		project.setMessageService(msgService);


		return project;
	}

	/**
	 * Initializes a project folder by putting special files (self-implemented
	 * trash, ...) in it.
	 */
	private void initializeProjectFolder(Project p) throws IOException {
		this.createProjectDatabase(p);
	}

	/**
	 * creates a file for the Project-local database
	 *
	 * @param p The project, in whos root folder to create a
	 *          database in.
	 */
	private void createProjectDatabase(Project p) {
		log.info("Create project database: " + p);

		// this should to all the magic
		this.applicationContextFactory.getApplicationContext(p);

		// TODO: cleanup
		/*
				//TODO not beautiful at all - may be completely wrong
		jdbcDataSource dataSource = new jdbcDataSource();
		ClassPathResource importScript;
		Statement stmt;
		Scanner sc;

		dataSource.setDatabase("jdbc:hsqldb:file:"+ new File(p.getRootPath(),"bla") +";ifexists=true;shutdown=false;create=true");
		dataSource.setUser("sa");
        dataSource.setPassword("");

        importScript = new ClassPathResource("/com/jakeapp/core/services/hsql-db-setup.sql");

        try {
			stmt = dataSource.getConnection("sa", "").createStatement();
			try {
				try {
					sc = new Scanner(new BufferedInputStream(importScript.getInputStream()));
					try {
						while (sc.hasNextLine()) {
		        			stmt.addBatch(sc.nextLine());
		        		}
					}
					finally {
						sc.close();
					}
				} catch (IOException e) {
				}
	        	stmt.executeUpdate("SHUTDOWN COMPACT");
	        }
	        finally {
	        	try {
	        		stmt.close();
	        	}
	        	catch (SQLException sqlex) {
	        		log.warn(sqlex);
					throw sqlex;
	        	}
	        }
		} catch (SQLException e) {
			  log.warn(e);
			  throw new RuntimeException(e);
		}
	}
		 */
	}


	@Override
	public boolean startProject(Project project, ChangeListener cl)
			  throws IllegalArgumentException, FileNotFoundException,
			  ProjectException {
		// Check preconditions
		if (project == null)
			throw new IllegalArgumentException();
		if (!project.isOpen() || project.isStarted())
			return false;

		try {
			this.getFileServices(project).setRootPath(project.getRootPath());
		} catch (Exception e) {
			throw new ProjectException(e);
		}

		this.syncService.startServing(project,
				  new TrustRequestHandlePolicy(project), cl);

		project.setStarted(true);

		return true;
	}

	@Override
	public boolean stopProject(Project project)
			  throws IllegalArgumentException, FileNotFoundException {
		// Check preconditions
		if (project == null)
			throw new IllegalArgumentException();
		if (!project.isOpen() || !project.isStarted())
			return false;

		try {
			syncService.stopServing(project);
			// stops monitoring the project
			this.getFileServices(project).unsetRootPath();
		} finally {
			project.setStarted(false);
		}

		return true;
	}

	@Override
	@Transactional
	public void closeProject(Project project) throws IllegalArgumentException,
			  FileNotFoundException {
		// Check preconditions
		if (project == null)
			throw new IllegalArgumentException();

		// Make sure project is stopped
		if (project.isStarted())
			this.stopProject(project);
		project.setOpen(false);

		// remove project from internal list
		this.getInternalProjectList().remove(project);
		// remove the project's file services
		this.getFileServices().remove(project);

		// Remove Project from the database
		try {
			this.getProjectDao().delete(project);
		} catch (NoSuchProjectException e) {
			log
					  .warn(
								 "Project does not exist in DB and cannot be deleted from it.",
								 e);
		}
	}

	@Override
	@Transactional
	public Project openProject(File rootPath, String name)
			  throws IllegalArgumentException, FileNotFoundException {
		Project result;

		// Check preconditions
		if (rootPath == null)
			throw new IllegalArgumentException();

		// Check if we can at least read the specified directory
		if (!rootPath.canRead()) {
			log.warn("Cannot read project folder: " + rootPath.toString());
			throw new FileNotFoundException();
		}
		// Check if rootpath is indeed a directory
		if (!rootPath.isDirectory()) {
			log.warn("Project folder is not a directory: "
					  + rootPath.toString());
			throw new FileNotFoundException();
		}

		// create Project
		result = new Project(name, UUID.randomUUID(), null, // msgService
				  rootPath);

		// Check if the Project-internal Database can be read (and read
		// it!)
		//if (!this.checkProjectDatabaseExists(result))
		//	throw new FileNotFoundException();

		// add Project to the global database
		try {
			this.getProjectDao().create(result);
		} catch (InvalidProjectException e) {
			log.error("Opening a project failed: Project was invalid");
			throw new IllegalArgumentException();
		}

		// add the project's file services
		try {
			this.getFileServices().put(result, new FSService());
		} catch (NoSuchAlgorithmException e) {
			log.error("Fileservices are not supported.");
			// throw a FileNotFoundException since working with Files will not
			// be possible.
			throw new FileNotFoundException();
		}

		// add project to internal list
		this.getInternalProjectList().add(result);

		result.setOpen(true);

		return result;
	}

	@Override
	public boolean deleteProject(Project project)
			  throws IllegalArgumentException, SecurityException,
			  FileNotFoundException {
		boolean result = true;
		IFSService fss;
		FileNotFoundException t = null;

		// Check preconditions
		if (project == null)
			throw new IllegalArgumentException();

		// Remove the Project's root folder
		fss = this.getFileServices(project);
		if (fss != null) {
			try {
				fss.trashFile(project.getRootPath());
			} catch (InvalidFilenameException e) {
				log.warn("Deleting Project with invalid rootpath.", e);
			} catch (FileNotFoundException e) {
				t = e;
			}
		}

		// Make sure project is stopped & closed
		if (project.isOpen())
			this.closeProject(project);

		if (t != null)
			throw t;

		return result;
	}

	@Override
	@Transactional
	public List<LogEntry> getLog(Project project)
			  throws IllegalArgumentException {
		ILogEntryDao dao;
		List<LogEntry> result = null;

		if (project == null)
			throw new IllegalArgumentException();

		dao = this.getLogEntryDao(project);

		// get Log via LogentryDao
		result = dao.getAll();

		return result;
	}

	@Override
	@Transactional
	public List<LogEntry<? extends ILogable>> getLog(JakeObject jakeObject)
			  throws IllegalArgumentException {
		Project project;
		ILogEntryDao dao;
		List<LogEntry<JakeObject>> entries = null;
		List<LogEntry<? extends ILogable>> result = null;

		if (jakeObject == null)
			throw new IllegalArgumentException();
		project = jakeObject.getProject();
		if (project == null)
			throw new IllegalArgumentException();

		// retrieve Logentrydao out of context
		dao = this.getLogEntryDao(project);

		entries = dao.getAllOfJakeObject(jakeObject);

		// transform dao-result to resulttype
		result = new ArrayList<LogEntry<? extends ILogable>>();
		result.addAll(entries);

		return result;
	}

	@Transactional
	@Override
	public void assignUserToProject(Project project, UserId userId)
			  throws IllegalArgumentException, IllegalAccessException {

		// Check preconditions
		if (project == null)
			throw new IllegalArgumentException();
		if (userId == null)
			throw new IllegalArgumentException();
		if (project.getUserId() != null)
			throw new IllegalStateException();

		// connect userId and project
		project.setUserId(userId);

		// persist Project-changes
		try {
			this.getProjectDao().update(project);
		} catch (NoSuchProjectException e) {
			throw new IllegalArgumentException(e);
		}

		// set UserId as ProjectMember
		this.addProjectMember(project, userId);
		this.setTrust(project, userId, TrustState.AUTO_ADD_REMOVE);
	}

	/**
	 * Adds a new Projectmember to a Project. It may not exist in the Project
	 * yet.
	 *
	 * @param project Project to add a member to. May not be null.
	 * @param userId  Member to add. May not be null.
	 */
	@Transactional
	private ProjectMember addProjectMember(Project project, UserId userId) {
		IProjectMemberDao dao;
		ProjectMember member;

		// retrieve ProjectMemberDao out of context
		dao = this.getProjectMemberDao(project);

		// create ProjectMember and add it to Project
		member = new ProjectMember(userId.getUuid(), userId.getNickname(), this
				  .getDefaultTrustState());
		member = dao.persist(project, member);

		return member;
	}

	private TrustState getDefaultTrustState() {
		return TrustState.NO_TRUST;
	}

	/**
	 * @param project may not be null
	 * @param userId  may not be null
	 * @return null if the User with the ID userId is not found in the Project.
	 *         The corresponding ProjectMember if it exisits.
	 */
	@Transactional
	private ProjectMember getProjectMember(Project project, UserId userId,
														IProjectMemberDao dao) {
		/*
		 * verlaesst sich auf userid.getUUid == projectMember.getUUid!!
		 */

		try {
			return dao.get(userId.getUuid());
		} catch (NoSuchProjectMemberException e) {
			return null;
		}
	}

	@Override
	@Transactional
	public void setTrust(Project project, UserId userId, TrustState trust)
			  throws IllegalArgumentException, IllegalAccessException {
		IProjectMemberDao dao;
		ProjectMember member;

		// /Check preconditions
		if (project == null)
			throw new IllegalArgumentException();
		if (userId == null)
			throw new IllegalArgumentException();
		if (project.getUserId() != null)
			throw new IllegalAccessException();

		dao = this.getProjectMemberDao(project);

		// get (or add) the Project member belonging to userId
		member = this.getProjectMember(project, userId, dao);
		if (member == null) {
			this.addProjectMember(project, userId);
			// invite ProjectMember to Project
			this.inviteMember(project, userId);
		}

		// set the new trustlevel
		member.setTrustState(trust);

		// persist changes to member
		dao.persist(project, member);
	}

	/**
	 * sends an Invitation to another ProjectMember
	 *
	 * @param project
	 */
	private void inviteMember(Project project, UserId userId) {
		this.getSyncService().invite(project, userId);
	}

	private boolean isProjectLoaded(Project project) {
		return this.getInternalProjectList().contains(project);
	}

	@Override
	public AvailableLaterObject<Integer> getProjectFileCount(Project project, AvailabilityListener listener)
			  throws NoSuchProjectException, FileNotFoundException, IllegalArgumentException {

		AvailableLaterWrapperObject<Integer, List<FileObject>> sizeFuture;
		AvailableLaterObject<List<FileObject>> filesFuture;

		sizeFuture = new ProjectFileCountFuture(listener);
		filesFuture = this.getAllProjectFiles(project, sizeFuture);
		sizeFuture.setSource(filesFuture);

		return sizeFuture;
	}

	@Override
	public AvailableLaterObject<Long> getProjectSizeTotal(Project project, AvailabilityListener listener)
			  throws NoSuchProjectException, FileNotFoundException, IllegalArgumentException {

		AvailableLaterWrapperObject<Long, List<FileObject>> sizeFuture;
		AvailableLaterObject<List<FileObject>> filesFuture;

		sizeFuture = new ProjectSizeTotalFuture(null);
		filesFuture = this.getAllProjectFiles(project, sizeFuture);
		sizeFuture.setSource(filesFuture);

		return sizeFuture;
	}

	@Override
	@Transactional
	public AvailableLaterObject<List<FileObject>> getAllProjectFiles(Project project, AvailabilityListener listener)
			  throws NoSuchProjectException, FileNotFoundException, IllegalArgumentException {
		IFileObjectDao dao;

		dao = this.getFileObjectDao(project);

		return (new AllProjectFilesFuture(listener, dao));
	}

	@Override
	@Transactional
	public FileObject getFileObjectByRelPath(Project project, String relpath) throws NoSuchJakeObjectException {
		IFileObjectDao dao = this.getFileObjectDao(project);
		return dao.get(relpath);
	}

	@Override
	@Transactional
	public void joinProject(Project project, UserId inviter)
			  throws IllegalStateException, NoSuchProjectException {

		// preconditions
		if (project == null)
			throw new NoSuchProjectException();
		if (!project.isInvitation())
			throw new IllegalStateException();

		// update join state
		project.setInvitationState(InvitationState.ACCEPTED);
		this.getProjectDao().update(project); // may throw a
		// NoSuchProjectException if
		// project does not exist

		// notify the inviter
		this.getSyncService().notifyInvitationAccepted(project, inviter);
	}

	@Override
	public void rejectProject(Project project, UserId inviter)
			  throws IllegalStateException, NoSuchProjectException {
		//preconditions
		if (project == null)
			throw new NoSuchProjectException();
		if (!project.isInvitation())
			throw new IllegalStateException();

		// remove the project
		try {
			this.closeProject(project);
		} catch (IllegalArgumentException e) {
			throw new NoSuchProjectException();
		} catch (FileNotFoundException e) {
			// empty catch
		}

		// notify the inviter
		this.getSyncService().notifyInvitationRejected(project, inviter);
	}

	@Override
	@Transactional
	public void updateProjectName(Project project, String newName)
			  throws NoSuchProjectException {

		if (project == null)
			throw new NoSuchProjectException();

		if (!project.getName().equals(newName)) {
			project.setName(newName);
			this.getProjectDao().update(project);
		}
	}

	@Override
	public Date getLastEdit(JakeObject jo)
			  throws NoSuchProjectException, IllegalArgumentException {
		LogEntry<? extends ILogable> logentry;
		Date result;

		//get the most recent logentry for the JakeObject
		try {
			logentry = this.getMostRecentFor(jo);
			result = logentry.getTimestamp();
		} catch (NoSuchLogEntryException e) {
			result = Calendar.getInstance().getTime();
		}

		return result;
	}

	@Override
	public ProjectMember getLastEditor(JakeObject jo)
			  throws NoSuchProjectException, IllegalArgumentException {
		LogEntry<? extends ILogable> logentry;
		ProjectMember result;

		//get the most recent logentry for the JakeObject
		try {
			logentry = this.getMostRecentFor(jo);
			result = logentry.getMember();
		} catch (NoSuchLogEntryException e) {
			result = null;
		}

		return result;
	}

	@Transactional
	private LogEntry<? extends ILogable> getMostRecentFor(JakeObject jo)
			  throws NoSuchProjectException, IllegalArgumentException, NoSuchLogEntryException {
		LogEntry<? extends ILogable> logentry;

		if (jo == null) throw new IllegalArgumentException();
		if (jo.getProject() == null) throw new NoSuchProjectException();

		//get the most recent logentry for the JakeObject
		//TODO rather call getMostRecentProcessed
		logentry = this.getLogEntryDao(jo.getProject()).getMostRecentFor(jo);

		return logentry;
	}

	@Override
	@Transactional
	public ProjectMember getProjectMember(Project project, MsgService msg) {
		return this.getProjectMember(project, msg.getUserId(), this.getProjectMemberDao(project));
	}

	@Override
	public String getProjectMemberID(Project project, ProjectMember pm) {
		//TODO it is completely unclear how to do that!! Dominik, please help us!
		return "";
	}


	@Override
	public INoteManagingService getNoteManagingService(Project p)
			  throws ProjectNotLoadedException, IllegalArgumentException {
		INoteObjectDao dao;

		// preconditions
		if (p == null)
			throw new IllegalArgumentException();
		if (!this.isProjectLoaded(p)) {
			throw new ProjectNotLoadedException("Project with id "
					  + p.getProjectId() + " is not loaded");
		}

		//TODO cache
		return new NoteManagingService(this.getNoteObjectDao(p), this.getLogEntryDao(p));
	}


	@Override
	@Transactional
	public List<ProjectMember> getProjectMembers(Project project) throws NoSuchProjectException {
		if (project==null) throw new NoSuchProjectException();
		
		return this.getProjectMemberDao(project).getAll(project);
	}


	@Override
	@Transactional
	public void updateProjectMember(Project project, ProjectMember member) {
		this.getProjectMemberDao(project).persist(project, member);
		
	}
}
