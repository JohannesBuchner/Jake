package com.jakeapp.core.services;

import com.jakeapp.core.dao.*;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.domain.exceptions.UserIdFormatException;
import com.jakeapp.core.services.futures.AllProjectFilesFuture;
import com.jakeapp.core.services.futures.ProjectFileCountFuture;
import com.jakeapp.core.services.futures.ProjectSizeTotalFuture;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class ProjectsManagingServiceImpl implements IProjectsManagingService {

	private static final Logger log = Logger
			  .getLogger(ProjectsManagingServiceImpl.class);

	//private List<Project> projectList = new ArrayList<Project>();

	private ProjectApplicationContextFactory applicationContextFactory;

	private IProjectDao projectDao;

	private IFriendlySyncService syncService;

	private IProjectsFileServices projectsFileServices;


	private INoteManagingService noteManagingService;

	private MsgService messageService;

	public ProjectsManagingServiceImpl() {

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

	public IProjectDao getProjectDao() {
		return projectDao;
	}

	public void setProjectDao(IProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	public ProjectApplicationContextFactory getApplicationContextFactory() {
		return applicationContextFactory;
	}

	public void setApplicationContextFactory(
			  ProjectApplicationContextFactory applicationContextFactory) {
		this.applicationContextFactory = applicationContextFactory;
	}

	@Override
	public IFSService getFileServices(Project p) throws ProjectNotLoadedException {
		IFSService result = null;
		ProjectNotLoadedException ex = null;

		try {
			result = this.getProjectsFileServices().getProjectFSService(p);
		}
		catch (ProjectNotLoadedException pnle) {
			ex = pnle;
		}

		//FIXMgetProjectE prevents some ugly Exceptions - remove after release
		if (result == null) {
			if (p.isOpen()) result = this.getProjectsFileServices().startProject(p);
			else throw ex;
		}

		return result;
	}

	/*
		 private List<Project> getInternalProjectList() {
			  return this.projectList;
		 }
		 */

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

	private IJakeObjectDao getJakeObjectDao(final Project project, final JakeObject jo) {
		IJakeObjectDao result = null;

		if (jo != null) {
			if (jo instanceof FileObject) result = (IJakeObjectDao) this.getFileObjectDao(project);
			else if (jo instanceof NoteObject) result = (IJakeObjectDao) this.getNoteObjectDao(project);
		}

		return result;
	}

	@Override
	public void setMsgService(MsgService service) {
		this.messageService = service;
	}

	private MsgService getMsgService() {
		return this.messageService;
	}


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

			for (Project p : result)
				initProject(p);

			log.debug("found " + result.size() + " projects to return");
			return result;
		}
		log.warn("didn't get any results!!!!!");
		throw new RuntimeException(" this should return an empty list");
		// return Collections.unmodifiableList(this.getInternalProjectList());
	}

	@Transactional
	@Override
	public List<Project> getProjectList(InvitationState state) {
		List<Project> result = this.getProjectDao().getAll(state);

		for (Project p : result)
			initProject(p);

		return result;
	}

	private void initProject(Project p) {
		p.setMessageService(this.getMsgService());
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


		// add Project to the global database
		try {
			project = this.getProjectDao().create(project);
		} catch (InvalidProjectException e) {
			log.error("Opening a project failed: Project was invalid");
			throw new IllegalArgumentException();
		}


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
		this.openProject(project);


		// create an applicationContext for the Project
		// this.applicationContextFactory.getApplicationContext(project);


		project.setMessageService(msgService);


		// TODO maybe move this to a extra method or remove it completly
		{
			INoteObjectDao noteObjectDao = this.applicationContextFactory.getNoteObjectDao(project);
			List<NoteObject> notesList = new ArrayList<NoteObject>();


			notesList.add(new NoteObject(new UUID(1, 1), project, "If you have five dollars and Chuck Norris has five dollars, Chuck Norris has more money than you"));
			notesList.add(new NoteObject(new UUID(2, 1), project, "Apple pays Chuck Norris 99 cents every time he listens to a song."));
			notesList.add(new NoteObject(new UUID(3, 1), project, "Chuck Norris is suing Myspace for taking the name of what he calls everything around you."));
			notesList.add(new NoteObject(new UUID(4, 1), project, "Chuck Norris destroyed the periodic table, because he only recognizes the element of surprise."));
			notesList.add(new NoteObject(new UUID(5, 1), project, "The leading causes of death in the United States are: 1. Heart Disease 2. Chuck Norris 3. Cancer."));
			notesList.add(new NoteObject(new UUID(6, 1), project, "Chuck Norris does not sleep. He waits."));
			notesList.add(new NoteObject(new UUID(7, 1), project, "There is no theory of evolution. Just a list of animals Chuck Norris allows to live. "));
			notesList.add(new NoteObject(new UUID(8, 1), project, "Guns don't kill people, Chuck Norris does."));
			notesList.add(new NoteObject(new UUID(9, 1), project, "Chuck Norris does not need an undo function"));
			notesList.add(new NoteObject(new UUID(10, 1), project, "Chuck Norris can kill two stones with one bird."));
			notesList.add(new NoteObject(new UUID(11, 1), project, "Chuck Norris knows: Jake is the best file-sharing app ever."));


			for (NoteObject note : notesList) {
				noteObjectDao.persist(note);
			}
		}


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
	}


	@Override
	public boolean startProject(Project project, ChangeListener cl)
			  throws IllegalArgumentException, FileNotFoundException,
			  ProjectException {
		log.info("start project: " + project + "project id: " + project.getProjectId());

		// Check preconditions
		if (project == null)
			throw new IllegalArgumentException();
		if (!project.isOpen() || project.isStarted())
			return false;

		log.debug("Userid of Project that is about to be started is: " + project.getUserId());

		// TODO: enable sync! (DEMO HACK)
		//this.syncService.startServing(project,
		//		  new TrustRequestHandlePolicy(project), cl);

		project.setStarted(true);

		try {
			this.getFileServices(project).setRootPath(project.getRootPath());
		} catch (Exception e) {
			throw new ProjectException(e);
		}


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
			this.getProjectsFileServices().getProjectFSService(project).unsetRootPath();

		}

		catch (ProjectNotLoadedException e) {
			// this is ok!
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
		//this.getInternalProjectList().remove(project);
		// remove the project's file services

		this.getProjectsFileServices().stopProject(project);

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
	public Project openProject(Project project)
			  throws IllegalArgumentException, FileNotFoundException {

		File rootPath = new File(project.getRootPath());

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
//		result = new Project(name, UUID.randomUUID(), null, // msgService
//				  rootPath);

		// Check if the Project-internal Database can be read (and read
		// it!)
		//if (!this.checkProjectDatabaseExists(result))
		//	throw new FileNotFoundException();


		// add the project's file services
		this.getProjectsFileServices().startProject(project);

		// add project to internal list
		//this.getInternalProjectList().add(project);

		project.setOpen(true);

		return project;
	}

	@Override
	public boolean deleteProject(Project project, boolean deleteProjectFiles)
			  throws IllegalArgumentException, SecurityException,
			  FileNotFoundException {
		boolean result = true;
		IFSService fss;
		FileNotFoundException t = null;

		// Check preconditions
		if (project == null)
			throw new IllegalArgumentException();

		// Remove the Project's root folder
		if (deleteProjectFiles) {
			log.debug("trashing project files: " + project.getRootPath());
			fss = this.getProjectsFileServices().startProject(project);
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
			this.getProjectsFileServices().stopProject(project);
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
		log.debug("Adding ProjectMember to Project: " + project + ';' + userId);

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
	public AvailableLaterObject<List<FileObject>> getAllProjectFiles(Project project, AvailabilityListener listener)
			  throws NoSuchProjectException, FileNotFoundException, IllegalArgumentException {
		return new AllProjectFilesFuture(listener, applicationContextFactory, project);
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
		log.info("getprojectmember: project: " + project + "msgservice: " + msg);
		return this.getProjectMember(project, msg.getUserId(), this.getProjectMemberDao(project));
	}

	@Override
	public String getProjectMemberID(Project project, ProjectMember pm) {
		//TODO it is completely unclear how to do that!! Dominik, please help us!
		return "";
	}


//    @Override
//    public INoteManagingService getNoteManagingService() {
//
//
//        INoteObjectDao dao;
//
//        // preconditions
//        if (p == null)
//            throw new IllegalArgumentException();
//        if (!this.isProjectLoaded(p)) {
//            throw new ProjectNotLoadedException("Project with id "
//                    + p.getProjectId() + " is not loaded");
//        }
//
//        //TODO cache
//        return new NoteManagingService(this, this.getNoteObjectDao(p), this.getLogEntryDao(p));
//    }


	@Override
	@Transactional
	public List<ProjectMember> getProjectMembers(Project project) throws NoSuchProjectException {
		if (project == null) throw new NoSuchProjectException();

		return this.getProjectMemberDao(project).getAll(project);
	}


	@Override
	@Transactional
	public void updateProjectMember(Project project, ProjectMember member) {
		this.getProjectMemberDao(project).persist(project, member);

	}

	@Override
	@Transactional
	public boolean isLocalJakeObject(JakeObject jo) {
		boolean result = false;

		try {
			// log.debug("Checking isLocalJakeObject for jo " + jo + " with project " + jo.getProject());


			this.getLogEntryDao(jo.getProject()).getMostRecentFor(jo);
		} catch (NoSuchLogEntryException e) {
			/*
																								* There is no Logentry for this note. Therefore it has never been
																								* announced and is only local.
																								*/
			result = true;
		}

		return result;
	}

	@Override
	@Transactional
	public ProjectMember getLastModifier(JakeObject jakeObject) {
		List<LogEntry<JakeObject>> entries;
		ArrayList<LogAction> actions = new ArrayList<LogAction>();

		actions.add(LogAction.JAKE_OBJECT_DELETE);
		actions.add(LogAction.JAKE_OBJECT_NEW_VERSION);

		entries = this.getLogEntryDao(jakeObject.getProject()).getAllOfJakeObject(jakeObject, actions);
		if (entries.size() > 0)
			return entries.get(0).getMember();
		else return null;
	}

	@Override
	@Transactional
	public ProjectMember invite(Project project, String userid) throws UserIdFormatException {
		ProjectMember member;
		UserId id;


		log.info("invite project: " + project + " userid: " +
				  userid + " msgservice: " + project.getMessageService());


		/* FIXME Hack. There is not reason why the MsgService is not set already. */
		if (project.getMessageService() == null) project.setMessageService(this.getMsgService());

		id = project.getMessageService().getUserId(userid);
		log.debug("extracted the userid to invite: " + userid + userid != null);

		member = this.addProjectMember(project, id);
		this.getSyncService().invite(project, id);

		return member;
	}

	@Override
	@Transactional
	public List<ProjectMember> getUninvitedPeople(Project project)
			  throws IllegalArgumentException, NoSuchProjectException {
		List<UserId> others;
		List<UserId> othersUserIds = new ArrayList<UserId>();
		List<ProjectMember> otherMembers;
		List<ProjectMember> result = new ArrayList<ProjectMember>();
		ProjectMember pm;
		// Set with ProjectMembers with unique Nicknames
		SortedSet<ProjectMember> otherMembersSet = new TreeSet<ProjectMember>(
				  new Comparator<ProjectMember>() {

					  @Override
					  public int compare(ProjectMember o1, ProjectMember o2) {
						  int result;

						  if (o1 == null)
							  result = (o2 == null) ? 0 : -1;
						  else if (o2 == null)
							  result = 1;
						  else
							  result = o1.getNickname().compareTo(
										 o2.getNickname());

						  return result;
					  }
				  });


		// preconditions
		if (!this.isProjectLoaded(project))
			throw new NoSuchProjectException("Project not loaded!");
		if (project.getMessageService() == null)
			throw new IllegalArgumentException();


		// get all other users
		others = project.getMessageService().getUserList();
		for (Object o : others)
			if (o instanceof UserId)
				othersUserIds.add((UserId) o);

		otherMembers = this.getProjectMemberDao(project).getAll(project);
		// get unique nicknames
		otherMembersSet.addAll(otherMembers);

		for (UserId uid : othersUserIds) {
			// only people who are currently not in the project are returned
			pm = new ProjectMember(null, uid.getNickname(), TrustState.NO_TRUST);
			if (this.isFriend(project, uid) // only friends are returned
					  && !otherMembersSet.contains(pm)) // only people who are
				// currently not in the
				// project are returned
				result.add(pm);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private boolean isFriend(Project project, UserId uid) {
		return project.getMessageService().checkFriends(uid);
	}

	@Override
	@Transactional
	public Set<Tag> getTagsForJakeObject(JakeObject jo) throws IllegalArgumentException, NoSuchJakeObjectException {
		try {
			return new TreeSet<Tag>(getTagsListFor(jo));
		}
		catch (NullPointerException npex) {
			throw new NoSuchJakeObjectException(npex);
		}
	}

	@SuppressWarnings("unchecked")
	private List<Tag> getTagsListFor(JakeObject jo) throws NoSuchJakeObjectException {
		return this.getJakeObjectDao(jo.getProject(), jo).getTagsFor(jo);
	}

	@Override
	@Transactional
	public void setTagsForJakeObject(JakeObject jo, Set<Tag> tags) throws NoSuchJakeObjectException {
		//TODO calc the real checksum?
		final String CHECKSUM = "";

		List<Tag> toAdd = new ArrayList<Tag>();
		List<Tag> toRemove = new ArrayList<Tag>();
		List<Tag> current = this.getTagsListFor(jo);
		List<Tag> passedTags = new ArrayList<Tag>(tags);
		TagLogEntry logEntry;
		IJakeObjectDao dao = this.getJakeObjectDao(jo.getProject(), jo);

		//calculate which tags to add and remove
		for (Tag t : tags)
			if (!current.contains(t))
				toAdd.add(t);

		for (Tag t : current)
			if (!tags.contains(t))
				toRemove.add(t);

		//remove and add tags
		for (Tag t : toRemove)
			removeTag(jo, CHECKSUM, dao, t);

		for (Tag t : toAdd)
			addTag(jo, CHECKSUM, dao, t);
	}

	private void addTag(JakeObject jo, final String CHECKSUM,
							  IJakeObjectDao dao, Tag t) throws NoSuchJakeObjectException {
		TagLogEntry logEntry;
		dao.addTagTo(jo, t);
		logEntry = new TagLogEntry(UUID.randomUUID(), LogAction.TAG_ADD, Calendar.getInstance().getTime(), jo.getProject(),
				  t, this.getProjectMember(jo.getProject(), jo.getProject().getMessageService()), "", CHECKSUM, true);
	}

	private void removeTag(JakeObject jo, final String CHECKSUM,
								  IJakeObjectDao dao, Tag t) throws NoSuchJakeObjectException {
		TagLogEntry logEntry;
		dao.removeTagFrom(jo, t);
		logEntry = new TagLogEntry(UUID.randomUUID(), LogAction.TAG_REMOVE, Calendar.getInstance().getTime(), jo.getProject(),
				  t, this.getProjectMember(jo.getProject(), jo.getProject().getMessageService()), "", CHECKSUM, true);
	}

	public IProjectsFileServices getProjectsFileServices() {
		return projectsFileServices;
	}

	public void setProjectsFileServices(IProjectsFileServices projectsFileServices) {
		this.projectsFileServices = projectsFileServices;
	}

	public INoteManagingService getNoteManagingService() {
		return noteManagingService;
	}

	public void setNoteManagingService(INoteManagingService noteManagingService) {
		this.noteManagingService = noteManagingService;
	}
}
