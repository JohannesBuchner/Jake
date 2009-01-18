package com.jakeapp.core.services;

import com.jakeapp.core.dao.IServiceCredentialsDao;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.synchronization.ISyncService;
import com.jakeapp.core.synchronization.JakeObjectSyncStatus;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAFileException;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Implementation of the FrontendServiceInterface
 */
public class FrontendServiceImpl implements IFrontendService {

	private static Logger log = Logger.getLogger(FrontendServiceImpl.class);

	private IProjectsManagingService projectsManagingService;

	private MsgServiceFactory msgServiceFactory;

	private Map<String, FrontendSession> sessions;

	/* this is hardwired because there will always be only one sync. EVVAAR!! */
	private IFriendlySyncService sync;

	/**
	 * Constructor
	 *
	 * @param projectsManagingService
	 * @param msgServiceFactory
	 * @param sync
	 */
	public FrontendServiceImpl(IProjectsManagingService projectsManagingService,
										MsgServiceFactory msgServiceFactory, IFriendlySyncService sync) {
		this.setProjectsManagingService(projectsManagingService);
		this.setSessions(new HashMap<String, FrontendSession>());
		this.msgServiceFactory = msgServiceFactory;
		this.sync = sync;
	}

	private IProjectsManagingService getProjectsManagingService() {
		return projectsManagingService;
	}

	private IServiceCredentialsDao getServiceCredentialsDao() {
		//TODO retrieve the dao
		return null;
	}

	private void setProjectsManagingService(
			  IProjectsManagingService projectsManagingService) {
		this.projectsManagingService = projectsManagingService;
	}

	/**
	 * Checks frontend-credentials and throws exceptions if they are not
	 * correct.
	 *
	 * @param credentials The credentials to be checked
	 * @throws com.jakeapp.core.domain.exceptions.InvalidCredentialsException
	 *
	 * @throws IllegalArgumentException
	 * @see #authenticate(Map)
	 */
	private void checkCredentials(Map<String, String> credentials)
			  throws IllegalArgumentException, InvalidCredentialsException {

		if (credentials == null)
			throw new IllegalArgumentException();

		// TODO do further checking for later versions
	}

	private void setSessions(Map<String, FrontendSession> sessions) {
		this.sessions = sessions;
	}

	private Map<String, FrontendSession> getSessions() {
		return sessions;
	}

	private void addSession(String sessid, FrontendSession session) {
		this.getSessions().put(sessid, session);
	}

	private boolean removeSession(String sessid) {
		FrontendSession fes;

		fes = this.getSessions().remove(sessid);

		return fes != null;
	}

	/**
	 * @param sessionId The id associated with the session after it was created
	 * @throws IllegalArgumentException if <code>sessionId</code> was null
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *                                  if no Session associated with <code>sessionId</code> exists.
	 */
	private FrontendSession getSession(String sessionId) throws IllegalArgumentException,
			  FrontendNotLoggedInException {
		checkSession(sessionId);
		return this.getSessions().get(sessionId);
	}

	/**
	 * @return A sessionid for a new session
	 */
	private String makeSessionID() {
		return UUID.randomUUID().toString();
	}


	@Override
	public String authenticate(Map<String, String> credentials)
			  throws IllegalArgumentException, InvalidCredentialsException {
		String sessid;

		this.checkCredentials(credentials);

		// create new session
		sessid = makeSessionID();
		this.addSession(sessid, new FrontendSession());

		return sessid;
	}

	@Override
	public boolean logout(String sessionId) throws IllegalArgumentException,
			  FrontendNotLoggedInException {
		boolean successfullyRemoved;

		if (sessionId == null)
			throw new IllegalArgumentException();

		successfullyRemoved = this.removeSession(sessionId);
		if (!successfullyRemoved)
			throw new FrontendNotLoggedInException();

		return true;
	}

	@Override
	public IProjectsManagingService getProjectsManagingService(String sessionId)
			  throws IllegalArgumentException, FrontendNotLoggedInException, IllegalStateException {
		checkSession(sessionId);
		// 3. return ProjectsManagingService
		return this.getProjectsManagingService();
	}

	private void checkSession(String sessionId) throws FrontendNotLoggedInException {


		// 1. check if session is null, if so throw IllegalArgumentException
		if (sessionId == null || sessionId.isEmpty())
			throw new IllegalArgumentException("invalid sessionid");

		// 2. check session validity
		if (!sessions.containsKey(sessionId)) {
			log.debug("sessions dont contain ssesionid " + sessionId);
			log.debug("this are the stored sessions ");
			for (String entry : sessions.keySet()) {
				log.debug(entry);
			}
			throw new FrontendNotLoggedInException("Invalid Session; Not logged in");
		}

	}

	@Override
	public List<MsgService> getMsgServices(String sessionId)
			  throws IllegalArgumentException, FrontendNotLoggedInException, IllegalStateException {
		this.checkSession(sessionId);
		return this.getMsgServices();
	}

	private List<MsgService> getMsgServices() throws IllegalStateException {
		return this.msgServiceFactory.getAll();
	}

	@Override
	public AvailableLaterObject<Void> createAccount(String sessionId, ServiceCredentials credentials, AvailabilityListener listener)
			  throws FrontendNotLoggedInException, InvalidCredentialsException,
			  ProtocolNotSupportedException, NetworkException {
		checkSession(sessionId);
		return msgServiceFactory.createAccount(credentials, listener);
	}

	@Override
	public MsgService addAccount(String sessionId, ServiceCredentials credentials)
			  throws FrontendNotLoggedInException, InvalidCredentialsException,
			  ProtocolNotSupportedException {
		checkSession(sessionId);

		return msgServiceFactory.addMsgService(credentials);
	}

	@Override
	public void signOut(String sessionId) throws FrontendNotLoggedInException {
		FrontendSession session;
		Iterable<MsgService> msgServices;

		this.checkSession(sessionId);
		this.getSessions().remove(sessionId);

		/* do not logout - other UIs may access the core */
	}

	@Override
	public void ping(String sessionId) throws IllegalArgumentException,
			  FrontendNotLoggedInException {
		checkSession(sessionId);

		// TODO
	}

	@Override
	public ISyncService getSyncService(String sessionId)
			  throws FrontendNotLoggedInException {
		this.checkSession(sessionId);
		return this.sync;
	}


	//	@Override
	public IFriendlySyncService getSync() {
		return this.sync;
	}

	@Override
	@Transactional
	public Collection<ServiceCredentials> getLastLogins() {
		return this.getServiceCredentialsDao().getAll();
	}

	@Override
	public JakeObjectSyncStatus getJakeObjectSyncStatus(String sessionId,
			Project project, FileObject file) throws InvalidFilenameException, FileNotFoundException, NotAReadableFileException {
		ISyncService iss = this.getSyncService(sessionId);
		IProjectsManagingService pms = this.getProjectsManagingService(sessionId);
		IFSService fss = pms.getFileServices(project);
		
		
		boolean locallyModified = !file.getChecksum().equals(fss.calculateHashOverFile(file.getRelPath())),
			remotelyModified = !iss.localIsNewest(file),
			onlyLocal = pms.isLocalJakeObject(file),
			onlyRemote = false;
		
		//TODO implement onlyRemote - how?
		return new JakeObjectSyncStatus(
			file.getAbsolutePath().toString(),fss.getLastModified(file.getRelPath()),
			locallyModified,remotelyModified,onlyLocal,onlyRemote
		);
	}
}
