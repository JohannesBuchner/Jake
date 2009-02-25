package com.jakeapp.gui.swing;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NoSuchMsgServiceException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.domain.exceptions.UserIdFormatException;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.services.futures.AnnounceFuture;
import com.jakeapp.core.services.futures.PullFuture;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.core.synchronization.ChangeAdapter;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.synchronization.ISyncService;
import com.jakeapp.core.synchronization.UserInfo;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.util.availablelater.AvailableErrorObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.callbacks.ConnectionStatus;
import com.jakeapp.gui.swing.callbacks.ErrorCallback;
import com.jakeapp.gui.swing.callbacks.ErrorCallback.JakeErrorEvent;
import com.jakeapp.gui.swing.callbacks.FilesChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged.ProjectChangedEvent.ProjectChangedReason;
import com.jakeapp.gui.swing.callbacks.RegistrationStatus;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.exceptions.InvalidNewFolderException;
import com.jakeapp.gui.swing.exceptions.NoteOperationFailedException;
import com.jakeapp.gui.swing.exceptions.PeopleOperationFailedException;
import com.jakeapp.gui.swing.exceptions.ProjectCreationException;
import com.jakeapp.gui.swing.exceptions.ProjectFolderMissingException;
import com.jakeapp.gui.swing.exceptions.ProjectNotFoundException;
import com.jakeapp.gui.swing.helpers.FolderObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.IModificationListener.ModifyActions;
import com.jakeapp.jake.fss.IProjectModificationListener;
import com.jakeapp.jake.fss.exceptions.CreatingSubDirectoriesFailedException;
import com.jakeapp.jake.fss.exceptions.FileAlreadyExistsException;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotADirectoryException;
import com.jakeapp.jake.fss.exceptions.NotAFileException;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SpringCoreAccessImpl implements ICoreAccess {
	private static final Logger log = Logger.getLogger(SpringCoreAccessImpl.class);
	private IFrontendService frontendService;
	private IProjectsManagingService pms;

	/**
	 * SessionId returned by the authentication Method of
	 * FrontendService.authenticate.
	 */
	private String sessionId;

	private Map<FilesChanged, IProjectModificationListener> fileListeners =
					new HashMap<FilesChanged, IProjectModificationListener>();
	// event spread
	private List<ConnectionStatus> connectionStatus;
	private List<RegistrationStatus> registrationStatus;
	private List<ProjectChanged> projectChanged;
	private List<ErrorCallback> errorCallback;


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

		return pms.getProjectList(InvitationState.ACCEPTED);
	}

	@Override
	public List<Project> getInvitedProjects() throws FrontendNotLoggedInException {
		return pms.getProjectList(InvitationState.INVITED);
	}


	@Override
	public void setFrontendService(IFrontendService frontendService) {
		this.frontendService = frontendService;
	}

	private void handleNotLoggedInException(FrontendNotLoggedInException e) {
		log.warn("Tried access core without a session", e);
	}

	@Override
	public void authenticateOnBackend(Map<String, String> authenticationData)
					throws InvalidCredentialsException {
		this.sessionId = this.frontendService.authenticate(authenticationData);

		// also cache the pms
		pms = frontendService.getProjectsManagingService(this.sessionId);
	}

	@Override
	public void backendLogOff() {
		try {
			this.frontendService.logout(this.sessionId);
		} catch (FrontendNotLoggedInException e) {
			log.warn("Frontent not logged in", e);
		}
		this.sessionId = "";
	}

	@Override
	public List<MsgService<UserId>> getMsgServics()
					throws FrontendNotLoggedInException {
		return this.frontendService.getMsgServices(this.sessionId);
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
								 ProtocolNotSupportedException, NetworkException,
								 NoSuchMsgServiceException {

		log.warn("removeAccount: " + msg + " NOT IMPLEMENTED YET");

		this.frontendService.removeAccount(this.sessionId, msg);
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
		log.info("Starting project: " + project);

		// HACK: create ChangeListener and add to
		try {

			ChangeListener syncChangeListener = new ChangeAdapter();

			// actual project start
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

		if (result == null)
			result = new AvailableErrorObject<Integer>(ex);
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

		if (result == null)
			result = new AvailableErrorObject<Long>(ex);
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
				} catch (IOException e) {
					//report to gui that the rootpath is invalid
					log.warn("Project cannot be deleted:", e);
					fireErrorListener(new ErrorCallback.JakeErrorEvent(e));
				} catch (NotADirectoryException e) {
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

		if (path == null)
			throw new IllegalArgumentException();

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
		} catch (NoSuchProjectException e) {
			e.printStackTrace();
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
	public FolderObject getFolder(Project project, FolderObject parent)
					throws ProjectFolderMissingException {
		log.info("get root folder for " + project);

		// currently disabled, until i fix this.
		/*

		String rootPath = project.getRootPath();
		File rootFolder = new File(rootPath);
		if (!rootFolder.exists()) {
			throw new ProjectFolderMissingException(rootPath);
		}

		FolderObject fo = null;
		try {
			IProjectsManagingService pms = this.getFrontendService()
							.getProjectsManagingService(getSessionId());
			fo = recursiveFileSystemHelper(project, "", "", pms.getFileServices(project),
							pms);
		} catch (IllegalArgumentException e) {
			log.warn("Error creating FolderObject", e);
		} catch (IllegalStateException e) {
			log.warn("Error creating FolderObject", e);
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		} catch (ProjectNotLoadedException e) {
			this.handleProjectNotLoaded(e);
		}

		return fo;
		*/
		return null;
	}

	@Override
	public AvailableLaterObject<List<FileObject>> getFiles(Project project) {
		log.debug("Calling getFiles");
		AvailableLaterObject<List<FileObject>> result = null;
		Exception ex = null;

		try {
			result = this.frontendService.getSyncService(this.sessionId).getFiles(project);
		} catch (IOException e) {
			ex = new FileOperationFailedException(e);
		}

		if (result == null) {
			log.debug("getFiles failed, returning error", ex);
			result = new AvailableErrorObject<List<FileObject>>(ex);
		}

		log.debug("getFiles - start AvailableLaterObject");
		return result.start();
	}


	@Override
	public <T extends JakeObject> Attributed<T> getJakeObjectSyncStatus(
					Project project, T jakeObject) {
		try {
			return this.getFrontendService().getSyncService(getSessionId())
							.getJakeObjectSyncStatus(jakeObject);
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
		} catch (Exception e) {
			log.warn("Catched generic exception while getting sync status", e);
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
					String name, IFSService fss, IProjectsManagingService pms)
					throws IllegalArgumentException, IllegalStateException,
								 FrontendNotLoggedInException {
		FolderObject fo = new FolderObject(relPath, name, prj);
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
						log.debug(f);

						fo.addFile(pms.getFileObjectByRelPath(prj, f));
					} else if (fss.folderExists(relPath)) {
						FolderObject subfolder =
										recursiveFileSystemHelper(prj, f, fss.getFileName(f), fss, pms);
						fo.addFolder(subfolder);
					}
				} catch (InvalidFilenameException e) {
					log.info("Invalid Filename", e);
				} catch (IOException e) {
					log.info("Generic File Exception", e);
				} catch (NoSuchJakeObjectException e) {
					log.warn("FileObject not found?", e);
				}
			}
		} catch (InvalidFilenameException e) {
			log.info("Invalid Filename", e);
		} catch (IOException e) {
			log.info("Generic File Exception", e);
		}

		return fo;
	}

	public AvailableLaterObject<List<Attributed<NoteObject>>> getNotes(
					final Project project) {

		// FIXME: exception handling?
		return new AvailableLaterObject<List<Attributed<NoteObject>>>() {
			@Override public List<Attributed<NoteObject>> calculate() throws Exception {
				try {
					// FIXME: do this over the SyncService. You also get AttributedJakeObjects
					return frontendService.getSyncService(sessionId).getNotes(project);
				} catch (Exception e) {
					throw new NoteOperationFailedException(e);
				}
			}
		}.start();
	}


	@Override
	public void createNote(NoteObject note) throws NoteOperationFailedException {
		try {
			pms.saveNote(note);
		} catch (Exception e) {
			throw new NoteOperationFailedException(e);
		}
		this.fireProjectChanged(new ProjectChanged.ProjectChangedEvent(note.getProject(),
						ProjectChangedReason.State));
	}

	@Override
	public void deleteNote(NoteObject note) throws NoteOperationFailedException {
		try {
			pms.deleteNote(note);
		} catch (Exception e) {
			throw new NoteOperationFailedException(e);
		}
	}

	@Override
	public void saveNote(NoteObject note) throws NoteOperationFailedException {
		pms.saveNote(note);

		this.fireProjectChanged(new ProjectChanged.ProjectChangedEvent(note.getProject(),
						ProjectChangedReason.State));
	}


	/**
	 * Generates a list so that people are remembered when they change.
	 *
	 * @param project : project that should be evaluated
	 * @return list of people in this project OR empty list.
	 */
	public List<UserInfo> getProjectUser(Project project)
					throws PeopleOperationFailedException {
		log.info("getProjectUser from project " + project);

		List<UserInfo> users = new ArrayList<UserInfo>();

		if (project == null) {
			log.warn("Get People for empty UserID.");
			return users;
		}

		try {
			return pms.getProjectUserInfos(project);
		} catch (NoSuchProjectException ex) {
			throw new PeopleOperationFailedException(ex);
		}
	}

	@Override
	public UserInfo getUserInfo(UserId user) {
		return pms.getProjectUserInfo(JakeMainApp.getProject(), user);
	}

	@Override
	public boolean setPeopleNickname(Project project, UserId userId, String nick) {
		log.info(
						"setPeopleNickname: project: " + project + " ProjectMember: " + userId + " Nick: " + nick);

		// TODO: ignore this and create a regex for checking!
		if (nick.indexOf("<") != -1) {
			return false;
		} else {
			pms.setUserNickname(project, userId, nick);

			fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
							ProjectChanged.ProjectChangedEvent.ProjectChangedReason.People));

			return true;
		}
	}

	@Override
	public void peopleSetTrustState(Project project, UserId userId, TrustState trust) {

		pms.setUserTrustState(project, userId, trust);

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
	public List<UserId> getSuggestedUser(Project project) {
		List<UserId> members = null;

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
		return (members == null) ? new ArrayList<UserId>() : members;
	}


	@Override
	public List<LogEntry<? extends ILogable>> getLog(Project project,
					JakeObject jakeObject, int entries) {
		log.info(
						"getLog pr:" + project + " jakeObject: " + jakeObject + " entries: " + entries);

		List<LogEntry<? extends ILogable>> logs;

		if (jakeObject == null) {
			logs = pms.getLog(project);
		} else {
			logs = pms.getLog(jakeObject);
		}

		// filter log list?
		if (logs.size() > entries) {
			return logs.subList(0, entries);
		} else {
			return logs;
		}
	}


	public void createProject(final String name, final String path,
					final MsgService msg) throws ProjectCreationException {
		log.info("Create project: " + name + " path: " + path + " msg: " + msg);

		// preconditions
		if (path == null) {
			throw new NullPointerException("Path cannot be null.");
		}

		Runnable runner = new Runnable() {
			public void run() {
				Project project;

				try {
					// add Project to core-internal list
					project = pms.createProject(name, path, msg);

					fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
									ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Created));

				} catch (FrontendNotLoggedInException e) {
					log.debug("Tried to create a project while not authenticated to the core.",
									e);
				} catch (RuntimeException run) {
					fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
				} catch (Exception e) {
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
	public AvailableLaterObject<Void> importExternalFileFolderIntoProject(
					Project project, List<File> files, String destFolderRelPath) {

		// TODO: implement

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


	@Override
	public void deleteToTrash(Project project, String relpath) {
		// TODO: What do we do with folders?
		try {
			// TODO: announce deletion
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
		}
	}

	@Override
	public void rename(FileObject file, String newName) {
		this.renamePath(file.getRelPath(), file.getProject(), newName);
		//TODO change relpath - enable setRelPath
	}

	@Override
	public void rename(FolderObject folder, String newName) {
		this.renamePath(folder.getRelPath(), folder.getProject(), newName);
		//TODO change relpath

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
		}
	}

	@Override
	public AvailableLaterObject<Void> announceJakeObject(JakeObject jo,
					String commitmsg) throws FileOperationFailedException {
		ISyncService iss;
		AvailableLaterObject<Void> result;

		try {
			iss = this.getFrontendService().getSyncService(this.getSessionId());
			result = new AnnounceFuture(iss, jo, LogAction.JAKE_OBJECT_NEW_VERSION);
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
		}
	}

	@Override
	public void addFilesChangedListener(final FilesChanged listener, Project project) {
		IProjectModificationListener projectModificationListener =
						new IProjectModificationListener() {

							public void fileModified(String relpath, ModifyActions action) {
								listener.filesChanged();
							}
						};

		this.fileListeners.put(listener, projectModificationListener);
		this.getFrontendService().getProjectsManagingService(getSessionId())
						.getFileServices(project)
						.addModificationListener(projectModificationListener);
	}

	@Override
	public void removeFilesChangedListener(FilesChanged listener, Project project) {
		IProjectModificationListener projectModificationListener;

		projectModificationListener = this.fileListeners.get(listener);

		if (projectModificationListener != null)
			this.getFrontendService().getProjectsManagingService(getSessionId())
							.getFileServices(project)
							.removeModificationListener(projectModificationListener);
	}

	private void handleProjectNotLoaded(ProjectNotLoadedException e) {
		log.error("got ProjectNotLoadedException with message " + e.getMessage());
		e.printStackTrace();
	}

	@Override
	public long getLocalFileSize(FileObject fo) {
		try {
			//since the method only checks local files, it fails for files
			//that only exist remotely
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
		}

		return 0;
	}

	@Override
	public Date getLocalFileLastModified(FileObject fo) {
		try {
			//since the method only checks local files, it fails for files
			//that only exist remotely
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
		}
		return new Date();
	}


	@Override
	public void setSoftLock(JakeObject jakeObject, boolean isSet,
					String lockingMessage) {
		if (isSet) {
			this.getFrontendService().getProjectsManagingService(getSessionId())
							.lock(jakeObject, lockingMessage);
		} else {
			this.getFrontendService().getProjectsManagingService(getSessionId())
							.unlock(jakeObject, lockingMessage);
		}

		this.fireProjectChanged(
						new ProjectChanged.ProjectChangedEvent(jakeObject.getProject(),
										ProjectChangedReason.Deleted));
	}


	@Override
	public AvailableLaterObject<Boolean> login(MsgService service, String password,
					boolean rememberPassword) {

		return this.getFrontendService()
						.login(getSessionId(), service, password, rememberPassword);
	}


	@Override
	public File getFile(FileObject fo) throws FileOperationFailedException {
		log.debug("getFile: fo: " + fo + " in pr: " + (fo != null ? fo.getProject() :
						null));

		// no need to go in iss if everything is null.
		if (fo == null) {
			log.warn("Tried to get a File with FileObject Null");
			return null;
		}

		IFriendlySyncService sync = this.frontendService.getSyncService(this.sessionId);
		try {
			return sync.getFile(fo);
		} catch (Exception e) {
			throw new FileOperationFailedException(e);
		}
	}
}