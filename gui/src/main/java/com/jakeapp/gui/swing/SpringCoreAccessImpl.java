package com.jakeapp.gui.swing;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.services.futures.AnnounceFuture;
import com.jakeapp.core.synchronization.ISyncService;
import com.jakeapp.core.synchronization.JakeObjectSyncStatus;
import com.jakeapp.core.synchronization.exceptions.SyncException;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.core.util.availablelater.AvailableErrorObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.callbacks.*;
import com.jakeapp.gui.swing.callbacks.ErrorCallback.JakeErrorEvent;
import com.jakeapp.gui.swing.exceptions.InvalidNewFolderException;
import com.jakeapp.gui.swing.exceptions.ProjectFolderMissingException;
import com.jakeapp.gui.swing.exceptions.ProjectNotFoundException;
import com.jakeapp.gui.swing.helpers.FolderObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.CreatingSubDirectoriesFailedException;
import com.jakeapp.jake.fss.exceptions.FileAlreadyExistsException;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.util.*;

public class SpringCoreAccessImpl implements ICoreAccess {

	private static final Logger log = Logger.getLogger(SpringCoreAccessImpl.class);

	private boolean isSignedIn;

	private Map<Project, List<ProjectMember>> peopleProjectMap = new HashMap<Project, List<ProjectMember>>();

	private IFrontendService frontendService;

	private List<Project> projects = new ArrayList<Project>(); // TODO remove
	// after
	// completion of
	// this
	// implementation

	private List<Project> invitedProjects = new ArrayList<Project>(); // TODO
	// remove
	// after
	// completion
	// of
	// this
	// implementation

	/**
	 * SessionId returned by the authentication Method of
	 * FrontendService.authenticate.
	 */
	private String sessionId;

	/**
	 * Core Access Mock initialisation code
	 */
	public SpringCoreAccessImpl() {
		isSignedIn = false;
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
		return frontendService.getProjectsManagingService(sessionId).getProjectList(
				  InvitationState.ACCEPTED);
	}

	@Override
	public List<Project> getInvitedProjects() throws FrontendNotLoggedInException {
		return frontendService.getProjectsManagingService(sessionId).getProjectList(
				  InvitationState.INVITED);
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
			result = this.getFrontendService().getProjectsManagingService(this.getSessionId()).getProjectMember(project, msg);
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
			result = this.getFrontendService().getProjectsManagingService(this.getSessionId()).getProjectMemberID(project, pm);
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


	public void signIn(final String user, final String pass) {
		log.info("Signs in: " + user + "pass: " + pass);

		ServiceCredentials credentials;

		credentials = new ServiceCredentials(user, pass);
		//TODO
		//This is the only protocoltype used right now. In the future
		//there may be more...
		credentials.setProtocol(ProtocolType.XMPP);

		this.signIn(credentials);
	}

	public void signIn(final ServiceCredentials credentials) {
		Runnable runner = new Runnable() {

			public void run() {
				fireConnectionStatus(ConnectionStatus.ConnectionStati.SigningIn, "");


				try {
					MsgService toLogin = frontendService.addAccount(sessionId, credentials);
					isSignedIn = toLogin.login();

					currentUser = credentials.getUserId();
					currentMessageService = toLogin;

					fireConnectionStatus(ConnectionStatus.ConnectionStati.Online, "");
				} catch (FrontendNotLoggedInException e) {
					handleNotLoggedInException(e);
				} catch (ProtocolNotSupportedException e) {
					// this should never happen...
					log.error("Login with this protocol is not supported.", e);
				} catch (InvalidCredentialsException e) {
				} catch (Exception e) {
				}
				finally {
					if (!isSignedIn) {
						// XXX NOTIFY GUI ABOUT THIS!!
						fireConnectionStatus(ConnectionStatus.ConnectionStati.Offline, "");
					}
				}
			}
		};

		// start our runner thread, that makes callbacks to connection status
		new Thread(runner).start();
	}

	public void addConnectionStatusCallbackListener(ConnectionStatus cb) {
		log.info("Registers connection status callback: " + cb);

		connectionStatus.add(cb);
	}

	public void removeConnectionStatusCallbackListener(ConnectionStatus cb) {
		log.info("Deregisters connection status callback: " + cb);

		connectionStatus.remove(cb);
	}


	private void fireConnectionStatus(ConnectionStatus.ConnectionStati state, String str) {
		log.info("spead callback event...");
		for (ConnectionStatus callback : connectionStatus) {
			callback.setConnectionStatus(state, str);
		}
	}

	@Override
	public AvailableLaterObject<Void> createAccount(ServiceCredentials credentials, AvailabilityListener listener)
			  throws FrontendNotLoggedInException, InvalidCredentialsException,
			  ProtocolNotSupportedException, NetworkException {
		return this.frontendService.createAccount(this.sessionId, credentials, listener).start();


	}

	@Override
	public MsgService addAccount(ServiceCredentials credentials)
			  throws FrontendNotLoggedInException, InvalidCredentialsException,
			  ProtocolNotSupportedException, NetworkException {
		return this.frontendService.addAccount(this.sessionId, credentials);
	}

	@Override
	public void removeAccount(MsgService msg) throws FrontendNotLoggedInException, InvalidCredentialsException, ProtocolNotSupportedException, NetworkException {
		//TODO
	}

	public void addRegistrationStatusCallbackListener(RegistrationStatus cb) {
		log.info("Registers registration status callback: " + cb);
	}

	public void removeRegistrationStatusCallbackListener(RegistrationStatus cb) {
		log.info("Deregisters registration status callback: " + cb);
	}


	public boolean isSignedIn() {
		return isSignedIn;
	}


	public void signOut() {
		if (!this.isSignedIn()) {


			try {
				this.frontendService.signOut(this.sessionId);
			} catch (FrontendNotLoggedInException e) {
				//silently discard invalid session
			}

			this.isSignedIn = false;
			this.currentUser = null;
			this.currentMessageService = null;
		}

		fireConnectionStatus(ConnectionStatus.ConnectionStati.Offline, "");
	}

	public String[] getLastSignInNames() {
		Collection<ServiceCredentials> credentials;
		String[] result;
		int i = 0;

		credentials = this.getFrontendService().getLastLogins();
		result = new String[credentials.size()];

		for (ServiceCredentials credential : credentials) {
			result[i] = credential.getUserId();

			i++;
		}

		return result;
	}


	public void addProjectChangedCallbackListener(ProjectChanged cb) {
		log.info("Register project changed callback: " + cb);

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
			this.getFrontendService().getProjectsManagingService(this.getSessionId()).stopProject(project);
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
		try {
			this.getFrontendService().getProjectsManagingService(this.getSessionId()).stopProject(project);
		} catch (IllegalArgumentException e) {
			log.debug("Illegal project for starting specified.", e);
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


	public AvailableLaterObject<Integer> getProjectFileCount(Project project, AvailabilityListener listener) {
		AvailableLaterObject<Integer> result = null;
		Exception ex = null;

		try {
			result = this.getFrontendService().getProjectsManagingService(this.getSessionId()).getProjectFileCount(project, listener);
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

		if (result == null) result = new AvailableErrorObject<Integer>(listener, ex);
		return result.start();
	}

	public AvailableLaterObject<Long> getProjectSizeTotal(Project project, AvailabilityListener listener) {
		AvailableLaterObject<Long> result = null;
		Exception ex = null;

		try {
			result = this.getFrontendService().getProjectsManagingService(this.getSessionId()).getProjectSizeTotal(project, null);
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
			ex = e;
		}
		catch (Exception e) {
			ex = e;
		}

		if (result == null) result = new AvailableErrorObject<Long>(listener, ex);
		return result.start();
	}


	public void deleteProject(final Project project, boolean deleteProjectFiles) {
		log.info("Delete project: " + project);

		if (project == null) {
			throw new IllegalArgumentException("Cannot delete empty project!");
		}


		Runnable runner = new Runnable() {

			public void run() {
				boolean ret;

				try {
					// search project in list
					ret = getFrontendService().getProjectsManagingService(getSessionId()).deleteProject(project);

					if (!ret) {
						throw new ProjectNotFoundException("Project not found in list!");
					}

					fireProjectChanged(new ProjectChanged.ProjectChangedEvent(
							  project,
							  ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Deleted));

				} catch (FrontendNotLoggedInException e) {
					handleNotLoggedInException(e);
				} catch (RuntimeException run) {
					fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
				} catch (FileNotFoundException e) {
					//TODO report to gui that the rootpath is invalid
					log.warn("Project cannot be deleted: its folder does not exist.", e);
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
					getFrontendService().getProjectsManagingService(getSessionId()).joinProject(project, project.getUserId());

					fireProjectChanged(new ProjectChanged.ProjectChangedEvent(
							  project,
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
					getFrontendService().getProjectsManagingService(getSessionId()).rejectProject(project, project.getUserId());

					fireProjectChanged(new ProjectChanged.ProjectChangedEvent(
							  project,
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

	public void setProjectName(Project project, String prName) {
		try {
			this.getFrontendService().getProjectsManagingService(this.getSessionId()).updateProjectName(project, prName);
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
			IProjectsManagingService pms = this.getFrontendService().getProjectsManagingService(getSessionId());
			fo = recursiveFileSystemHelper(project, "", "", pms.getFileServices(project), pms);
		} catch (IllegalArgumentException e) {
			//empty implementation
		} catch (IllegalStateException e) {
			//empty implementation
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		}

		return fo;
	}

	@Override
	public AvailableLaterObject<List<FileObject>> getAllProjectFiles(Project project, AvailabilityListener al) {
		AvailableLaterObject<List<FileObject>> result = null;
		Exception ex = null;

		try {
			result = this.getFrontendService().getProjectsManagingService(sessionId).getAllProjectFiles(project, al);
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

		if (result == null) result = new AvailableErrorObject<List<FileObject>>(al, ex);
		return result.start();
	}

	@Override
	public JakeObjectSyncStatus getJakeObjectSyncStatus(Project project, FileObject file) {
		//TODO this is only a mock yet...
		return new JakeObjectSyncStatus(file.getAbsolutePath().toString(), 0, false, false, false, false);
	}

	/**
	 * Helper method: Works recursively through the file system to
	 * build the FolderObject for getProjectRootFolder()
	 *
	 * @param prj	  The project the created Folder Object is in.
	 * @param relPath a Project-relative path to the returned folder object.
	 * @param name	 the name of the current folder. It is the last part of relPath
	 * @param fss	  The FileSystemService used to access the Project-Folder
	 * @return
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *
	 * @throws IllegalStateException
	 * @throws IllegalArgumentException
	 */
	private FolderObject recursiveFileSystemHelper(Project prj, String relPath, String name, IFSService fss, IProjectsManagingService pms) throws IllegalArgumentException, IllegalStateException, FrontendNotLoggedInException {
		FolderObject fo = new FolderObject(relPath, name);
		try {
			for (String f : fss.listFolder(relPath)) {
				// f is a valid relpath
				try {
					if (fss.fileExists(f)) {
						//is an ordinary file
						//get an appropriate FileObject - with the real UUID
						//The UUid may still be null e.g. if the file only exists locally
						//and has just been detected.
						fo.addFile(
								  pms.getFileObjectByRelPath(prj, f)
						);
					} else if (fss.folderExists(relPath)) {
						FolderObject subfolder = recursiveFileSystemHelper(prj, f, fss.getFileName(f), fss, pms);
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

	public List<NoteObject> getNotes(Project project) throws FrontendNotLoggedInException,
			  ProjectNotLoadedException {
		return this.frontendService.getProjectsManagingService(this.sessionId).getNoteManagingService(project).getNotes();
	}

	@Override
	public Date getLastEdit(NoteObject note) {
		Date result = null;

		try {
			result = this.getFrontendService().getProjectsManagingService(this.getSessionId()).getLastEdit(note);
		} catch (Exception e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		}

		return result;
	}

	@Override
	public ProjectMember getLastEditor(NoteObject note) {
		ProjectMember member = null;
		try {
			member = this.getFrontendService().getProjectsManagingService(this.getSessionId()).getLastEditor(note);
		} catch (Exception e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		}

		return member;
	}

	@Override
	public boolean isLocalNote(NoteObject note) {
		boolean result;

		try {
			result = this.frontendService.getProjectsManagingService(
					  this.sessionId).getNoteManagingService(note.getProject())
					  .isLocalNote(note);
		} catch (Exception e) {
			// Every error allows only one interpretation: a Note that cannot
			// be checked wether it is local is NOT local!
			result = false;
		}

		return result;
	}

	@Override
	public void deleteNote(NoteObject note) {
		IProjectsManagingService pms;
		ProjectMember member;

		try {
			pms = this.frontendService.getProjectsManagingService(this.getSessionId());
			member = pms.getProjectMember(note.getProject(), this.getLoggedInUser(this.getSessionId()));

			pms.getNoteManagingService(note.getProject()).deleteNote(note, member);
		} catch (IllegalArgumentException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (IllegalStateException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (ProjectNotLoadedException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		} catch (NoSuchJakeObjectException e) {
			// The corresponding JakeObject does not exist any more - there is
			// no
			// need deleting it therefore we simply discard this exception.
		}
	}

	@Override
	public void newNote(NoteObject note) {
		try {
			this.frontendService
					  .getProjectsManagingService(this.getSessionId())
					  .getNoteManagingService(note.getProject()).addNote(note);
		} catch (IllegalArgumentException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (IllegalStateException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (ProjectNotLoadedException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		}
	}

	@Override
	public void saveNote(NoteObject note) {
		try {
			this.frontendService
					  .getProjectsManagingService(this.getSessionId())
					  .getNoteManagingService(note.getProject()).saveNote(note);
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		} catch (IllegalArgumentException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (IllegalStateException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		} catch (ProjectNotLoadedException e) {
			this.fireErrorListener(new JakeErrorEvent(e));
		}
	}

	// TODO this might be removed in further versions...
	// it depends on only having a SINGLE user.
	private MsgService getLoggedInUser(String session) throws FrontendNotLoggedInException {
		return this.currentMessageService;
	}

	/**
	 * Generates a list so that people are remembered when they change.
	 *
	 * @param project : project that should be evaluated
	 * @return
	 */
	public List<ProjectMember> getPeople(Project project) {
		//TODO implement!!

		log.info("getPeople from project " + project);

		if (project == null) {
			return new ArrayList<ProjectMember>();
		}
		return new ArrayList<ProjectMember>();

		//this.getFrontendService().getProjectsManagingService(this.getSessionId()).getProjectMembers(project);

		/*
		if (!peopleProjectMap.containsKey(project)) {
			List<ProjectMember> people = new ArrayList<ProjectMember>();

			// TODO: fix
			people.add(new ProjectMember(new UUID(11, 22), "Nickname",
					  TrustState.AUTO_ADD_REMOVE));



								 * people.add(new ProjectMember(new XMPPUserId(new
								 * ServiceCredentials("User1", "pass2"), new UUID(22, 33),
								 * "pstein@jabber.fsinf.at", "", "Peter", "Steinberger"),
								 * TrustState.TRUST));
								 *
								 * people.add(new ProjectMember(new XMPPUserId(new
								 * ServiceCredentials("User2", "pass2"), new UUID(222, 333),
								 * "test@jabber.org", "Pr-" + project.getName(), "ProjectTestUser",
								 * project.getName()), TrustState.AUTO_ADD_REMOVE));
								 *
								 *
								 * people.add(new ProjectMember(new XMPPUserId(new
								 * ServiceCredentials("User3", "pass3"), new UUID(22, 33),
								 * "max@jabber.org", "Max", "Max", "Mustermann"),
								 * TrustState.NO_TRUST));
								 
			peopleProjectMap.put(project, people);
		}

		return peopleProjectMap.get(project);
		*/
	}

	@Override
	public boolean setPeopleNickname(Project project, ProjectMember pm, String nick) {
		// TODO: ignore this and create a regex for checking!
		if (nick.indexOf("<") != -1) {
			return false;
		} else {
			pm.setNickname(nick);

			fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
					  ProjectChanged.ProjectChangedEvent.ProjectChangedReason.People));

			return true;
		}
	}

	@Override
	public void peopleSetTrustState(Project project, ProjectMember member,
											  TrustState trust) {
		member.setTrustState(trust);

		fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
				  ProjectChanged.ProjectChangedEvent.ProjectChangedReason.People));
	}


	@Override
	public void invitePeople(Project project, String userid) {
		log.info("Mock: invite pm: " + userid + " to project: " + project);

		// TODO: implement the invite

	}

	@Override
	public List<ProjectMember> getSuggestedPeople(Project project) {
		List<ProjectMember> members = new ArrayList<ProjectMember>();
		members.add(getPeople(project).get(0));
		// members.add(getPeople(project).get(1));
		return members;
	}


	@Override
	public List<LogEntry> getLog(Project project, JakeObject jakeObject, int entries) {
		// TODO: implement!
		List<LogEntry> log = new ArrayList<LogEntry>();

		if (project != null) {
			/*
			// yeah... what a beautiful interface ;o)
			log.add(new LogEntry(new UUID(1, 2), LogAction.PROJECT_CREATED, new Date(),
					  project, null, getPeople(project).get(0), "comment 1", "checksum???",
					  true));

			log.add(new LogEntry(new UUID(1, 2), LogAction.JAKE_OBJECT_NEW_VERSION, new Date(), project,
					  null, getPeople(project).get(0), "comment 1", "checksum???", true));
					  */
		}

		return log;
	}


	public void createProject(final String name, final String path) {
		log.info("Create project: " + name + " path: " + path);

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
					pr = frontendService.getProjectsManagingService(sessionId).createProject(name, path, null);

					/*
																				 * The project is created, but not started and no user is assigned to it.
																				 */

					fireProjectChanged(new ProjectChanged.ProjectChangedEvent(
							  pr,
							  ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Created));

				} catch (FrontendNotLoggedInException e) {
					log.debug("Tried to create a project while not authenticated to the core.", e);
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


	private void fireRegistrationStatus(RegistrationStatus.RegisterStati state, String str) {
		for (RegistrationStatus callback : registrationStatus) {
			callback.setRegistrationStatus(state, str);
		}
	}

	@Override
	public Date getFileLastModified(FileObject file) {
		// TODO: Make this useful
		return new Date();
	}

	@Override
	public AvailableLaterObject<Void> importExternalFileFolderIntoProject(List<File> files, String destFolderRelPath) {
		//TODO
		return null;
	}


	@Override
	public ProjectMember getLastModifier(JakeObject jakeObject) throws NoSuchLogEntryException {
		//TODO
		return null;
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
		// TODO: Implement me
		return null;
	}

	@Override
	public void setTagsForFileObject(FileObject fo, Set<Tag> tags) {
		// TODO: Implement me
	}

	/**
	 * Renames a file
	 *
	 * @param fromPath relative Path of source
	 * @param project  Project to retrieve a FSService for - fromPath should be in the Project.
	 * @param newName  new name
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
		}
	}

	@Override
	public AvailableLaterObject<Void> announceJakeObject(JakeObject jo, String commitmsg) throws SyncException, FrontendNotLoggedInException {
		ProjectMember member;
		LogEntry<JakeObject> action;
		ISyncService iss;
		AvailableLaterObject<Void> result = null;

		member = this.getFrontendService().getProjectsManagingService(this.getSessionId()).getProjectMember(jo.getProject(), this.getLoggedInUser(this.getSessionId()));

		action = new LogEntry<JakeObject>(
				  UUID.randomUUID(), LogAction.JAKE_OBJECT_NEW_VERSION,
				  Calendar.getInstance().getTime(), jo.getProject(), jo, member
		);

		try {
			iss = this.getFrontendService().getSyncService(this.getSessionId());
			result = new AnnounceFuture(null, iss, jo, action);
		} catch (FrontendNotLoggedInException e) {
			this.handleNotLoggedInException(e);
		}

		return result.start();
	}

	@Override
	public void pullJakeObject(JakeObject jo) throws FrontendNotLoggedInException, OtherUserOfflineException, NoSuchObjectException, NoSuchLogEntryException {
		//TODO implement
	}

	@Override
	public boolean isJakeObjectLocked(JakeObject jo) {
		return false;  //TODO change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void createNewFolderAt(Project project, String relpath, String folderName) throws InvalidNewFolderException {
		//TODO change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void addFilesChangedListener(FilesChanged listener) {
		//TODO change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void removeFilesChangedListener(FilesChanged listener) {
		//TODO change body of implemented methods use File | Settings | File Templates.
	}

	public long getFileSize(FileObject file) {
		//TODO
		return 1234;
	}

	@Override
	public long getLocalFileSize(FileObject fo) {
		//TODO
		return 0;
	}

	@Override
	public Date getLocalFileLastModified(FileObject fo) {
		//TODO
		return null;
	}

	private String currentUser = null;
	private MsgService currentMessageService = null;

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSoftLock(JakeObject jakeObject, boolean isSet, String lockingMessage) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ProjectMember getLockOwner(JakeObject jakeObject) {
		// TODO Auto-generated method stub
		return null;
	}
}
