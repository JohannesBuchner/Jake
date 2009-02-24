package com.jakeapp.core.services;

import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.dao.INoteObjectDao;
import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.dao.IServiceCredentialsDao;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import com.jakeapp.core.domain.exceptions.UserIdFormatException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.services.futures.ProjectFileCountFuture;
import com.jakeapp.core.services.futures.ProjectSizeTotalFuture;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.synchronization.RequestHandlePolicy;
import com.jakeapp.core.synchronization.TrustAwareRequestHandlePolicy;
import com.jakeapp.core.synchronization.UserInfo;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.util.UnprocessedBlindLogEntryDaoProxy;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotADirectoryException;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class ProjectsManagingServiceImpl extends JakeService implements
		IProjectsManagingService {

	private static final Logger log = Logger.getLogger(ProjectsManagingServiceImpl.class);

	// private List<Project> projectList = new ArrayList<Project>();

	private IProjectDao projectDao;

	private IServiceCredentialsDao serviceCredentialsDao;

	private IFriendlySyncService syncService;

	private IProjectsFileServices projectsFileServices;

	private MsgServiceFactory msgServiceFactory;

	public ProjectsManagingServiceImpl(
			ProjectApplicationContextFactory applicationContextFactory,
			MsgServiceFactory msgServiceFactory) {
		super(applicationContextFactory);
		this.msgServiceFactory = msgServiceFactory;
	}

	public IFriendlySyncService getSyncService() {
		return this.syncService;
	}

	public void setSyncService(IFriendlySyncService syncService) {
		this.syncService = syncService;
	}

	/**
	 * *********** GETTERS & SETTERS ************
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

	/**
	 * ***** STARTING IMPLEMENTATIONS *************
	 */
	@Transactional
	@Override
	public List<Project> getProjectList() {
		List<Project> result;
		log.debug("calling ProjectsManagingServiceImpl.getProjectList() ");

		result = this.getProjectDao().getAll();
		if (result != null) {
			checkProjects(result);
			log.debug("found " + result.size() + " projects to return");
			return result;
		} else {
			log.warn("didn't get any results!!!!!from ProjectDao.getAll");
			return Collections.emptyList();
		}
	}

	private void checkProjects(List<Project> result) {
		for (Project p : result) {
			log.debug("getProjectList gave us a project with credentials:"
					+ p.getCredentials());
			try {
				initProject(p);
			} catch (NoSuchProjectException e) {
				log.error("invalid project: ", e);
				result.remove(p);
			} catch (NotADirectoryException e) {
				log.error("invalid project: ", e);
				result.remove(p);
			}
		}
	}

	@Transactional
	@Override
	public List<Project> getProjectList(InvitationState state) {
		log.debug("calling getProjectList");
		List<Project> result = this.getProjectDao().getAll(state);
		checkProjects(result);
		return result;
	}

	private void initProject(Project p) throws NoSuchProjectException, NotADirectoryException {
		log.debug("initialising project " + p);

		if (p.getCredentials() == null) {
			log.warn("fixing null credentials (bug workaround) ");
			try {
				// workaround until many-to-one works (bug 46)
				UserId user = new UserId(ProtocolType.XMPP, "testuser1@localhost");
				ServiceCredentials credentials = this.getServiceCredentialsDao().getAll()
						.get(0);
				credentials.setProtocol(ProtocolType.XMPP);
				credentials.setSavePassword(true);
				MsgService<UserId> msg = this.msgServiceFactory
						.createMsgService(credentials);
				p.setMessageService(msg);
				p.setCredentials(credentials);
				this.projectDao.update(p);
			} catch (ProtocolNotSupportedException e) {
			}
		}

		log.debug("Init Project: " + p + " with credentials: " + p.getCredentials());
		if (p.getMessageService() == null)
			p.setMessageService(msgServiceFactory.getByCredentials(p.getCredentials()));

		// make sure the projects have fileservices
		try {
			this.getProjectsFileServices().startForProject(p);
		} catch (IOException e) {
			log.debug("starting fss failed", e);
		}

		if (!p.getUserId().equals(p.getMessageService().getUserId()))
			throw new IllegalStateException();
		log.debug("initialising project done for " + p);
	}


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
			UserId user = project.getMessageService().getUserId();
			this.getLogEntryDao(project).create(new ProjectLogEntry(project, user));
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
	public boolean startProject(Project project, ChangeListener cl)
			throws IllegalArgumentException, FileNotFoundException, ProjectException {
		log.info("start project: " + project);

		// Check preconditions
		if (project == null)
			throw new IllegalArgumentException("project is null!");
		if (!project.isOpen() || project.isStarted()) {
			log.warn("Attemted to start a project that's not open/already started: "
					+ project);
			return false;
		}
		

		try {
			this.getFileServices(project).setRootPath(project.getRootPath());
			this.getProjectsFileServices().startForProject(project);
			/*TODO call me! syncService.startServing(
				project,
				new TrustAwareRequestHandlePolicy(this.getApplicationContextFactory(), this.getProjectsFileServices()),
				null/* new ChangeListener()}
			);*/
			project.setStarted(true);
		} catch (Exception e) {
			throw new ProjectException(e);
		}

		return true;
	}

	@Override
	public boolean stopProject(Project project) throws IllegalArgumentException,
			FileNotFoundException {
		// Check preconditions
		if (project == null)
			throw new IllegalArgumentException();
		if (!project.isOpen() || !project.isStarted())
			return false;

		
		try {
			syncService.stopServing(project);
			// stops monitoring the project
			this.getProjectsFileServices().stopForProject(project);
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

		File rootPath = new File(project.getRootPath());

		
		// add Project to the global database
		try {
			project = this.getProjectDao().create(project);
		} catch (InvalidProjectException e) {
			log.error("Opening a project failed: Project was invalid");
			throw new IllegalArgumentException();
		}
		
		// add the project's file services
		this.getProjectsFileServices().startForProject(project);

		project.setOpen(true);

		return project;
	}

	@Override
	public boolean deleteProject(Project project, boolean deleteProjectFiles)
			throws IllegalArgumentException, SecurityException, IOException, NotADirectoryException {
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
		UnprocessedBlindLogEntryDaoProxy dao;
		List<LogEntry<? extends com.jakeapp.core.domain.ILogable>> result = null;

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
	 * @Override public void assignUserToProject(Project project, UserId userId)
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
	 * // set UserId as ProjectMember this.addUserToProject(project, userId);
	 * this.setTrust(project, userId, TrustState.AUTO_ADD_REMOVE); }
	 */

	/**
	 * Adds a new Projectmember to a Project. It may not exist in the Project
	 * yet.
	 * 
	 * @param project
	 *            Project to add a member to. Must not be null.
	 * @param userId
	 *            Member to add. Must not be null.
	 * @return the ProjectMember added
	 */
	@Transactional
	private UserId addUserToProject(Project project, UserId userId) {
		// TODO: @JOHANNES startTrusting
		return userId;
	}


	@Override
	@Transactional
	public void setTrust(Project project, UserId userId, TrustState trust)
			throws IllegalArgumentException, IllegalAccessException {
		// TODO: @JOHANNES use startTrusting, stopStrusting
	}

	/**
	 * sends an Invitation to another ProjectMember
	 * 
	 * @param project
	 * @param userId
	 */
	// TODO: why is this private? not needed?
	private void inviteMember(Project project, UserId userId) {
		if (project.getUserId() == null) {
			throw new IllegalArgumentException(
					"Project needs a UserId before a member can be invited.");
		}

		// this is perfectly ok, invite is called on ourself in setTrust
		// (assignUserToProject)
		if (project.getUserId().equals(userId)) {
			log.info("Not inviting myself.");
		} else {
			this.getSyncService().invite(project, userId);
		}
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

		log.debug("\n\n\n\n\n\n\t\tCalling getProjectFileCount");


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

		log.debug("\n\n\n\n\t\tCalling getProjectSizeTotal");


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

		return getApplicationContextFactory().getAllProjectFilesFuture(project);
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
	public UserId getLastEditor(JakeObject jo) throws NoSuchProjectException,
			IllegalArgumentException {
		LogEntry<? extends ILogable> logentry;
		UserId result;

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
	public List<UserId> getProjectUsers(Project project) throws NoSuchProjectException {
		if (project == null)
			throw new NoSuchProjectException();

		return this.getLogEntryDao(project).getCurrentProjectMembers();
	}


	@Override
	@Transactional
	public List<UserInfo> getProjectUserInfos(Project project)
			throws NoSuchProjectException {
		if (project == null)
			throw new NoSuchProjectException();

		Collection<UserId> users = this.getLogEntryDao(project)
				.getCurrentProjectMembers();

		List<UserInfo> userInfos = new LinkedList<UserInfo>();

		for (UserId user : users) {
			UserInfo userInfo = getProjectUserInfo(project, user);
			userInfos.add(userInfo);
		}

		return userInfos;
	}

	@Override
	public UserInfo getProjectUserInfo(Project project, UserId user) {
		TrustState state = this.getLogEntryDao(project).trustsHow(project.getUserId(),
				user);

		// TODO: fill in with useful data
		return new UserInfo(state, VisibilityStatus.ONLINE, "Nick", "First", "Last", user);
	}


	@Override
	@Transactional
	public UserId getLastModifier(JakeObject jakeObject) {
		try {
			return this.getLogEntryDao(jakeObject.getProject())
					.getLastVersionOfJakeObject(jakeObject).getMember();
		} catch (NoSuchLogEntryException e) {
			return null;
		}
	}

	@Override
	@Transactional
	public UserId invite(Project project, String userid) throws UserIdFormatException {
		UserId member;
		UserId id;

		log.info("invite project: " + project + " userid: " + userid + " msgservice: "
				+ project.getMessageService());

		id = project.getMessageService().getUserId(userid);
		log.debug("extracted the userid to invite: " + userid);

		member = this.addUserToProject(project, id);
		this.getSyncService().invite(project, id);

		return member;
	}

	@Override
	@Transactional
	public List<UserId> getUninvitedPeople(Project project)
			throws IllegalArgumentException, NoSuchProjectException {
		// TODO
		return new LinkedList<UserId>();
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
		logEntry = new TagLogEntry(LogAction.TAG_ADD, t, jo.getProject().getUserId());
		this.getLogEntryDao(jo.getProject()).create(logEntry);
	}

	private void removeTag(JakeObject jo, Tag t) {
		TagLogEntry logEntry;
		t.setObject(jo);
		logEntry = new TagLogEntry(LogAction.TAG_REMOVE, t, jo.getProject().getUserId());
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
	public void deleteNote(NoteObject no) throws IllegalArgumentException,
			NoSuchJakeObjectException {
		this.getNoteObjectDao(no.getProject()).delete(no);
	}

	@Override
	@Transactional
	public void lock(JakeObject jo, String comment) {
		this.locking(jo, comment, LogAction.JAKE_OBJECT_LOCK);
	}

	@Override
	@Transactional
	public void unlock(JakeObject jo, String comment) {
		this.locking(jo, comment, LogAction.JAKE_OBJECT_UNLOCK);
	}

	@Override
	public void setUserNickname(Project project, UserId userId, String nick) {
		// TODO: implement!
	}

	@Override
	public void setUserTrustState(Project project, UserId userId, TrustState trust) {
		// TODO: implement!
	}

	@Transactional
	private void locking(JakeObject jo, String comment, LogAction which) {
		Project p = jo.getProject();
		UserId me = p.getUserId();
		LogEntry<JakeObject> le = new JakeObjectLogEntry(which, jo, me, comment, null,
				true);
		this.getApplicationContextFactory().getLogEntryDao(p).create(le);
	}

	public void setServiceCredentialsDao(IServiceCredentialsDao serviceCredentialsDao) {
		this.serviceCredentialsDao = serviceCredentialsDao;
	}

	public IServiceCredentialsDao getServiceCredentialsDao() {
		return serviceCredentialsDao;
	}
}