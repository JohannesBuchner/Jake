package com.jakeapp.core.services;

import com.jakeapp.core.dao.*;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.logentries.*;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import com.jakeapp.core.domain.exceptions.UserIdFormatException;
import com.jakeapp.core.services.futures.DeleteFilesFuture;
import com.jakeapp.core.services.futures.GetProjectsFuture;
import com.jakeapp.core.services.futures.ProjectFileCountFuture;
import com.jakeapp.core.services.futures.ProjectSizeTotalFuture;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.synchronization.UserInfo;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.util.UnprocessedBlindLogEntryDaoProxy;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;
import com.jakeapp.core.util.availablelater.AvailableNowObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotADirectoryException;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.status.IStatusService;
import com.jakeapp.jake.ics.users.IUsersService;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class ProjectsManagingServiceImpl extends JakeService implements
		IProjectsManagingService {

	private static final Logger log = Logger.getLogger(ProjectsManagingServiceImpl.class);

	private IProjectDao projectDao;

	private IInvitationDao invitationDao;

	private IAccountDao accountDao;

	private IFriendlySyncService syncService;

	private IProjectsFileServices projectsFileServices;

	private MsgServiceManager msgServiceFactory;

	private ChangeListener changeListener;

	// initialized projects are saved here.
	private HashMap<Project, Boolean> activeProjects = new HashMap<Project, Boolean>();

	public void setChangeListener(ChangeListener changeListener) {
		this.changeListener = changeListener;
	}

	public ProjectsManagingServiceImpl(
			ProjectApplicationContextFactory applicationContextFactory,
			MsgServiceManager msgServiceFactory) {
		super(applicationContextFactory);
		this.msgServiceFactory = msgServiceFactory;
	}

	public IFriendlySyncService getSyncService() {
		return this.syncService;
	}

	public void setSyncService(IFriendlySyncService syncService) {
		this.syncService = syncService;
	}

	/*
	 * *********** GETTERS & SETTERS ***********
	 */

	public IProjectDao getProjectDao() {
		return this.projectDao;
	}

	public void setProjectDao(IProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	@Override
	public IFSService getFileServices(Project p) {
		return this.getProjectsFileServices().getProjectFSService(p);
	}

	private UnprocessedBlindLogEntryDaoProxy getLogEntryDao(Project project) {
		return this.getApplicationContextFactory().getLogEntryDao(project);
	}

	private INoteObjectDao getNoteObjectDao(Project project) {
		return this.getApplicationContextFactory().getNoteObjectDao(project);
	}

	private IFileObjectDao getFileObjectDao(Project project) {
		return this.getApplicationContextFactory().getFileObjectDao(project);
	}

	public IInvitationDao getInvitationDao() {
		return invitationDao;
	}

	public void setInvitationDao(IInvitationDao invitationDao) {
		this.invitationDao = invitationDao;
	}

	/*
		 * @SuppressWarnings("unchecked") private IJakeObjectDao<JakeObject>
		 * getJakeObjectDao(final Project project, final JakeObject jo) {
		 * IJakeObjectDao result = null;
		 *
		 * if (jo != null) { if (jo instanceof FileObject) result =
		 * this.getFileObjectDao(project); else if (jo instanceof NoteObject) result
		 * = this.getNoteObjectDao(project); }
		 *
		 * return result; }
		 */

	/*
	 * ***** STARTING IMPLEMENTATIONS *************
	 */
	
	@Override
	public AvailableLaterObject<List<Project>> getProjects(MsgService user) {
		return new GetProjectsFuture(this,user);
	}
	
	@Transactional
	@Override
	public List<Project> getProjectList(MsgService msg) {
		List<Project> result;
		log.debug("calling ProjectsManagingServiceImpl.getProjectList() ");

		result = this.getProjectDao().getAll(msg.getServiceCredentials());
		if (result != null) {
			checkProjects(result);
			log.debug("found " + result.size() + " projects to return");
			return result;
		} else {
			log.warn("didn't get any results from ProjectDao.getAll");
			return Collections.emptyList();
		}
	}

	private void checkProjects(List<Project> result) {
		List<Project> invalidProjects = new LinkedList<Project>();
		for (Project p : result) {
			log.debug("getProjectList gave us a project with credentials:"
					+ p.getCredentials());
			try {
				initProject(p);
			} catch (NoSuchProjectException e) {
				log.error("invalid project: ", e);
				invalidProjects.add(p);
			} catch (NotADirectoryException e) {
				log.error("invalid project: ", e);
				invalidProjects.add(p);
			} catch (FileNotFoundException e) {
				log.error("invalid project (error on start): ", e);
				invalidProjects.add(p);
			} catch (ProjectException e) {
				log.error("invalid project (error on start): ", e);
				invalidProjects.add(p);
			}
		}
		result.removeAll(invalidProjects);
	}

	@Transactional
	@Override
	public List<Project> getProjectList(InvitationState state) {
		log.debug("calling getProjectList");
		List<Project> result = this.getProjectDao().getAll(state);
		checkProjects(result);
		return result;
	}

	@Override
	public List<Invitation> getInvitations(MsgService user) {
		return this.invitationDao.getAll(user.getUserId());
	}

	private Project initProject(Project p) throws NoSuchProjectException,
			NotADirectoryException, FileNotFoundException, ProjectException {
		log.debug("initialising project " + p);

		p.setOpen(true);

		log.debug("Init Project: " + p + " with credentials: " + p.getCredentials());
		if (p.getMessageService() == null)
			p.setMessageService(msgServiceFactory.getOrCreate(p.getCredentials()));
		
		if(p.getCredentials() != p.getMessageService().getServiceCredentials()){
			p.setCredentials(p.getMessageService().getServiceCredentials());
			this.getProjectDao().update(p);
		}

		try {
			this.getProjectsFileServices().startForProject(p);
		} catch (IOException e) {
			log.debug("starting fss failed", e);
		}
		

		if (!p.getUserId().equals(p.getMessageService().getUserId()))
			throw new IllegalStateException();

		/*
		 * A project, that has the started-property set although it was just fetched from
		 * the database was started the last time the user manipulated it and must be
		 * restarted here.
		 */
		if(p.isStarted() && !isActive(p)) {
			p.setStarted(false);
			this.startProject(p);
			setActive(p);
		}

		log.debug("initialising project done for " + p);
		return p;
	}


	private void setActive(Project p) {activeProjects.put(p, true);}


	private boolean isActive(Project p) {
		Boolean b = activeProjects.get(p);
		return b != null ? b : false;
	}


	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public Project createProject(String name, String rootPath, MsgService msgService)
			throws IllegalArgumentException, IOException, NotADirectoryException {
		
		log.debug("Creating a Project with name " + name + ", path " + rootPath
				+ " and MsgService " + msgService);
		File projectRoot = new File(rootPath);

		if (msgService == null || msgService.userId == null)
			throw new IllegalArgumentException("MsgService must not be null!!");

		// create a new, empty project
		Project project = new Project(name, UUID.randomUUID(), msgService, projectRoot);
		project.setCredentials(msgService.getServiceCredentials());

		// Open the project (add to database)
		try {
			this.openProject(project);
		} catch (InvalidProjectException e) {
			throw new IllegalStateException("we created a illegal project", e);
		}

		try {
			log.debug("initializing project folder");
			this.initializeProjectFolder(project);
		} catch (IOException e) {
			log.debug("initializing project folder didn't work");
			throw new FileNotFoundException();
		}

		this.createFirstLogEntry(project);

		// welcome the user
		// TODO: only attach on the first created project!
		attachTestNotes(project);

		return project;
	}

	private void createFirstLogEntry(Project project) {
		// create the project's first logentry
		if (project.getMessageService() != null) {
			User user = project.getMessageService().getUserId();
			this.getLogEntryDao(project).create(new ProjectCreatedLogEntry(project, user));
		}
	}

	/**
	 * adds example notes. May be removed in the future.
	 * 
	 * @param project
	 */
	private void attachTestNotes(Project project) {
		INoteObjectDao noteObjectDao = this.getApplicationContextFactory()
				.getNoteObjectDao(project);
		List<NoteObject> notesList = new ArrayList<NoteObject>();

		// these are local notes, they don't have a log entry
		notesList.add(new NoteObject(UUID.randomUUID(), project,
				"Create Notes for your Project and share them with your friends."));
		notesList.add(new NoteObject(UUID.randomUUID(), project,
				"Everyone can add, change or remove the project notes."));

		for (NoteObject note : notesList) {
			noteObjectDao.persist(note);
		}
	}

	/**
	 * Initializes a project folder by putting special files (self-implemented
	 * trash, ...) in it.
	 * 
	 * @param p
	 * @throws java.io.IOException
	 */
	private void initializeProjectFolder(Project p) throws IOException {
		this.createProjectDatabase(p);
	}

	/**
	 * creates a file for the Project-local database
	 * 
	 * @param p
	 *            The project, in whos root folder to create a database in.
	 */
	private void createProjectDatabase(Project p) {
		log.info("Create project database: " + p);

		// this should to all the magic
		this.getApplicationContextFactory().getApplicationContextThread(p);
	}


	@Override
	public boolean startProject(Project project)
			throws IllegalArgumentException, FileNotFoundException, ProjectException {
		log.info("start project: " + project);

		// Check preconditions
		if (project == null)
			throw new IllegalArgumentException("project is null!");
		if (!project.isOpen() || project.isStarted()) {
			log.warn("Attemted to start a project that's not open or that is already started: "
					+ project);
			return false;
		}

		try {
			this.getFileServices(project).setRootPath(project.getRootPath());
			this.getProjectsFileServices().startForProject(project);
			this.syncService.startServing(project, changeListener);

			project.setStarted(true);
			this.setActive(project);
			this.getProjectDao().update(project);
		} catch (Exception e) {
			log.debug("starting failed",e);
			throw new ProjectException(e);
		}
		return true;
	}

	@Override
	public boolean stopProject(Project project) throws IllegalArgumentException,
																										FileNotFoundException,
																										NoSuchProjectException {
		// Check preconditions
		if (project == null)
			throw new IllegalArgumentException();
		if (!project.isOpen() || !project.isStarted())
			return false;

		try {
			this.syncService.stopServing(project);
			// stops monitoring the project
			this.getProjectsFileServices().stopForProject(project);
		} finally {
			project.setStarted(false);
			this.getProjectDao().update(project);
		}

		return true;
	}

	@Override
	@Transactional
	public void closeProject(Project project) throws IllegalArgumentException,
																									FileNotFoundException,
																									NoSuchProjectException {
		// Check preconditions
		if (project == null)
			throw new IllegalArgumentException();

		// Make sure project is stopped
		if (project.isStarted())
			this.stopProject(project);
		project.setOpen(false);

		// remove project from internal list
		// this.getInternalProjectList().remove(project);
		// remove the project's file services

		this.getProjectsFileServices().stopForProject(project);

		// Remove Project from the database
		try {
			this.getProjectDao().delete(project);
		} catch (NoSuchProjectException e) {
			log.warn("Project does not exist in DB and cannot be deleted from it.", e);
		}
	}

	@Override
	@Transactional
	public Project openProject(Project project) throws IllegalArgumentException,
			InvalidProjectException, IOException, NotADirectoryException {
		if(project != null && project.getMessageService() != null && project.getCredentials() ==null)
			project.setCredentials(project.getMessageService().getServiceCredentials());

		// add Project to the global database
		try {
			project = this.getProjectDao().create(project);
		} catch (InvalidProjectException e) {
			log.error("Opening a project failed: Project was invalid");
			throw new IllegalArgumentException(e);
		}

		// add the project's file services
		this.getProjectsFileServices().startForProject(project);

		project.setOpen(true);

		return project;
	}

	@Override
	public boolean deleteProject(Project project, boolean deleteProjectFiles) throws IllegalArgumentException, SecurityException, IOException,
																																									NotADirectoryException,
																																									NoSuchProjectException {
		boolean result = true;
		IFSService fss;
		FileNotFoundException t = null;

		// Check preconditions
		if (project == null)
			throw new IllegalArgumentException();

		// Remove the Project's root folder
		if (deleteProjectFiles) {
			log.debug("trashing project files: " + project.getRootPath());
			fss = this.getProjectsFileServices().startForProject(project);
			if (fss != null) {
				try {
					if (!fss.trashFolder("/")) {
						log.warn("trash failed for: " + project.getRootPath());
					}
				} catch (InvalidFilenameException e) {
					log.warn("Deleting Project with invalid rootpath.", e);
				} catch (FileNotFoundException e) {
					t = e;
				}
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
	public List<LogEntry<? extends ILogable>> getLog(Project project)
			throws IllegalArgumentException {
		ILogEntryDao dao;
		List<LogEntry<? extends com.jakeapp.core.domain.ILogable>> result = null;

		if (project == null)
			throw new IllegalArgumentException();

		dao = this.getApplicationContextFactory().getUnprocessedAwareLogEntryDao(project);

		// get Log via LogentryDao
		result = dao.getAll(true);

		return result;
	}

	@Override
	@Transactional
	public List<LogEntry<? extends ILogable>> getLog(JakeObject jakeObject)
			throws IllegalArgumentException {
		Project project;
		UnprocessedBlindLogEntryDaoProxy dao;
		List<LogEntry<JakeObject>> entries;
		List<LogEntry<? extends ILogable>> result;

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

	/**
	 * Assigns a User Id to the Project. Called only once, to bind user and
	 * project. It's not supposed to change an already bound user id.
	 * 
	 * @param project
	 *            the Project to set the UserId
	 * @param userId
	 *            the UserId to be set
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	/*
	 * @Transactional
	 * 
	 * @Override public void assignUserToProject(Project project, User userId)
	 * throws IllegalArgumentException, IllegalAccessException {
	 * 
	 * // Check preconditions if (project == null) throw new
	 * IllegalArgumentException("Project is null"); if (userId == null) throw
	 * new IllegalArgumentException("User ID is null"); if (project.getUserId()
	 * != null) throw new
	 * IllegalStateException("Project already has a user Id: " +
	 * project.getUserId());
	 * 
	 * // connect userId and project project.setUserId(userId);
	 * 
	 * // persist Project-changes try { this.getProjectDao().update(project); }
	 * catch (NoSuchProjectException e) { throw new IllegalArgumentException(e);
	 * }
	 * 
	 * // set User as ProjectMember this.addUserToProject(project, userId);
	 * this.setTrust(project, userId, TrustState.AUTO_ADD_REMOVE); }
	 */

	/**
	 * Adds a new Projectmember to a Project. It may not exist in the Project
	 * yet.
	 * 
	 * @param project
	 *            Project to add a member to. Must not be null.
	 * @param user
	 *            Member to add. Must not be null.
	 * @return the ProjectMember added
	 */
	private User addUserToProject(Project project, User user) {
		log.debug("adding " + user + " to " + project);
			try {
				TrustState currentState = this.getLogEntryDao(project).trustsHow(project.getUserId(), user);
				log.debug("current state is "+currentState);
				//check if this person is already a trusted project member
				if (currentState==TrustState.NO_TRUST) {
					//add logentry stating that we now trust this member
					this.setTrust(project, user, TrustState.TRUST);
				}
			} catch (IllegalArgumentException e) {
				//empty handling, as only a non-fulfilled precondition of this method was violated.
				log.debug(e);
			} catch (IllegalAccessException e) {
				//empty handling, as only a non-fulfilled precondition of this method was violated.
				log.debug(e);
			}
		return user;
	}


	@Override
	@Transactional
	public void setTrust(Project project, User user, TrustState trust)
			throws IllegalArgumentException, IllegalAccessException {
		LogEntry<User> le;
		
		log.debug("setting trust for " + user + " to " + trust);

		//check preconditions
		if (project==null) throw new IllegalArgumentException("Project may not be null");
		else if (user ==null)  throw new IllegalArgumentException("User may not be null");
		else if (project.getUserId()==null) throw new IllegalAccessException("Project must have a valid User");
		
		log.debug("passed preconditions-check."); 
		
		//determine action
		if (trust == TrustState.NO_TRUST) {
			le = new StopTrustingProjectMemberLogEntry(user, project.getUserId());
		}
		else if (trust == TrustState.TRUST) {
			le = new StartTrustingProjectMemberLogEntry(user, project.getUserId());
		}
		else {
			le = new FollowTrustingProjectMemberLogEntry(user, project.getUserId());
		}

		//insert logentry
		this.getLogEntryDao(project).create(le);
	}

	@Transactional
	private boolean isProjectLoaded(Project project) {
		try {
			this.getProjectDao().read(UUID.fromString(project.getProjectId()));
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public AvailableLaterObject<Integer> getProjectFileCount(Project project)
			throws NoSuchProjectException, FileNotFoundException,
			IllegalArgumentException {

		log.debug("Calling getProjectFileCount");

		AvailableLaterWrapperObject<Integer, List<FileObject>> sizeFuture;
		AvailableLaterObject<List<FileObject>> filesFuture;

		filesFuture = this.getAllProjectFiles(project);
		sizeFuture = new ProjectFileCountFuture();
		sizeFuture.setSource(filesFuture);

		return sizeFuture;
	}

	@Override
	public AvailableLaterObject<Long> getProjectSizeTotal(Project project)
			throws NoSuchProjectException, FileNotFoundException,
			IllegalArgumentException {

		log.debug("Calling getProjectSizeTotal");


		AvailableLaterWrapperObject<Long, List<FileObject>> sizeFuture;
		AvailableLaterObject<List<FileObject>> filesFuture;

		filesFuture = this.getAllProjectFiles(project);
		sizeFuture = new ProjectSizeTotalFuture(getFileServices(project));
		sizeFuture.setSource(filesFuture);

		return sizeFuture;
	}

	@Override
	public AvailableLaterObject<List<FileObject>> getAllProjectFiles(Project project)
			throws NoSuchProjectException, FileNotFoundException,
			IllegalArgumentException {
		log.debug("Calling getAllProjectFiles for " + project);

		return getApplicationContextFactory().getAllProjectFilesFuture(project,getFileServices(project));
	}


	@Override
	@Transactional
	public FileObject getFileObjectByRelPath(Project project, String relpath)
			throws NoSuchJakeObjectException {
		log.debug("calling getFileObjectByRelPath for relpath: " + relpath);
		IFileObjectDao dao = this.getFileObjectDao(project);
		return dao.get(relpath);
	}

	@Override
	@Transactional(readOnly = false)
	public void acceptInvitation(Invitation invitation, File rootPath)
			throws IllegalStateException, NoSuchProjectException {

		log.debug("joining a project ");
		invitation.setRootPath(rootPath);

		try {
//			this.getProjectDao().create(project); // may throw a
			// NoSuchProjectException if
			// project does not exist
			Project project = this.getInvitationDao().accept(invitation);
			this.getLogEntryDao(project).acceptInvitation(invitation);
			project = initProject(project);
			ProjectInvitationHandler.notifyInvitationAccepted(project,  invitation.getInviter());
		} catch (Exception e) {
			log.warn("Something weird happened while trying to init project!", e);
		}

		// notify the inviter
		try {
			// FIXME: WTF WHY DOESN'T THIS WORK:
			// if(inviter != null)
			// ProjectInvitationHandler.notifyInvitationAccepted(project, inviter);
			// else log.warn("Inviter not notified because we don't know who it is?!");
		} catch (Exception e) {
			log.warn("Couldn't notify inviter for some reason");
		}
	}

	@Override
	public void rejectInvitation(Invitation invitation)
			throws IllegalStateException, NoSuchProjectException {

		Project project = invitation.createProject();
		User inviter = invitation.getInviter();
		
		// preconditions
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
		ProjectInvitationHandler.notifyInvitationRejected(project, inviter);
	}

	@Override
	@Transactional
	public User invite(Project project, String userid) throws UserIdFormatException {
		User member;
		User id;

		log.info("invite project: " + project + " userid: " + userid + " msgservice: "
				+ project.getMessageService());

		id = project.getMessageService().getUserId(userid);
		log.debug("extracted the userid to invite: " + userid);

		ProjectMemberInvitedLogEntry logEntry =  new ProjectMemberInvitedLogEntry(id, project.getUserId());
		this.getLogEntryDao(project).create(logEntry);

//		member = this.addUserToProject(project, id);
		ProjectInvitationHandler.invite(project, id);
		return id;

//		return member;
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
	// TODO: think we can delete those...
	public Date getLastEdit(JakeObject jo) throws NoSuchProjectException,
			IllegalArgumentException {
		LogEntry<? extends ILogable> logentry;
		Date result;

		// get the most recent logentry for the JakeObject
		try {
			logentry = this.getMostRecentFor(jo);
			result = logentry.getTimestamp();
		} catch (NoSuchLogEntryException e) {
			result = now();
		}

		return result;
	}

	@Override
	// TODO: think we can delete those...
	public User getLastEditor(JakeObject jo) throws NoSuchProjectException,
			IllegalArgumentException {
		LogEntry<? extends ILogable> logentry;
		User result;

		// get the most recent logentry for the JakeObject
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
			throws NoSuchProjectException, IllegalArgumentException,
			NoSuchLogEntryException {
		LogEntry<? extends ILogable> logentry;

		if (jo == null)
			throw new IllegalArgumentException();
		if (jo.getProject() == null)
			throw new NoSuchProjectException();

		// get the most recent logentry for the JakeObject
		// TODO rather call getMostRecentProcessed
		logentry = this.getLogEntryDao(jo.getProject()).getLastVersionOfJakeObject(jo);

		return logentry;
	}

	@Override
	@Transactional
	public List<User> getProjectUsers(Project project) throws NoSuchProjectException {
		if (project == null)
			throw new NoSuchProjectException();

		return this.getLogEntryDao(project).getCurrentProjectMembers(project.getUserId());
	}


	@Override
	@Transactional
	public List<UserInfo> getProjectUserInfos(Project project)
			throws NoSuchProjectException {
		if (project == null)
			throw new NoSuchProjectException();

		Collection<User> users = this.getLogEntryDao(project)
				.getCurrentProjectMembers(project.getUserId());

		List<UserInfo> userInfos = new LinkedList<UserInfo>();

		for (User user : users) {
			UserInfo userInfo = getProjectUserInfo(project, user);
			userInfos.add(userInfo);
		}

		return userInfos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserInfo getProjectUserInfo(Project project, User user) {
		TrustState state;
		com.jakeapp.jake.ics.UserId backendUser;
		MsgService msgService;
		ICService ics;
		String nickname = "", firstname = "", lastname = "";
		VisibilityStatus visibilityStatus = VisibilityStatus.OFFLINE;
		UserInfo result = null;
		IStatusService statusService;
		IUsersService usersService;



		//FIXME! do this calculation only after logsync and store it somewhere. 
		msgService = project.getMessageService();
		backendUser = msgService.getIcsManager()
				.getBackendUserId(project, user);
		ics = msgService.getIcsManager().getICService(project);
		statusService = msgService.getMainIcs().getStatusService();
		usersService = ics.getUsersService();
		
		state = this.getLogEntryDao(project).trustsHow(project.getUserId(),
				user);
		
		try {
			firstname = statusService.getFirstname(backendUser);
			lastname = statusService.getLastname(backendUser);
			nickname = usersService.getNickName(backendUser);
		} catch (OtherUserOfflineException e) {
		} catch (NotLoggedInException e) {
		} catch (NetworkException e) {
		}
		try{
			usersService.requestOnlineNotification(backendUser);
			if(statusService.isLoggedIn(backendUser)){
				visibilityStatus = VisibilityStatus.ONLINE;
			}else{
				visibilityStatus = VisibilityStatus.OFFLINE;
			}
		} catch (NotLoggedInException e) {
		} catch (TimeoutException e) {
		} catch (NetworkException e) {
		}
		nickname = backendUser.getUserId();

		result = new UserInfo(state, visibilityStatus, nickname, firstname,
				lastname, user);

		return result;
	}


	@Override
	@Transactional
	public User getLastModifier(JakeObject jakeObject) {
		try {
			return this.getLogEntryDao(jakeObject.getProject())
					.getLastVersionOfJakeObject(jakeObject).getMember();
		} catch (NoSuchLogEntryException e) {
			return null;
		}
	}



	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<User> getSuggestedPeopleForInvite(Project project)
			throws IllegalArgumentException, NoSuchProjectException {

		if (project == null || project.getMessageService() == null)
			throw new NoSuchProjectException();

		MsgService msgService;
		ICService ics;
		IUsersService usersService;
		Iterable<com.jakeapp.jake.ics.UserId> possibleBackendUsers;
		Collection<User> possibleUsers;
		User possibleUser;
		List<User> result = new LinkedList<User>();

		msgService = project.getMessageService();
		// backendUser = msgService.getIcsManager()
		// .getBackendUserId(project, user);
		ics = msgService.getIcsManager().getICService(project);
		usersService = ics.getUsersService();

		try {
			// get all possible users
			log.debug("before usersService.getUsers;status is: " + msgService.getVisibilityStatus());
			possibleBackendUsers = usersService.getAllUsers();

			// convert them to 'frontend-users'
			possibleUsers = new LinkedList<User>();
			for (com.jakeapp.jake.ics.UserId possibleBackendUser : possibleBackendUsers) {
				possibleUser = msgService.getIcsManager().getFrontendUserId(
						project, possibleBackendUser);
				if (possibleUser != null)
					possibleUsers.add(possibleUser);
			}

			// subtract all users that are already in the project
			possibleUsers.removeAll(this.getLogEntryDao(project)
					.getCurrentProjectMembers(project.getUserId()));
			result.addAll(possibleUsers);
		} catch (NotLoggedInException e) {
			// empty handling
			log.debug("Must be online to get uninvited users.",e);
		}

		log.info("getUninvitedPeople returns " + result.size() + " results");

		return result;
	}

	@Override
	@Transactional
	public Set<Tag> getTagsForJakeObject(JakeObject jo) throws IllegalArgumentException,
			NoSuchJakeObjectException {
		try {
			return new TreeSet<Tag>(getTagsListFor(jo));
		} catch (NullPointerException npex) {
			throw new NoSuchJakeObjectException(npex);
		}
	}

	private List<Tag> getTagsListFor(JakeObject jo) {
		return new LinkedList<Tag>(this.getLogEntryDao(jo.getProject())
				.getCurrentTags(jo));
	}

	@Override
	@Transactional
	public void setTagsForJakeObject(JakeObject jo, Set<Tag> tags)
			throws NoSuchJakeObjectException {
		List<Tag> toAdd = new ArrayList<Tag>();
		List<Tag> toRemove = new ArrayList<Tag>();
		List<Tag> current = this.getTagsListFor(jo);

		// calculate which tags to add and remove
		for (Tag t : tags)
			if (!current.contains(t))
				toAdd.add(t);


		for (Tag t : current)
			if (!tags.contains(t))
				toRemove.add(t);

		// remove and add tags
		for (Tag t : toRemove)
			removeTag(jo, t);

		for (Tag t : toAdd)
			addTag(jo, t);
	}

	private Date now() {
		return Calendar.getInstance().getTime();
	}

	private void addTag(JakeObject jo, Tag t) {
		LogEntry<Tag> logEntry;
		t.setObject(jo);
		logEntry = new TagAddLogEntry(t, jo.getProject().getUserId());
		this.getLogEntryDao(jo.getProject()).create(logEntry);
	}

	private void removeTag(JakeObject jo, Tag t) {
		TagRemoveLogEntry logEntry;
		t.setObject(jo);
		logEntry = new TagRemoveLogEntry(t, jo.getProject().getUserId());
		this.getLogEntryDao(jo.getProject()).create(logEntry);
	}

	public IProjectsFileServices getProjectsFileServices() {
		return this.projectsFileServices;
	}

	public void setProjectsFileServices(IProjectsFileServices projectsFileServices) {
		this.projectsFileServices = projectsFileServices;
	}

	@Override
	@Transactional
	public void saveNote(NoteObject no) {
		this.getNoteObjectDao(no.getProject()).persist(no);
	}

	@Override
	@Transactional
	public void deleteNote(NoteObject no) throws IllegalArgumentException {
		no.setDeleted(true);
		this.getNoteObjectDao(no.getProject()).persist(no);
		// christopher: if you want to call persist to delete the object, you have to
		// implement that too! if you don't want to delete the note, you have to correct
		// SyncService et. al. to operate correctly (TestSyncService, etc.)
		try {
			this.getNoteObjectDao(no.getProject()).delete(no);
		} catch (NoSuchJakeObjectException e) {
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	@Transactional
	public AvailableLaterObject<Void> deleteFile(FileObject fo, boolean trash) {
		List<FileObject> fos = new ArrayList<FileObject>();
		fos.add(fo);
		AvailableLaterWrapperObject<Void,Integer> result = 
			new AvailableLaterWrapperObject<Void,Integer>() {
				@Override
				public Void calculate() throws Exception {
					return null; //the entire calculation is done by the source
				}
			};
		result.setSource(this.deleteFiles(fos, trash));
		
		return result;
	}
	
	@Override
	@Transactional
	public AvailableLaterObject<Integer> deleteFiles(List<FileObject> fos, boolean trash) {
		Project project;
		
		if (fos.size()<0)
			return new AvailableNowObject<Integer>(0);
		else {
			project = fos.get(0).getProject(); 
				
			return new DeleteFilesFuture(
				this.getFileObjectDao(project),
				this.getProjectsFileServices().getProjectFSService(project),
				fos,trash
			);
		}
	}
   
	@Override
	@Transactional
	public void lock(JakeObject jo, String comment) {
		Project p = jo.getProject();
        User me = p.getUserId();
        LogEntry<JakeObject> le = new JakeObjectLockLogEntry(jo, me, comment, true);
        this.getApplicationContextFactory().getLogEntryDao(p).create(le);
	}

	@Override
	@Transactional
	public void unlock(JakeObject jo, String comment) {
		Project p = jo.getProject();
        User me = p.getUserId();
        LogEntry<JakeObject> le = new JakeObjectUnlockLogEntry(jo, me, comment, true);
        this.getApplicationContextFactory().getLogEntryDao(p).create(le);
	}

	@Override
	public void setUserNickname(Project project, User user, String nick) {
		// TODO: implement!
		// TODO christopher: I think that this function is not supported
		// by the current database structure
		
	}

	public void setAccountDao(IAccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public IAccountDao getAccountDao() {
		return accountDao;
	}
}