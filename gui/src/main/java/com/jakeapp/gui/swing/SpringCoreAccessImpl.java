package com.jakeapp.gui.swing;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.domain.exceptions.UserIdFormatException;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.services.futures.AnnounceFuture;
import com.jakeapp.core.services.futures.PullFuture;
import com.jakeapp.core.synchronization.AttributedJakeObject;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.synchronization.ISyncService;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.util.availablelater.AvailableErrorObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.callbacks.*;
import com.jakeapp.gui.swing.callbacks.ErrorCallback.JakeErrorEvent;
import com.jakeapp.gui.swing.callbacks.ProjectChanged.ProjectChangedEvent.ProjectChangedReason;
import com.jakeapp.gui.swing.exceptions.*;
import com.jakeapp.gui.swing.helpers.FolderObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.IModificationListener.ModifyActions;
import com.jakeapp.jake.fss.IProjectModificationListener;
import com.jakeapp.jake.fss.exceptions.*;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class SpringCoreAccessImpl implements ICoreAccess {

	private static final Logger log = Logger.getLogger(SpringCoreAccessImpl.class);
	private IFrontendService frontendService;

	private Set<JakeObject> MockIsSoftLockedSet;

	// HACK INIT MOCK CORE
	//private CoreAccessMock coreMock = new CoreAccessMock();

	{
		this.MockIsSoftLockedSet = new HashSet<JakeObject>();
		Set<ProjectMember> onlineList = new HashSet<ProjectMember>();
	}

	/**
	 * Checks if MOCK should be used instead of the real thing
	 *
	 * @param f
	 * @return
	 */
	// HACK
/*	private boolean useMock(String f) {

		if ("getNotes".compareTo(f) == 0) return false;
		else if ("getProjectRootFolder".compareTo(f) == 0
				  || "getAllProjectFiles".compareTo(f) == 0) return true;
		else if ("getLog".compareTo(f) == 0) return true;
		else if ("getSuggestedPeople".compareTo(f) == 0) return true;
		else if ("getPeople".compareTo(f) == 0) return false;
		else if ("getJakeObjectSyncStatus".compareTo(f) == 0) return true;

			// default
		else return false;
	}*/

	/**
	 * SessionId returned by the authentication Method of
	 * FrontendService.authenticate.
	 */
	private String sessionId;

	private Map<FilesChanged, IProjectModificationListener> fileListeners = new HashMap<FilesChanged, IProjectModificationListener>();

	/**
	 * Core Access Mock initialisation code
	 */
	public SpringCoreAccessImpl() {
		connectionStatus = new ArrayList<ConnectionStatus>();
		registrationStatus = new ArrayList<RegistrationStatus>();
		projectChanged = new ArrayList<ProjectChanged>();
		errorCallback = new ArrayList<ErrorCallback>();
	}

	private IFrontendService getFrontendService() {
		return this.frontendService;
	}

	private String getSessionId() {
		return this.sessionId;
	}

	@Override
	public List<Project> getMyProjects() throws FrontendNotLoggedInException {

		// HACK: refactor, should be implicit!
		for (Project p : frontendService.getProjectsManagingService(sessionId)
						.getProjectList(InvitationState.ACCEPTED)) {
			log.info("Opening project: " + p);
			if (!p.isOpen()) {
				p.setOpen(true);
			}
		}

		return frontendService.getProjectsManagingService(sessionId)
						.getProjectList(InvitationState.ACCEPTED);
	}

	@Override
	public List<Project> getInvitedProjects() throws FrontendNotLoggedInException {
		return frontendService.getProjectsManagingService(sessionId)
						.getProjectList(InvitationState.INVITED);
	}


	@Override
	public void setFrontendService(IFrontendService frontendService) {
		this.frontendService = frontendService;
	}

	private void handleNotLoggedInException(FrontendNotLoggedInException e) {
		log.debug("Tried access core without a session", e);
	}

	@Override
	public void authenticateOnBackend(Map<String, String> authenticationData)
					throws InvalidCredentialsException {
		this.sessionId = this.frontendService.authenticate(authenticationData);
	}

	@Override
	public void backendLogOff() {
		try {
			this.frontendService.logout(this.sessionId);
		} catch (FrontendNotLoggedInException e) {
			// silently ignore this exception. may be removed from
			// frontendservice in the future

		}
		this.sessionId = "";
	}

	@Override
	public List<MsgService> getMsgServics() throws FrontendNotLoggedInException {
		return this.frontendService.getMsgServices(this.sessionId);
	}

	@Override
	public ProjectMember getProjectMember(Project project, MsgService msg)
					throws NoSuchProjectMemberException {
		ProjectMember result = null;

		try {
			result = this.getFrontendService()
							.getProjectsManagingService(this.getSessionId())
							.getProjectMember(project, msg);
		} catch (IllegalArgumentException e) {
			throw new NoSuchProjectMemberException(msg.getUserId().getUserId());
		} catch (IllegalStateException e) {
			throw new NoSuchProjectMemberException(msg.getUserId().getUserId());
		} catch (FrontendNotLoggedInException e) {
			throw new NoSuchProjectMemberException(msg.getUserId().getUserId());
		}
		return result;
	}

	@Override
	public String getProjectMemberID(Project project, ProjectMember pm)
					throws NoSuchProjectMemberException {
		String result = "";

		try {
			result = this.getFrontendService()
							.getProjectsManagingService(this.getSessionId())
							.getProjectMemberID(project, pm);
		} catch (IllegalArgumentException e) {
			throw new NoSuchProjectMemberException("");
		} catch (IllegalStateException e) {
			throw new NoSuchProjectMemberException("");
		} catch (FrontendNotLoggedInException e) {
			throw new NoSuchProjectMemberException("");
		}
		return result;
	}

	public void addErrorListener(ErrorCallback ec) {
		errorCallback.add(ec);
	}

	public void removeErrorListener(ErrorCallback ec) {
		errorCallback.remove(ec);
	}

	private void fireErrorListener(ErrorCallback.JakeErrorEvent ee) {
		for (ErrorCallback callback : errorCallback) {
			callback.reportError(ee);
		}
	}

	public void addConnectionStatusCallbackListener(ConnectionStatus cb) {
		log.info("Registers connection status callback: " + cb);

		connectionStatus.add(cb);
	}

	public void removeConnectionStatusCallbackListener(ConnectionStatus cb) {
		log.info("Deregisters connection status callback: " + cb);

		connectionStatus.remove(cb);
	}


	private void fireConnectionStatus(ConnectionStatus.ConnectionStati state,
																		String str) {
		log.info("spead callback event...");
		for (ConnectionStatus callback : connectionStatus) {
			callback.setConnectionStatus(state, str);
		}
	}

	@Override
	public AvailableLaterObject<Void> createAccount(ServiceCredentials credentials)
					throws FrontendNotLoggedInException, InvalidCredentialsException,
								 ProtocolNotSupportedException, NetworkException {
		return this.frontendService.createAccount(this.sessionId, credentials).start();


	}

	@Override
	public MsgService addAccount(ServiceCredentials credentials)
					throws FrontendNotLoggedInException, InvalidCredentialsException,
								 ProtocolNotSupportedException, NetworkException {
		return this.frontendService.addAccount(this.sessionId, credentials);
	}

	@Override
	public void removeAccount(MsgService msg)
					throws FrontendNotLoggedInException, InvalidCredentialsException,
								 ProtocolNotSupportedException, NetworkException {
		log.warn("removeAccount: " + msg + " NOT IMPLEMENTED YET");
		//TODO
	}

	public void addRegistrationStatusCallbackListener(RegistrationStatus cb) {
		log.info("Registers registration status callback: " + cb);
	}

	public void removeRegistrationStatusCallbackListener(RegistrationStatus cb) {
		log.info("Deregisters registration status callback: " + cb);
	}


	public void addProjectChangedCallbackListener(ProjectChanged cb) {
		//log.info("Register project changed callback: " + cb);

		projectChanged.add(cb);
	}

	public void removeProjectChangedCallbackListener(ProjectChanged cb) {
		log.info("Deregister project changed callback: " + cb);

		if (projectChanged.contains(cb)) {
			projectChanged.remove(cb);
		}
	}

	private void fireProjectChanged(ProjectChanged.ProjectChangedEvent ev) {
		for (ProjectChanged callback : projectChanged) {
			callback.projectChanged(ev);
		}
	}

	public void stopProject(Project project) {
		log.info("stop project: " + project);

		try {
			this.getFrontendService().getProjectsManagingService(this.getSessionId())
							.stopProject(project);
		} catch (IllegalArgumentException e) {
			log.debug("Illegal project for stopping specified.", e);
		} catch (FileNotFoundException e) {
			log.warn("Project-Folder not found.", e);
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (IllegalStateException e) {
			log.debug("Cannot access ProjectManagingService.", e);
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		}

		// generate event
		fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
						ProjectChanged.ProjectChangedEvent.ProjectChangedReason.State));
	}

	public void startProject(Project project) {

		// HACK: create ChangeListener and add to

		try {

			ChangeListener syncChangeListener = new ChangeListener() {

				@Override
				public INegotiationSuccessListener beganRequest(JakeObject jo) {
					return null;
				}

				@Override
				public void pullNegotiationDone(JakeObject jo) {
				}

				@Override
				public void pullDone(JakeObject jo) {
				}

				@Override
				public void pullProgressUpdate(JakeObject jo, Status status,
																			 double progress) {
					log.info(
									"pullProgressUpdate: " + jo + "status: " + status + " progress: " + progress);
				}
			};

			this.getFrontendService().getProjectsManagingService(this.getSessionId())
							.startProject(project, syncChangeListener);
		} catch (IllegalArgumentException e) {
			log.debug("Illegal project for starting specified.", e);
		} catch (FileNotFoundException e) {
			log.warn("Project-Folder not found.", e);
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (IllegalStateException e) {
			log.debug("Cannot access ProjectManagingService.", e);
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		} catch (ProjectException e) {
			log.warn("Generic Project Exception", e);
		}

		// generate event
		fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
						ProjectChanged.ProjectChangedEvent.ProjectChangedReason.State));
	}


	public AvailableLaterObject<Integer> getProjectFileCount(Project project) {
		AvailableLaterObject<Integer> result = null;
		Exception ex = null;

		try {
			result = this.getFrontendService()
							.getProjectsManagingService(this.getSessionId())
							.getProjectFileCount(project);
		} catch (FileNotFoundException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
			ex = e;
		} catch (IllegalArgumentException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
			ex = e;
		} catch (IllegalStateException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
			ex = e;
		} catch (NoSuchProjectException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
			ex = e;
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
			ex = e;
		}

		if (result == null) result = new AvailableErrorObject<Integer>(ex);
		return result.start();
	}

	public AvailableLaterObject<Long> getProjectSizeTotal(Project project) {
		AvailableLaterObject<Long> result = null;
		Exception ex = null;

		try {
			result = this.getFrontendService()
							.getProjectsManagingService(this.getSessionId())
							.getProjectSizeTotal(project);
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
			ex = e;
		} catch (Exception e) {
			ex = e;
		}

		if (result == null) result = new AvailableErrorObject<Long>(ex);
		return result.start();
	}


	public void deleteProject(final Project project,
														final boolean deleteProjectFiles) {
		log.info(
						"Delete project: " + project + " deleteProjectFiles: " + deleteProjectFiles);

		if (project == null) {
			throw new IllegalArgumentException("Cannot delete empty project!");
		}


		Runnable runner = new Runnable() {

			public void run() {
				boolean ret;

				try {
					// search project in list
					ret = getFrontendService().getProjectsManagingService(getSessionId())
									.deleteProject(project, deleteProjectFiles);

					if (!ret) {
						throw new ProjectNotFoundException("Project not found in list!");
					}

					fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
									ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Deleted));

				} catch (FrontendNotLoggedInException e) {
					handleNotLoggedInException(e);
				} catch (RuntimeException run) {
					fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
				} catch (FileNotFoundException e) {
					//report to gui that the rootpath is invalid
					log.warn("Project cannot be deleted: its folder does not exist.", e);
					fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
				}
			}
		};

		// start our runner thread, that makes a callback to project status
		new Thread(runner).start();
	}

	public void joinProject(final String path, final Project project) {
		log.info("Join project: " + project + " path: " + path);

		if (path == null) throw new IllegalArgumentException();

		Runnable runner = new Runnable() {

			public void run() {
				try {
					project.setRootPath(path);
					getFrontendService().getProjectsManagingService(getSessionId())
									.joinProject(project, project.getUserId());

					fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
									ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Joined));

				} catch (FrontendNotLoggedInException e) {
					handleNotLoggedInException(e);
				} catch (RuntimeException run) {
					fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
				} catch (NoSuchProjectException e) {
					fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
				}
			}
		};

		// start our runner thread, that makes a callback to project status
		new Thread(runner).start();
	}


	public void rejectProject(final Project project) {
		log.info("Reject project: " + project);

		Runnable runner = new Runnable() {
			public void run() {
				try {
					getFrontendService().getProjectsManagingService(getSessionId())
									.rejectProject(project, project.getUserId());

					fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
									ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Rejected));

				} catch (FrontendNotLoggedInException e) {
					fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
				} catch (RuntimeException run) {
					fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
				} catch (NoSuchProjectException e) {
					fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
				}
			}
		};

		// start our runner thread, that makes a callback to project status
		new Thread(runner).start();
	}

	@Override
	// TODO: should be a running later ?
	public void syncProject(Project project) {
		try {
			getFrontendService().getSyncService(getSessionId()).poke(project);

			fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
							ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Sync));

		} catch (IllegalArgumentException e) {
			//empty implementation
		} catch (IllegalStateException e) {
			//empty implementation
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		}
	}


	public void setProjectName(Project project, String prName) {
		try {
			this.getFrontendService().getProjectsManagingService(this.getSessionId())
							.updateProjectName(project, prName);
			fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
							ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Name));
		} catch (IllegalArgumentException e) {
			//empty implementation
		} catch (IllegalStateException e) {
			//empty implementation
		} catch (NoSuchProjectException e) {
			//empty implementation
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		}
	}

	@Override
	public FolderObject getProjectRootFolder(Project project)
					throws ProjectFolderMissingException {
		log.info("get root folder for " + project);

		String rootPath = project.getRootPath();
		File rootFolder = new File(rootPath);
		if (!rootFolder.exists()) {
			throw new ProjectFolderMissingException(rootPath);
		}

		/*
									 * Construct a folder from the entire project
									 */
		FolderObject fo = null;
		try {
			IProjectsManagingService pms = this.getFrontendService()
							.getProjectsManagingService(getSessionId());
			fo = recursiveFileSystemHelper(project, "", "", pms.getFileServices(project),
							pms);
		} catch (IllegalArgumentException e) {
			//empty implementation
		} catch (IllegalStateException e) {
			//empty implementation
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		} catch (ProjectNotLoadedException e) {
			this.handleProjectNotLoaded(e);
		}

		return fo;
	}

	@Override
	public AvailableLaterObject<List<FileObject>> getAllProjectFiles(Project project) {
		AvailableLaterObject<List<FileObject>> result = null;
		Exception ex = null;

		try {
			result = this.getFrontendService().getProjectsManagingService(sessionId)
							.getAllProjectFiles(project);
		} catch (FileNotFoundException e) {
			ex = e;
		} catch (IllegalArgumentException e) {
			ex = e;
		} catch (IllegalStateException e) {
			ex = e;
		} catch (NoSuchProjectException e) {
			ex = e;
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
			ex = e;
		}

		if (result == null) result = new AvailableErrorObject<List<FileObject>>(ex);
		return result.start();
	}

	@Override
	public AttributedJakeObject getJakeObjectSyncStatus(Project project,
																											FileObject file) {
		try {
			return this.getFrontendService().getSyncService(getSessionId())
							.getJakeObjectSyncStatus(file);
		} catch (NotAFileException e) {
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (FileNotFoundException e) {
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (InvalidFilenameException e) {
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (NotAReadableFileException e) {
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		} catch (IOException e) {
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		}

		return null;
	}

	/**
	 * Helper method: Works recursively through the file system to
	 * build the FolderObject for getProjectRootFolder()
	 *
	 * @param prj		 The project the created Folder Object is in.
	 * @param relPath a Project-relative path to the returned folder object.
	 * @param name		the name of the current folder. It is the last part of relPath
	 * @param fss		 The FileSystemService used to access the Project-Folder
	 * @param pms
	 * @return
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *
	 * @throws IllegalStateException
	 * @throws IllegalArgumentException
	 */
	private FolderObject recursiveFileSystemHelper(Project prj, String relPath,
																								 String name, IFSService fss,
																								 IProjectsManagingService pms)
					throws IllegalArgumentException, IllegalStateException,
								 FrontendNotLoggedInException {
		FolderObject fo = new FolderObject(relPath, name);
		log.info(
						"recursiveFileSystemHelper: " + prj + " relPath: " + relPath + " name: " + name + " ifss: " + fss + "iprojectManagingService: " + pms);
		try {
			for (String f : fss.listFolder(relPath)) {
				// f is a valid relpath
				try {
					if (fss.fileExists(f)) {
						//is an ordinary file
						//get an appropriate FileObject - with the real UUID
						//The UUid may still be null e.g. if the file only exists locally
						//and has just been detected.
						fo.addFile(pms.getFileObjectByRelPath(prj, f));
					} else if (fss.folderExists(relPath)) {
						FolderObject subfolder = recursiveFileSystemHelper(prj, f,
										fss.getFileName(f), fss, pms);
						fo.addFolder(subfolder);
					}
				} catch (InvalidFilenameException e) {
					//silently discarded  - files/folders we cannot open or read are not processed
				} catch (IOException e) {
					//silently discarded  - files/folders we cannot open or read are not processed
				} catch (NoSuchJakeObjectException e) {
					//silently discarded  - files/folders we cannot open or read are not processed
				}
			}
		} catch (InvalidFilenameException e) {
			//silently discarded  - files/folders we cannot open or read are not processed
		} catch (IOException e) {
			//silently discarded  - files/folders we cannot open or read are not processed
		}

		return fo;
	}

	public List<NoteObject> getNotes(Project project)
					throws NoteOperationFailedException {
		try {
			return this.frontendService.getProjectsManagingService(this.sessionId)
							.getNoteManagingService().getNotes(project);
		} catch (Exception e) {
			NoteOperationFailedException ex = new NoteOperationFailedException();
			ex.append(e);
			throw ex;
		}
	}

	@Override
	public Date getLastEdit(NoteObject note) throws NoteOperationFailedException {
		Date result = null;

		try {
			result = this.getFrontendService()
							.getProjectsManagingService(this.getSessionId()).getLastEdit(note);
		} catch (Exception e) {
			log.error("Tried to getLastEdit on NoteObject: " + note + "got Exception: " + e
							.getMessage());
			// TODO: i did not print out the exception, because there were just so many of them ;)
			//this.fireErrorListener(new JakeErrorEvent(e));
			result = Calendar.getInstance().getTime();
		}

		return result;
	}

	@Override
	public ProjectMember getLastEditor(NoteObject note)
					throws NoteOperationFailedException {
		ProjectMember member = null;
		try {
			member = this.getFrontendService()
							.getProjectsManagingService(this.getSessionId()).getLastEditor(note);
		} catch (Exception e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		}

		return member;
	}

	@Override
	public boolean isLocalNote(NoteObject note) throws NoteOperationFailedException {
		boolean result;

		try {
			result = this.frontendService.getProjectsManagingService(this.sessionId)
							.isLocalJakeObject(note);
		} catch (Exception e) {
			// Every error allows only one interpretation: a Note that cannot
			// be checked wether it is local is NOT local!
			result = false;
		}

		return result;
	}

	@Override
	public void deleteNote(NoteObject note) throws NoteOperationFailedException {
		IProjectsManagingService pms;
		ProjectMember member;

		try {
			pms = this.frontendService.getProjectsManagingService(this.getSessionId());
			log.debug("getLoggedInUser: " + this.getLoggedInUser(note.getProject()));
			member = pms.getProjectMember(note.getProject(),
							this.getLoggedInUser(note.getProject()));

			pms.getNoteManagingService().deleteNote(note);
		} catch (Exception e) {
			//FIXME: 
			e.printStackTrace();
			NoteOperationFailedException ex = new NoteOperationFailedException();
			ex.append(e);
			throw ex;
		}
		// The corresponding JakeObject does not exist any more - there is
		// no
		// need deleting it therefore we simply discard this exception.
	}

	@Override
	public void newNote(NoteObject note) throws NoteOperationFailedException {
		try {
			this.frontendService.getProjectsManagingService(this.getSessionId())
							.getNoteManagingService().addNote(note);
		} catch (Exception e) {
			NoteOperationFailedException ex = new NoteOperationFailedException();
			ex.append(e);
			throw ex;
		}
		this.fireProjectChanged(new ProjectChanged.ProjectChangedEvent(note.getProject(),
						ProjectChangedReason.State));
	}

	@Override
	public void saveNote(NoteObject note) throws NoteOperationFailedException {
//		try {
		this.frontendService.getProjectsManagingService(this.getSessionId())
						.getNoteManagingService().saveNote(note);

		this.fireProjectChanged(new ProjectChanged.ProjectChangedEvent(note.getProject(),
						ProjectChangedReason.State));


//		} catch (Exception e) {
//			NoteOperationFailedException ex = new NoteOperationFailedException();
//			ex.append(e);
//			throw ex;
//		}

	}

	//FIXME: holy crap! The method name does  not match the return type!
	// TODO this might be removed in further versions...
	// it depends on only having a SINGLE user per project.
	private MsgService getLoggedInUser(Project p) throws FrontendNotLoggedInException {
		return p.getMessageService();
	}

	/**
	 * Generates a list so that people are remembered when they change.
	 *
	 * @param project : project that should be evaluated
	 * @return
	 */
	public List<ProjectMember> getPeople(Project project)
					throws PeopleOperationFailedException {
		log.info("getPeople from project " + project);

		if (project == null) return new ArrayList<ProjectMember>();

		// FIXME: hack, hack, hack - not really - looks perfect to me! - christopher
		List<ProjectMember> result = new ArrayList<ProjectMember>();
		/*try {
			 for (UserId mem : this.getFrontendService().getSyncService(
						 getSessionId()).getBackendUsersService(project)
						 .getUsers()) {

				 result.add(new ProjectMember(UUID.randomUUID(), mem
							 .getUserId(), TrustState.TRUST));
			 }
			 return result;
		 } catch (Exception e) {
			 log.warn("getPeople failed: " + e.getMessage());
			 //ExceptionUtilities.showError(e);
			 return result;
		 }
		 */

		try {
			result = this.getFrontendService()
							.getProjectsManagingService(this.getSessionId())
							.getProjectMembers(project);
		} catch (Exception e) {
			PeopleOperationFailedException ex = new PeopleOperationFailedException();
			ex.append(e);
			throw ex;
		}

		// HACK DEMO
		//result.addAll(coreMock.getPeople(project));

		return result;
	}

	@Override
	public boolean setPeopleNickname(Project project, ProjectMember pm, String nick) {
		log.info(
						"setPeopleNickname: project: " + project + " ProjectMember: " + pm + " Nick: " + nick);

		// TODO: ignore this and create a regex for checking!
		if (nick.indexOf("<") != -1) {
			return false;
		} else {
			pm.setNickname(nick);
			this.getFrontendService().getProjectsManagingService(getSessionId())
							.updateProjectMember(project, pm);

			fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
							ProjectChanged.ProjectChangedEvent.ProjectChangedReason.People));

			return true;
		}
	}

	@Override
	public void peopleSetTrustState(Project project, ProjectMember member,
																	TrustState trust) {
		member.setTrustState(trust);
		this.getFrontendService().getProjectsManagingService(getSessionId())
						.updateProjectMember(project, member);

		fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
						ProjectChanged.ProjectChangedEvent.ProjectChangedReason.People));
	}


	@Override
	public void invitePeople(Project project, String userid) {
		try {
			this.getFrontendService().getProjectsManagingService(getSessionId())
							.invite(project, userid);

			fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
							ProjectChanged.ProjectChangedEvent.ProjectChangedReason.People));
		} catch (IllegalArgumentException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		} catch (IllegalStateException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (UserIdFormatException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (Exception e) {
			// TODO: generic fetcher?
			this.fireErrorListener(new JakeErrorEvent(e));
		}
	}

	@Override
	public List<ProjectMember> getSuggestedPeople(Project project) {
		List<ProjectMember> members = null;

		try {
			members = this.getFrontendService().getProjectsManagingService(getSessionId())
							.getUninvitedPeople(project);
		} catch (IllegalArgumentException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		} catch (IllegalStateException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (NoSuchProjectException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		}

		//never return null
		return (members == null) ? new ArrayList<ProjectMember>() : members;
	}


	@Override
	public List<LogEntry> getLog(Project project, JakeObject jakeObject, int entries) {
		log.info(
						"getLog pr:" + project + " jakeObject: " + jakeObject + " entries: " + entries);

		// TODO: implement
		return new ArrayList<LogEntry>();

	}


	public void createProject(final String name, final String path,
														final MsgService msg) {
		log.info("Create project: " + name + " path: " + path + " msg: " + msg);

		//preconditions
		if (path == null) {
			throw new NullPointerException();
		}

		Runnable runner = new Runnable() {
			public void run() {
				Project pr = null;
				MsgService messageService;

				try {
					//add Project to core-internal list
					pr = frontendService.getProjectsManagingService(sessionId)
									.createProject(name, path, msg);

					/*
																				 * The project is created, but not started and no user is assigned to it.
																				 */

					/* FIXME since there is only one user in the current implementation, it is added to the project! */
					if (pr.getUserId() == null) try {
						frontendService.getProjectsManagingService(sessionId)
										.assignUserToProject(pr, pr.getMessageService().getUserId());
						log.debug("After creation, the project's userid is: " + pr.getUserId());
					} catch (IllegalAccessException e) {
						// FIXME: is this expected? Why?
						log.error("assigning to project failed", e);
					}

					fireProjectChanged(new ProjectChanged.ProjectChangedEvent(pr,
									ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Created));

				} catch (FrontendNotLoggedInException e) {
					log.debug("Tried to create a project while not authenticated to the core.",
									e);
				} catch (RuntimeException run) {
					fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
				} catch (FileNotFoundException e) {
					fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
				}
			}
		};

		// start our runner thread, that makes a callback to project status
		new Thread(runner).start();
	}


	private void fireRegistrationStatus(RegistrationStatus.RegisterStati state,
																			String str) {
		for (RegistrationStatus callback : registrationStatus) {
			callback.setRegistrationStatus(state, str);
		}
	}

	@Override
	public Date getFileLastModified(FileObject file) {
		// TODO: Make this useful - should return the last modification - remote or local
		return this.getLocalFileLastModified(file);
	}

	@Override
	public AvailableLaterObject<Void> importExternalFileFolderIntoProject(
					Project project, List<File> files, String destFolderRelPath) {

		// HACK
//		for (File file : files) {
//			try {
//				copy(file.getAbsolutePath(), new File(project.getRootPath(), destFolderRelPath).getAbsolutePath());
//			} catch (Exception e) {
//				log.warn("copy failed", e);
//			}
//		}
//
//		fireProjectChanged(new ProjectChanged.ProjectChangedEvent(
//				  project, ProjectChangedReason.Files));
//
//		for (FilesChanged f : coreMock.filesChangedListeners) {
//			f.filesChanged();
//		}


		return null;
	}

	public static void copy(String fromFileName, String toFileName)
					throws IOException {
		File fromFile = new File(fromFileName);
		File toFile = new File(toFileName);

		if (!fromFile.exists())
			throw new IOException("FileCopy: " + "no such source file: " + fromFileName);
		if (!fromFile.isFile())
			throw new IOException("FileCopy: " + "can't copy directory: " + fromFileName);
		if (!fromFile.canRead()) throw new IOException(
						"FileCopy: " + "source file is unreadable: " + fromFileName);

		if (toFile.isDirectory()) toFile = new File(toFile, fromFile.getName());

		if (toFile.exists()) {
			if (!toFile.canWrite()) throw new IOException(
							"FileCopy: " + "destination file is unwriteable: " + toFileName);
			System.out.print("Overwrite existing file " + toFile.getName() + "? (Y/N): ");
			System.out.flush();
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String response = in.readLine();
			if (!response.equals("Y") && !response.equals("y"))
				throw new IOException("FileCopy: " + "existing file was not overwritten.");
		} else {
			String parent = toFile.getParent();
			if (parent == null) parent = System.getProperty("user.dir");
			File dir = new File(parent);
			if (!dir.exists()) throw new IOException(
							"FileCopy: " + "destination directory doesn't exist: " + parent);
			if (dir.isFile()) throw new IOException(
							"FileCopy: " + "destination is not a directory: " + parent);
			if (!dir.canWrite()) throw new IOException(
							"FileCopy: " + "destination directory is unwriteable: " + parent);
		}

		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead); // write
		} finally {
			if (from != null) try {
				from.close();
			} catch (IOException e) {
				;
			}
			if (to != null) try {
				to.close();
			} catch (IOException e) {
				;
			}
		}
	}


	@Override
	public ProjectMember getLastModifier(JakeObject jakeObject)
					throws NoSuchLogEntryException {
		return this.getFrontendService().getProjectsManagingService(getSessionId())
						.getLastModifier(jakeObject);
	}

	@Override
	public void deleteToTrash(Project project, String relpath) {
		// TODO: What do we do with folders?
		try {
			this.getFrontendService().
							getProjectsManagingService(this.getSessionId()).
							getFileServices(project).
							trashFile(relpath);
		} catch (FileNotFoundException e) {
			log.debug("Tried to delete nonexisting file", e);
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (IllegalArgumentException e) {
			log.debug("Cannot delete FileObject", e);
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (IllegalStateException e) {
			log.debug("Project service is not available.");
		} catch (InvalidFilenameException e) {
			log.debug("Filename of FileObject invalid: " + relpath);
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (FrontendNotLoggedInException e) {
			log.debug("Tried access session without signing in.", e);
		} catch (ProjectNotLoadedException e) {
			log.debug("Tried to delete file of project not loaded");
			this.handleProjectNotLoaded(e);

		}
	}

	@Override
	public void rename(FileObject file, String newName) {
		this.renamePath(file.getRelPath(), file.getProject(), newName);
	}

	@Override
	public void rename(FolderObject folder, String newName) {
		//TODO obtain project
		//TODO call renamePath
	}

	@Override
	public Set<Tag> getTagsForFileObject(FileObject fo) {
		try {
			return this.getFrontendService()
							.getProjectsManagingService(this.getSessionId())
							.getTagsForJakeObject(fo);
		} catch (IllegalArgumentException e) {
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		} catch (IllegalStateException e) {
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (NoSuchJakeObjectException e) {
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		}

		return new TreeSet<Tag>();
	}

	@Override
	public void setTagsForFileObject(FileObject fo, Set<Tag> tags) {
		try {
			this.getFrontendService().getProjectsManagingService(this.getSessionId())
							.setTagsForJakeObject(fo, tags);
		} catch (IllegalArgumentException e) {
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		} catch (IllegalStateException e) {
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (NoSuchJakeObjectException e) {
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		}
	}

	/**
	 * Renames a file
	 *
	 * @param fromPath relative Path of source
	 * @param project	Project to retrieve a FSService for - fromPath should be in the Project.
	 * @param newName	new name
	 */
	private void renamePath(String fromPath, Project project, String newName) {
		String to;
		File toFile;


		/*
								 * create a File with the same parent as the passed
								 * FROM-Path, but with a different name.
								 */
		toFile = new File(new File(fromPath).getParentFile(), newName);


		try {
			this.getFrontendService().
							getProjectsManagingService(this.getSessionId()).
							getFileServices(project).moveFile(fromPath, toFile.toString());
		} catch (IllegalArgumentException e) {
			log.warn("Cannot rename file");
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (IllegalStateException e) {
			log.debug("Project service is not available.");
		} catch (InvalidFilenameException e) {
			log.debug("Filename of FileObject invalid: " + fromPath);
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (NotAReadableFileException e) {
			log.warn("Cannot move an unreadable file");
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (FileAlreadyExistsException e) {
			log.warn("Cannot move file: destination already exists.");
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (IOException e) {
			log.warn("Cannot rename file");
			fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
		} catch (CreatingSubDirectoriesFailedException e) {
			log.error("Creating a subdirectory that should not be created failed...");
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		} catch (ProjectNotLoadedException e) {
			log.error("tried to rename a file of a project thats not loaded");
			this.handleProjectNotLoaded(e);
		}
	}

	@Override
	public AvailableLaterObject<Void> announceJakeObject(JakeObject jo,
																											 String commitmsg)
					throws FileOperationFailedException {
		ProjectMember member;
		LogEntry<JakeObject> action;
		ISyncService iss;
		AvailableLaterObject<Void> result = null;

		member = this.getFrontendService()
						.getProjectsManagingService(this.getSessionId())
						.getProjectMember(jo.getProject(),
										this.getLoggedInUser(jo.getProject()));

		action = new LogEntry<JakeObject>(UUID.randomUUID(),
						LogAction.JAKE_OBJECT_NEW_VERSION, Calendar.getInstance().getTime(),
						jo.getProject(), jo, member);

		try {
			iss = this.getFrontendService().getSyncService(this.getSessionId());
			result = new AnnounceFuture(iss, jo, action);
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
			throw new FileOperationFailedException(e);
		}

		return result.start();
	}


	@Override
	public void announceFileObjects(ArrayList<FileObject> jos)
					throws FileOperationFailedException {

	}

	@Override
	public AvailableLaterObject<Void> pullJakeObject(JakeObject jo)
					throws FileOperationFailedException {
		ISyncService iss;
		AvailableLaterObject<Void> result;

		try {
			iss = this.getFrontendService().getSyncService(this.getSessionId());
			result = new PullFuture(iss, jo);
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
			throw new FileOperationFailedException(e);
		}

		return result.start();
	}

	@Override
	public boolean isJakeObjectLocked(JakeObject jo) {
		return this.getFrontendService().getSyncService(this.getSessionId())
						.getLock(jo) != null;
	}

	@Override
	public void createNewFolderAt(Project project, String relpath, String folderName)
					throws InvalidNewFolderException {
		try {
			this.getFrontendService().getProjectsManagingService(getSessionId())
							.getFileServices(project).createFolder(relpath);
		} catch (IllegalArgumentException e) {
			throw new InvalidNewFolderException(relpath);
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		} catch (IllegalStateException e) {
			throw new InvalidNewFolderException(relpath);
		} catch (InvalidFilenameException e) {
			throw new InvalidNewFolderException(relpath);
		} catch (IOException e) {
			throw new InvalidNewFolderException(relpath);
		} catch (ProjectNotLoadedException e) {
			this.handleProjectNotLoaded(e);
		}
	}

	@Override
	public void addFilesChangedListener(final FilesChanged listener, Project project) {

		IProjectModificationListener projectModificationListener = new IProjectModificationListener() {

			@Override
//package com.jakeapp.gui.swing;
//
//import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
//import com.jakeapp.core.domain.*;
//import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
//import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
//import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
//import com.jakeapp.core.services.IFrontendService;
//import com.jakeapp.core.services.MsgService;
//import com.jakeapp.core.services.XMPPMsgService;
//import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
//import com.jakeapp.core.util.availablelater.AvailableLaterObject;
//import com.jakeapp.core.util.availablelater.AvailableNowObject;
//import com.jakeapp.gui.swing.callbacks.*;
//import com.jakeapp.gui.swing.callbacks.ProjectChanged.ProjectChangedEvent.ProjectChangedReason;
//import com.jakeapp.gui.swing.exceptions.*;
//import com.jakeapp.gui.swing.helpers.*;
//import com.jakeapp.jake.ics.exceptions.NetworkException;
//import org.apache.log4j.Logger;
//
//import java.io.File;
//import java.util.*;
//
//
//public class CoreAccessMock implements ICoreAccess {
//	private static final Logger log = Logger.getLogger(CoreAccessMock.class);
//
//	private boolean isSignedIn;
//	private List<Project> projects = new ArrayList<Project>();
//	private List<Project> invitedProjects = new ArrayList<Project>();
//	private Map<Project, List<ProjectMember>> peopleProjectMap = new HashMap<Project, List<ProjectMember>>();
//	private IFrontendService frontendService;
//	private List<NoteObject> notesList;
//	private List<Boolean> notesIsLocal;
//	private List<Boolean> notesIsLocked;
//
//	public List<FilesChanged> filesChangedListeners;
//
//	/**
//	 * SessionId returned by the authentication Method of FrontendService.authenticate.
//	 */
//	private String sessionId;
//
//
//	/**
//	 * Core Access Mock initialisation code
//	 */
//	public CoreAccessMock() {
//		isSignedIn = false;
//		connectionStatus = new ArrayList<ConnectionStatus>();
//		registrationStatus = new ArrayList<RegistrationStatus>();
//		projectChanged = new ArrayList<ProjectChanged>();
//		errorCallback = new ArrayList<ErrorCallback>();
//		filesChangedListeners = new ArrayList<FilesChanged>();
//
//		// init the demo projects
//		Project pr1 = new Project("Desktop", new UUID(212, 383), new XMPPMsgService(), new File(FileUtilities.getUserHomeDirectory() + FileUtilities.getPathSeparator() + "Desktop"));
//		pr1.setStarted(true);
//		pr1.setInvitationState(InvitationState.ACCEPTED);
//		projects.add(pr1);
//
//		Project pr2 = new Project("Downloads", new UUID(222, 373), null, new File(FileUtilities.getUserHomeDirectory() + FileUtilities.getPathSeparator() + "Downloads"));
//		pr2.setInvitationState(InvitationState.ACCEPTED);
//		projects.add(pr2);
//
//		Project pr3 = new Project("Jake", new UUID(232, 363), null, new File(FileUtilities.getUserHomeDirectory() + FileUtilities.getPathSeparator() + "Jake"));
//		pr3.setInvitationState(InvitationState.ACCEPTED);
//		projects.add(pr3);
//
//		// Yes, we need a windows testing project too...
//		Project pr4 = new Project("Windows test", new UUID(242, 353), null, new File("C:\\test"));
//		pr4.setInvitationState(InvitationState.ACCEPTED);
//		projects.add(pr4);
//
//		Project ipr1 = new Project("DEMO INVITATION 1", new UUID(212, 33), null, new File(""));
//		ipr1.setInvitationState(InvitationState.INVITED);
//		invitedProjects.add(ipr1);
//
//		Project ipr2 = new Project("DEMO INVITATION 2", new UUID(222, 33), null, new File(""));
//		ipr2.setInvitationState(InvitationState.INVITED);
//		invitedProjects.add(ipr2);
//
//		Project ipr3 = new Project("DEMO INVITATION 3", new UUID(232, 33), null, new File(""));
//		ipr3.setInvitationState(InvitationState.INVITED);
//		invitedProjects.add(ipr3);
//
//		//mock notes
//		this.notesList = new ArrayList<NoteObject>();
//		this.notesList.add(new NoteObject(new UUID(1, 1), pr1, "Chuck Norris does not need an undo function"));
//		this.notesList.add(new NoteObject(new UUID(1, 1), pr1, "If you have five dollars and Chuck Norris has five dollars, Chuck Norris has more money than you"));
//		this.notesList.add(new NoteObject(new UUID(2, 1), pr1, "Apple pays Chuck Norris 99 cents every time he listens to a song."));
//		this.notesList.add(new NoteObject(new UUID(3, 1), pr1, "Chuck Norris is suing Myspace for taking the name of what he calls everything around you."));
//		this.notesList.add(new NoteObject(new UUID(4, 1), pr1, "Chuck Norris destroyed the periodic table, because he only recognizes the element of surprise."));
//		this.notesList.add(new NoteObject(new UUID(4, 1), pr1, "Chuck Norris can kill two stones with one bird."));
//		this.notesList.add(new NoteObject(new UUID(5, 1), pr1, "The leading causes of death in the United States are: 1. Heart Disease 2. Chuck Norris 3. Cancer."));
//		this.notesList.add(new NoteObject(new UUID(6, 1), pr1, "Chuck Norris does not sleep. He waits."));
//		this.notesList.add(new NoteObject(new UUID(7, 1), pr1, "There is no theory of evolution. Just a list of animals Chuck Norris allows to live. "));
//		this.notesList.add(new NoteObject(new UUID(8, 1), pr1, "Guns don't kill people, Chuck Norris does."));
//		this.notesList.add(new NoteObject(new UUID(9, 1), pr1, "============ MOCK NOTES ==========="));
//
//
//		this.notesIsLocal = new ArrayList<Boolean>();
//		this.notesIsLocked = new ArrayList<Boolean>();
//		for (int i = 0; i < this.notesList.size(); i++) {
//			this.notesIsLocal.add(new Random().nextBoolean());
//			this.notesIsLocked.add(new Random().nextBoolean());
//		}
//
//		/*
//		// Mocking of FSS updates
//		TimerTask t = new TimerTask() {
//			@Override
//			public void run() {
//				log.debug("Notifying " + filesChangedListeners.size() + " FilesChanged listeners...");
//				for (FilesChanged f : filesChangedListeners) {
//					f.filesChanged();
//				}
//			}
//		};
//
//		(new Timer()).scheduleAtFixedRate(t, 1000, 3000);
//		*/
//	}
//
//
//	@Override
//	public List<Project> getMyProjects() {
//		return projects;
//	}
//
//	// TODO: change this: invitations are only runtime specific... (are they?)
//	@Override
//	public List<Project> getInvitedProjects() {
//
//		return invitedProjects;
//	}
//
//
//	@Override
//	public void setFrontendService(IFrontendService frontendService) {
//		this.frontendService = frontendService;
//	}
//
//	@Override
//	public void authenticateOnBackend(Map<String, String> authenticationData) throws InvalidCredentialsException {
//		this.sessionId = this.frontendService.authenticate(authenticationData);
//	}
//
//	@Override
//	public void backendLogOff() {
//		this.sessionId = "";
//
//
//	}
//
//	public void addErrorListener(ErrorCallback ec) {
//		errorCallback.add(ec);
//	}
//
//	public void removeErrorListener(ErrorCallback ec) {
//		errorCallback.remove(ec);
//	}
//
//	private void fireErrorListener(ErrorCallback.JakeErrorEvent ee) {
//		for (ErrorCallback callback : errorCallback) {
//			callback.reportError(ee);
//		}
//	}
//
//	public void signIn(final String user, final String pass) {
//		log.info("Signs in: " + user + "pass: " + pass);
//
//		Runnable runner = new Runnable() {
//			public void run() {
//				fireConnectionStatus(ConnectionStatus.ConnectionStati.SigningIn, "");
//
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//				isSignedIn = true;
//				currentUser = user;
//
//				fireConnectionStatus(ConnectionStatus.ConnectionStati.Online, "");
//			}
//		};
//
//		// start our runner thread, that makes callbacks to connection status
//		new Thread(runner).start();
//	}
//
//	public void addConnectionStatusCallbackListener(ConnectionStatus cb) {
//		log.info("Registers connection status callback: " + cb);
//
//		connectionStatus.add(cb);
//	}
//
//	public void removeConnectionStatusCallbackListener(ConnectionStatus cb) {
//		log.info("Deregisters connection status callback: " + cb);
//
//		connectionStatus.remove(cb);
//	}
//
//
//	private void fireConnectionStatus(ConnectionStatus.ConnectionStati state, String str) {
//		log.info("spead callback event...");
//		for (ConnectionStatus callback : connectionStatus) {
//			callback.setConnectionStatus(state, str);
//		}
//	}
//
//	public void register(String user, String pass) {
//		log.info("Registering user: " + user + " pass: " + pass);
//
//		Runnable runner = new Runnable() {
//			public void run() {
//
//				// registering
//				fireRegistrationStatus(RegistrationStatus.RegisterStati.RegistrationActive, "");
//
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//				fireRegistrationStatus(RegistrationStatus.RegisterStati.RegisterSuccess, "");
//
//				// logging in after registering
//				fireConnectionStatus(ConnectionStatus.ConnectionStati.SigningIn, "");
//
//				try {
//					Thread.sleep(1500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//				isSignedIn = true;
//
//				fireConnectionStatus(ConnectionStatus.ConnectionStati.Online, "");
//			}
//		};
//
//		// start our runner thread, that makes callbacks to connection status
//		new Thread(runner).start();
//	}
//
//	public void addRegistrationStatusCallbackListener(RegistrationStatus cb) {
//		log.info("Registers registration status callback: " + cb);
//	}
//
//	public void removeRegistrationStatusCallbackListener(RegistrationStatus cb) {
//		log.info("Deregisters registration status callback: " + cb);
//
//	}
//
//	public void addProjectChangedCallbackListener(ProjectChanged cb) {
//		// log.debug("Mock: register project changed callback: " + cb);
//
//		projectChanged.add(cb);
//	}
//
//	public void removeProjectChangedCallbackListener(ProjectChanged cb) {
//		log.info("Mock: deregister project changed callback: " + cb);
//
//		if (projectChanged.contains(cb)) {
//			projectChanged.remove(cb);
//		}
//	}
//
//	private void fireProjectChanged(ProjectChanged.ProjectChangedEvent ev) {
//		for (ProjectChanged callback : projectChanged) {
//			callback.projectChanged(ev);
//		}
//	}
//
//
//	public void stopProject(Project project) {
//		log.info("stop project: " + project);
//
//		//if(!project.isStarted())
//		//    throw new ProjectNotStartedException();
//
//		project.setStarted(false);
//
//		// generate event
//		fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
//				  ProjectChanged.ProjectChangedEvent.ProjectChangedReason.State));
//	}
//
//	public void startProject(Project project) {
//		project.setStarted(true);
//
//		// generate event
//		fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
//				  ProjectChanged.ProjectChangedEvent.ProjectChangedReason.State));
//	}
//
//
//	public AvailableLaterObject<Integer> getProjectFileCount(Project project) {
//		return new AvailableLaterObject<Integer>() {
//			@Override
//			public Integer calculate() {
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				return 100;
//			}
//		}.start();
//	}
//
//	public AvailableLaterObject<Long> getProjectSizeTotal(Project project) {
//		return new AvailableLaterObject<Long>() {
//			@Override
//			public Long calculate() {
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				return 50000L;
//			}
//		}.start();
//	}
//
//
//	public void deleteProject(final Project project, boolean deleteProjectFiles) {
//		log.info("Mock: delete project: " + project);
//
//		if (project == null) {
//			throw new IllegalArgumentException("Cannot delete empty project!");
//		}
//
//
//		Runnable runner = new Runnable() {
//			public void run() {
//
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//				try {
//					// search project in list
//					boolean ret = projects.remove(project);
//
//					if (!ret) {
//						throw new ProjectNotFoundException("Project not found in list!");
//					}
//
//					fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
//							  ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Deleted));
//
//				} catch (RuntimeException run) {
//					fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
//				}
//			}
//		};
//
//		// start our runner thread, that makes a callback to project status
//		new Thread(runner).start();
//	}
//
//	public void joinProject(final String path, final Project project) {
//		log.info("Mock: join project: " + project + " path: " + path);
//
//		if (path == null) {
//			//throw new
//		}
//
//		Runnable runner = new Runnable() {
//			public void run() {
//
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//				try {
//					project.setRootPath(path);
//					projects.add(project);
//					invitedProjects.remove(project);
//					project.setInvitationState(InvitationState.ACCEPTED);
//
//
//					fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
//							  ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Joined));
//
//				} catch (RuntimeException run) {
//					fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
//				}
//			}
//		};
//
//		// start our runner thread, that makes a callback to project status
//		new Thread(runner).start();
//	}
//
//
//	public void rejectProject(final Project project) {
//		log.info("Mock: reject project: " + project);
//
//
//		Runnable runner = new Runnable() {
//			public void run() {
//
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//				try {
//					invitedProjects.remove(project);
//
//					fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
//							  ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Rejected));
//
//				} catch (RuntimeException run) {
//					fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
//				}
//			}
//		};
//
//		// start our runner thread, that makes a callback to project status
//		new Thread(runner).start();
//	}
//
//	@Override
//	public void syncProject(Project project) {
//		// do nothing
//	}
//
//	public void setProjectName(Project project, String prName) {
//		project.setName(prName);
//
//		fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
//				  ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Name));
//	}
//
//	@Override
//	public FolderObject getProjectRootFolder(Project project) throws ProjectFolderMissingException {
//		// This is all mocked from the actual file system
//		String rootPath = project.getRootPath();
//		log.debug("File mocking: Project root path is " + rootPath);
//
//		File rootFolder = new File(rootPath);
//		if (!rootFolder.exists()) {
//			throw new ProjectFolderMissingException(rootPath);
//		}
//
//		FolderObject fo = recursiveFolderObjectHelper(project, rootFolder, System.getProperty("file.separator"), "");
//
//		return fo;
//	}
//
//	@Override
//	public AvailableLaterObject<List<FileObject>> getAllProjectFiles(final Project project) {
//		log.info("Mock: getAllProjectFiles: " + project);
//
//		return new AvailableLaterObject<List<FileObject>>() {
//			@Override
//			public List<FileObject> calculate() throws ProjectFolderMissingException {
//
//				// This is all mocked from the actual file system
//				String rootPath = project.getRootPath();
//				log.debug("File mocking: Project root path is " + rootPath);
//
//				File rootFolder = new File(rootPath);
//				if (!rootFolder.exists()) {
//					throw new ProjectFolderMissingException(rootPath);
//				}
//
//				try {
//					Thread.sleep(800);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//				// query the data
//				ArrayList<FileObject> fo = recursiveFileObjectListHelper(project, new File(project.getRootPath()), "");
//				log.debug("File mocking done!");
//				return fo;
//			}
//		}.start();
//	}
//
//
//	private ArrayList<FileObject> recursiveFileObjectListHelper(Project p, File f, String relPath) {
//		log.info("recursiveFileObjectListHelper: " + f + " " + relPath);
//
//		if (!f.isDirectory()) {
//			throw new IllegalArgumentException(f.getAbsolutePath() + " is not a directory");
//		}
//
//		ArrayList<FileObject> files = new ArrayList<FileObject>();
//
//		for (File sub : f.listFiles()) {
//			if (sub.isDirectory()) {
//				files.addAll(recursiveFileObjectListHelper(p, sub, relPath + sub.getName() + System.getProperty("file.separator")));
//			} else {
//				FileObject fo = new FileObject(UUID.randomUUID(), p, relPath + sub.getName());
//				files.add(fo);
//			}
//		}
//
//		return files;
//	}
//
//	/**
//	 * Helper method for this mock: Works recursively through the file system to
//	 * build the FolderObject for getProjectRootFolder()
//	 *
//	 * @param file
//	 * @return
//	 */
//	private FolderObject recursiveFolderObjectHelper(Project prj, File file, String relPath, String name) {
//		FolderObject fo = new FolderObject(relPath, name);
//		log.debug("File mocking: Started recursing through folder " + file.getAbsolutePath());
//
//		for (File f : file.listFiles()) {
//			if (f.isDirectory()) {
//				log.debug("File mocking: Recursing into subdirectory " + relPath + f.getName() + System.getProperty("file.separator"));
//				FolderObject subfolder = recursiveFolderObjectHelper(prj, f, relPath + f.getName() + System.getProperty("file.separator"),
//						  f.getName());
//				fo.addFolder(subfolder);
//			} else {
//				log.debug("File mocking: Adding file " + relPath + f.getName());
//				fo.addFile(new FileObject(new UUID(5, 3), prj, relPath + f.getName()));
//			}
//		}
//
//		return fo;
//	}
//
//	public List<NoteObject> getNotes(Project project) throws NoteOperationFailedException {
//		//return this.frontendService.getProjectsManagingService(this.sessionId).getNotes(project);
//		return this.notesList;
//	}
//
//	@Override
//	public Date getLastEdit(NoteObject note) throws NoteOperationFailedException {
//		return new Date();
//	}
//
//	@Override
//	public ProjectMember getLastEditor(NoteObject note) throws NoteOperationFailedException {
//		// TODO: fix
//		return new ProjectMember(new UUID(11, 22), "Nickname", TrustState.AUTO_ADD_REMOVE);
//
//		//	return new ProjectMember(new XMPPUserId(new ServiceCredentials("Chuck Norris", "foo"), new UUID(1, 1), "chuck norris", "chuck", "Chuck", "Norris"), TrustState.TRUST);
//	}
//
//	@Override
//	public boolean isLocalNote(NoteObject note) throws NoteOperationFailedException {
//		return this.notesIsLocal.get(this.notesList.indexOf(note));
//	}
//
//	@Override
//	public void deleteNote(NoteObject note) throws NoteOperationFailedException {
//		log.debug("removing note: " + note + "from notes");
//		log.debug("success: " + Boolean.toString(this.notesList.remove(note)));
//		this.fireProjectChanged(new ProjectChanged.ProjectChangedEvent(note.getProject(), ProjectChangedReason.Deleted));
//	}
//
//	@Override
//	public void newNote(NoteObject note) throws NoteOperationFailedException {
//		log.debug("adding note: " + note);
//		this.notesList.add(note);
//		this.notesIsLocal.add(Boolean.TRUE);
//		this.notesIsLocked.add(Boolean.FALSE);
//		this.fireProjectChanged(new ProjectChanged.ProjectChangedEvent(note.getProject(), ProjectChangedReason.Deleted));
//	}
//
//	/**
//	 * Generates a list so that people are remembered when they change.
//	 *
//	 * @param project: project that should be evaluated
//	 * @return
//	 */
//	public List<ProjectMember> getPeople(Project project) throws PeopleOperationFailedException {
//		log.info("Mock: getPeople from project " + project);
//
//
//		if (!peopleProjectMap.containsKey(project)) {
//			List<ProjectMember> people = new ArrayList<ProjectMember>();
//
//			people.add(new ProjectMember(new UUID(11, 22), "Nickname", TrustState.AUTO_ADD_REMOVE));
//
//
//			people.add(new ProjectMember(UUID.randomUUID(), "Pr1", TrustState.TRUST));
//			people.add(new ProjectMember(UUID.randomUUID(), "Pr2", TrustState.AUTO_ADD_REMOVE));
//			people.add(new ProjectMember(UUID.randomUUID(), "Pr3", TrustState.NO_TRUST));
//			peopleProjectMap.put(project, people);
//		}
//
//		return peopleProjectMap.get(project);
//	}
//
//	@Override
//	public boolean setPeopleNickname(Project project, ProjectMember pm, String nick) {
//		if (nick.indexOf("<") != -1) {
//			return false;
//		} else {
//			pm.setNickname(nick);
//
//			fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
//					  ProjectChanged.ProjectChangedEvent.ProjectChangedReason.People));
//
//			return true;
//		}
//	}
//
//	@Override
//	public void peopleSetTrustState(Project project, ProjectMember member, TrustState trust) {
//		member.setTrustState(trust);
//
//		fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
//				  ProjectChanged.ProjectChangedEvent.ProjectChangedReason.People));
//	}
//
//
//	@Override
//	public void invitePeople(Project project, String userid) {
//		log.info("Mock: invite pm: " + userid + " to project: " + project);
//
//		// TODO: implement the invite
//
//	}
//
//	@Override
//	public List<ProjectMember> getSuggestedPeople(Project project) {
//		List<ProjectMember> members = new ArrayList<ProjectMember>();
//		try {
//			members.add(getPeople(project).get(0));
//		} catch (PeopleOperationFailedException e) {
//			ExceptionUtilities.showError(e);
//		}
////		members.add(getPeople(project).get(1));
//		return members;
//	}
//
//
//	@Override
//	public List<LogEntry> getLog(Project project, JakeObject jakeObject, int entries) {
//		return null;
//	}
//
//
//	public void createProject(final String name, final String path, MsgService msg) {
//		log.info("Mock: create project: " + name + " path: " + path);
//
//
//		if (path == null) {
//			//throw new
//		}
//
//		Runnable runner = new Runnable() {
//			public void run() {
//
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//				try {
//					Project pr1 = null;
//					/*
//					try {
//						pr1 = frontendService.getProjectsManagingService(sessionId).createProject(name, path, null);
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (FrontendNotLoggedInException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					*/
//					pr1 = new Project(name, new UUID(22, 33), null, new File(path));
//					pr1.setStarted(true);
//					projects.add(pr1);
//
//					fireProjectChanged(new ProjectChanged.ProjectChangedEvent(pr1,
//							  ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Created));
//
//				} catch (RuntimeException run) {
//					fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
//				}
//			}
//		};
//
//		// start our runner thread, that makes a callback to project status
//		new Thread(runner).start();
//	}
//
//
//	private void fireRegistrationStatus(RegistrationStatus.RegisterStati state, String str) {
//		for (RegistrationStatus callback : registrationStatus) {
//			callback.setRegistrationStatus(state, str);
//		}
//	}
//
//	@Override
//	public Date getFileLastModified(FileObject file) {
//		// TODO: Make this useful
//		return new Date(new File(file.getProject().getRootPath(), file.getRelPath()).lastModified());
//	}
//
//
//	@Override
//	public AvailableLaterObject<Void> importExternalFileFolderIntoProject(Project project, List<File> files, String destFolderRelPath) {
//		log.info("Mock: import file: " + DebugHelper.arrayToString(files) + " to " + destFolderRelPath);
//
//		return new AvailableLaterObject<Void>() {
//			@Override
//			public Void calculate() {
//				// do magic (import folder)
//				try {
//					Thread.sleep(100);
//					getListener().statusUpdate(0.5, "I am trying really hard!");
//					Thread.sleep(400);
//					getListener().statusUpdate(0.8, "I am on it!!!");
//					Thread.sleep(1000);
//					getListener().statusUpdate(0.9, "Stop annoying me, you bastard.");
//					Thread.sleep(400);
//					getListener().statusUpdate(1, "Did it! Yeah... I'm the man!");
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				return null;
//			}
//		}.start();
//	}
//
//	@Override
//	public ProjectMember getLastModifier(JakeObject jakeObject) throws NoSuchLogEntryException {
//		return new ProjectMember(new UUID(1, 1), "LastModifierNick", TrustState.AUTO_ADD_REMOVE);
//	}
//
//	@Override
//	public void deleteToTrash(Project project, String relpath) {
//		// FIXME: Implement me
//		//File f = file.getAbsolutePath();
//		log.info("Deleting file " + relpath);
//		//f.delete();
//	}
//
//	@Override
//	public void rename(FileObject file, String newName) {
//
//	}
//
//	@Override
//	public void rename(FolderObject folder, String newName) {
//
//	}
//
//	@Override
//	public Set<Tag> getTagsForFileObject(FileObject fo) {
//		Set<Tag> tags = new HashSet<Tag>();
//
//		try {
//			tags.add(new Tag("blubb"));
//			tags.add(new Tag("bar"));
//			tags.add(new Tag("foo"));
//		} catch (InvalidTagNameException e) {
//			// This won't happen.
//		}
//
//		return tags;
//	}
//
//	@Override
//	public void setTagsForFileObject(FileObject fo, Set<Tag> tags) {
//		log.info("Mock: setTagsForFileObject " + fo.getRelPath() + " (" + TagHelper.tagsToString(tags) + ")");
//	}
//
//	@Override
//	public AvailableLaterObject<Void> announceJakeObject(JakeObject jo, String commitmsg) throws FrontendNotLoggedInException {
//		log.info("Mock: announceFileObjects: " + jo + " with msg: " + commitmsg);
//		return null;
//	}
//
//	@Override
//	public void announceFileObjects(ArrayList<FileObject> jos) throws FileOperationFailedException {
//	}
//
//	@Override
//	public AvailableLaterObject<Void> pullJakeObject(JakeObject jo) throws FrontendNotLoggedInException {
//		log.info("Mock: pullJakeObject: " + jo);
//		return null;
//	}
//
//	@Override
//	public boolean isJakeObjectLocked(JakeObject jo) {
//		if (jo instanceof FileObject) {
//			return ((FileObject) jo).getRelPath().contains("l");
//		}
//		return (new Random()).nextBoolean();
//	}
//
//	@Override
//	public void createNewFolderAt(Project project, String relpath, String folderName) throws InvalidNewFolderException {
//		log.info("MOCKED: Creating new folder '" + folderName + "' below '" + relpath + "'");
//	}
//
//	@Override
//	public void addFilesChangedListener(FilesChanged listener, Project project) {
//		filesChangedListeners.add(listener);
//	}
//
//	@Override
//	public void removeFilesChangedListener(FilesChanged listener, Project project) {
//		filesChangedListeners.remove(listener);
//	}
//
//
//	public long getFileSize(FileObject file) {
//		return 1234;
//	}
//
//	@Override
//	public long getLocalFileSize(FileObject fo) {
//		return 8888;
//	}
//
//	@Override
//	public Date getLocalFileLastModified(FileObject fo) {
//		return new Date();
//	}
//
//	private String currentUser = null;
//
//	// event spread
//	private List<ConnectionStatus> connectionStatus;
//	private List<RegistrationStatus> registrationStatus;
//	private List<ProjectChanged> projectChanged;
//	private List<ErrorCallback> errorCallback;
//
//
//	@Override
//	public List<MsgService> getMsgServics() throws FrontendNotLoggedInException {
//		ArrayList<MsgService> msgs = new ArrayList<MsgService>();
//
//		MsgService m = null;
//		try {
//			m = addAccount(new ServiceCredentials("User", "Pass"));
//		} catch (InvalidCredentialsException e) {
//			e.printStackTrace();
//		} catch (ProtocolNotSupportedException e) {
//			e.printStackTrace();
//		}
//		msgs.add(m);
//		return msgs;
////		return this.frontendService.getMsgServices(this.sessionId);
//	}
//
//	@Override
//	public ProjectMember getProjectMember(Project project, MsgService msg) {
//		return new ProjectMember(UUID.randomUUID(), "Nickname", TrustState.AUTO_ADD_REMOVE);
//	}
//
//	@Override
//	public String getProjectMemberID(Project project, ProjectMember pm) {
//		if (pm != null) {
//			return pm.getNickname() + "@jabber.com";
//		} else {
//			return null;
//		}
//	}
//
//
//	@Override
//	public AvailableLaterObject<Void> createAccount(ServiceCredentials credentials)
//			  throws FrontendNotLoggedInException, InvalidCredentialsException,
//			  ProtocolNotSupportedException, NetworkException {
//		// TODO: mock!
//
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//		}
//
//		return null;
//		//return this.frontendService.createAccount(this.sessionId, credentials);
//	}
//
//	@Override
//	public MsgService addAccount(ServiceCredentials credentials)
//			  throws FrontendNotLoggedInException, InvalidCredentialsException,
//			  ProtocolNotSupportedException {
//		XMPPMsgService m = new XMPPMsgService();
//		m.setServiceCredentials(new ServiceCredentials("user", "pass"));
//		return m;
//		//return this.frontendService.addAccount(this.sessionId, credentials);
//	}
//
//	@Override
//	public void removeAccount(MsgService msg) throws FrontendNotLoggedInException, InvalidCredentialsException, ProtocolNotSupportedException, NetworkException {
//	}
//
//
//	@Override
//	public String getLockingMessage(JakeObject jakeObject) {
//		return "All your base are belong to Chuck Norris.";
//	}
//
//
//	@Override
//	public boolean isSoftLocked(JakeObject jakeObject) {
//		// FIXME: Merge two methods
//		if (jakeObject instanceof NoteObject) {
//			NoteObject note = (NoteObject) jakeObject;
//			return this.notesIsLocked.get(this.notesList.indexOf(note));
//		} else {
//			return new Random().nextBoolean();
//		}
//
//	}
//
//	@Override
//	public void setSoftLock(JakeObject jakeObject, boolean isSet, String lockingMessage) {
//		if (jakeObject instanceof NoteObject) {
//			NoteObject note = (NoteObject) jakeObject;
//			this.notesIsLocked.set(this.notesList.indexOf(note), isSet);
//		}
//		this.fireProjectChanged(new ProjectChanged.ProjectChangedEvent(jakeObject.getProject(), ProjectChangedReason.Deleted));
//	}
//
//
//	@Override
//	public ProjectMember getLockOwner(JakeObject jakeObject) {
//		return new ProjectMember(UUID.randomUUID(), "Chuck N.", TrustState.TRUST);
//	}
//
//
//	@Override
//	public void saveNote(NoteObject note) throws NoteOperationFailedException {
//		// [After HAL has killed the rest of the crew] Look Dave, I can see you're really upset about
//		// this. I honestly think you ought to sit down calmly, take a stress pill, and think things
//		// over. I know I've made some very poor decisions recently, but I can give you my complete
//		// assurance that my work will be back to normal. I've still got the greatest enthusiasm and
//		// confidence in the mission. And I want to help you.
//	}
//
//
//	@Override
//	public AvailableLaterObject<Boolean> login(MsgService service,
//															 String password, boolean rememberPassword) {
//		return new AvailableNowObject<Boolean>(true);
//	}
//
//	@Override
//	public File getFile(FileObject fo) throws FileOperationFailedException {
//
//		return null;
//	}
//
//	/*
//	@Override
//	public void announce(JakeObject jakeObject) {
//		if (jakeObject instanceof NoteObject) {
//			NoteObject note = (NoteObject) jakeObject;
//			this.notesIsLocal.set(this.notesList.indexOf(note), false);
//		}
//		this.fireProjectChanged(new ProjectChanged.ProjectChangedEvent(jakeObject.getProject(), ProjectChangedReason.State));
//
//	}
//
//	*/
//	@Override
//	public boolean isOnline(ProjectMember member) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//
//	@Override
//	public MsgService getMsgService(ProjectMember member) {
//
//		MsgService m = null;
//		try {
//			m = addAccount(new ServiceCredentials("User", "Pass"));
//		} catch (InvalidCredentialsException e) {
//			e.printStackTrace();
//		} catch (ProtocolNotSupportedException e) {
//			e.printStackTrace();
//		}
//
//		return m;
//	}
//}

public void fileModified(String relpath, ModifyActions action) {
				listener.filesChanged();
			}
		};

		this.fileListeners.put(listener, projectModificationListener);
		try {
			this.getFrontendService().getProjectsManagingService(getSessionId())
							.getFileServices(project)
							.addModificationListener(projectModificationListener);
		} catch (ProjectNotLoadedException e) {
			this.handleProjectNotLoaded(e);
		}
	}

	@Override
	public void removeFilesChangedListener(FilesChanged listener, Project project) {
		IProjectModificationListener projectModificationListener;

		projectModificationListener = this.fileListeners.get(listener);

		if (projectModificationListener != null) try {
			this.getFrontendService().getProjectsManagingService(getSessionId())
							.getFileServices(project)
							.removeModificationListener(projectModificationListener);
		} catch (ProjectNotLoadedException e) {
			this.handleProjectNotLoaded(e);
		}
	}

	private void handleProjectNotLoaded(ProjectNotLoadedException e) {
		log.error("got ProjectNotLoadedException with message " + e.getMessage());
		// TODO: enable (disabled, caused too much noise)
		//e.printStackTrace();
	}

	public long getFileSize(FileObject file) {
		//TODO implement this differently - get the Size of the STORED FileObject...
		return this.getLocalFileSize(file);
	}

	@Override
	public long getLocalFileSize(FileObject fo) {
		try {
			return this.getFrontendService().getProjectsManagingService(getSessionId())
							.getFileServices(fo.getProject()).getFileSize(fo.getRelPath());
		} catch (FileNotFoundException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (NotAFileException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (IllegalArgumentException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		} catch (IllegalStateException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (InvalidFilenameException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (ProjectNotLoadedException e) {
			this.handleProjectNotLoaded(e);
		}

		return 0;
	}

	@Override
	public Date getLocalFileLastModified(FileObject fo) {
		try {
			return new Date(
							this.getFrontendService().getProjectsManagingService(getSessionId())
											.getFileServices(fo.getProject()).getLastModified(
											fo.getRelPath()));
		} catch (NotAFileException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (IllegalArgumentException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		} catch (IllegalStateException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (InvalidFilenameException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (ProjectNotLoadedException e) {
			this.handleProjectNotLoaded(e);
		}

		return new Date();
	}

	private String currentUser = null;

	// event spread
	private List<ConnectionStatus> connectionStatus;

	private List<RegistrationStatus> registrationStatus;

	private List<ProjectChanged> projectChanged;

	private List<ErrorCallback> errorCallback;

	@Override
	public String getLockingMessage(JakeObject jakeObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSoftLocked(JakeObject jakeObject) {
		return this.MockIsSoftLockedSet.contains(jakeObject);
	}

	@Override
	public void setSoftLock(JakeObject jakeObject, boolean isSet,
													String lockingMessage) {
		if (isSet) {
			this.MockIsSoftLockedSet.add(jakeObject);
		} else {
			this.MockIsSoftLockedSet.remove(jakeObject);
		}
		this.fireProjectChanged(
						new ProjectChanged.ProjectChangedEvent(jakeObject.getProject(),
										ProjectChangedReason.Deleted));
	}

	@Override
	public ProjectMember getLockOwner(JakeObject jakeObject) {
		return new ProjectMember(UUID.randomUUID(), "Chuck Norris",
						TrustState.AUTO_ADD_REMOVE);
	}

	@Override
	public AvailableLaterObject<Boolean> login(MsgService service, String password,
																						 boolean rememberPassword) {

		/*
		 TODO: move this clutter to caller

		 try {
			AvailableLaterObject<Boolean> ret;
			JakeStatusBar.showMessage("Logging in...", 1);
			if (!ret) {
				log.warn("Wrong User/Password");
				JakeStatusBar.showMessage("Logging in unsuccessful: Wrong User/Password.", 100);
			} else {
				JakeStatusBar.showProgressAnimation(false);
				JakeStatusBar.showMessage("Successfully logged in");
				//JakeStatusBar.updateMessage();
			}

		} catch (Exception e) {
			log.warn("Login failed: " + e);
			ExceptionUtilities.showError(e);
			return new AvailableErrorObject<Boolean>(e);
		}
		 */
		return this.getFrontendService()
						.login(getSessionId(), service, password, rememberPassword);
	}


	@Override
	public File getFile(FileObject fo) throws FileOperationFailedException {
		IFriendlySyncService sync = this.frontendService.getSyncService(this.sessionId);
		try {
			return sync.getFile(fo);
		} catch (IOException e) {
			throw new FileOperationFailedException(e);
		}
	}

	/**
	 * Determine if a project member is currently online.
	 *
	 * @param member
	 * @return <code>true</code> iff the member is online.
	 */
	public boolean isOnline(ProjectMember member) {
		//TODO implement
		return false;
	}

	@Override
	public MsgService getMsgService(ProjectMember member) {
		List<MsgService> services;

		services = this.frontendService.getMsgServices(this.sessionId);

		for (MsgService s : services)
			if (member.getUserId().equals(s.getUserId().getUuid())) return s;

		return null;
	}
}
