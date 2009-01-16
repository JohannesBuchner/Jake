package com.jakeapp.core.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.core.dao.IServiceCredentialsDao;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NotLoggedInException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.synchronization.SyncServiceImpl;
import com.jakeapp.jake.ics.ICService;

/**
 * Implementation of the FrontendServiceInterface
 */
public class FrontendServiceImpl implements IFrontendService, InternalFrontendService {

	private static Logger log = Logger.getLogger(FrontendServiceImpl.class);

	private IProjectsManagingService projectsManagingService;

	private MsgServiceFactory msgServiceFactory;

	private Map<String, FrontendSession> sessions;
	
	private ICServicesManager icsManager = new ICServicesManager();
	
	/* this is hardwired because there will always be only one sync. EVVAAR!! */
	private IFriendlySyncService sync = new SyncServiceImpl(this);
	
	/**
	 * Constructor
	 * 
	 * @param projectsManagingService
	 * @param msgServiceFactory
	 */
	public FrontendServiceImpl(IProjectsManagingService projectsManagingService,
			MsgServiceFactory msgServiceFactory) {
		this.setProjectsManagingService(projectsManagingService);
		this.setSessions(new HashMap<String, FrontendSession>());
		this.msgServiceFactory = msgServiceFactory;
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
	 * @param credentials
	 *            The credentials to be checked
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
	 * /// TODO Do we really need this!? -- Dominik <p/> retrieves a session Yes
	 * we do!
	 * 
	 * @param sessionId
	 *            The id associated with the session after it was created
	 * @return // TODO
	 * @throws IllegalArgumentException
	 *             if <code>sessionId</code> was null
	 * @throws NotLoggedInException
	 *             if no Session associated with <code>sessionId</code> exists.
	 */
	private FrontendSession getSession(String sessionId) throws IllegalArgumentException,
			NotLoggedInException {
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
			NotLoggedInException {
		boolean successfullyRemoved;

		if (sessionId == null)
			throw new IllegalArgumentException();

		successfullyRemoved = this.removeSession(sessionId);
		if (!successfullyRemoved)
			throw new NotLoggedInException();

		return true;
	}

	@Override
	public IProjectsManagingService getProjectsManagingService(String sessionId)
			throws IllegalArgumentException, NotLoggedInException, IllegalStateException {
		checkSession(sessionId);
		// 3. return ProjectsManagingService
		return this.getProjectsManagingService();
	}

	private void checkSession(String sessionId) throws NotLoggedInException {


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
			throw new NotLoggedInException("Invalid Session; Not logged in");
		}

	}

	@Override
	public List<MsgService> getMsgServices(String sessionId)
			throws IllegalArgumentException, NotLoggedInException, IllegalStateException {
		this.checkSession(sessionId);
		return this.getMsgServices();
	}
	
	private List<MsgService> getMsgServices() throws  IllegalStateException {
        return this.msgServiceFactory.getAll();
    }

	@Override
	public boolean createAccount(String sessionId, ServiceCredentials credentials)
			throws NotLoggedInException, InvalidCredentialsException,
			ProtocolNotSupportedException, Exception {
		checkSession(sessionId);
		return msgServiceFactory.createAccount(credentials);
	}

	@Override
	public MsgService addAccount(String sessionId, ServiceCredentials credentials)
			throws NotLoggedInException, InvalidCredentialsException,
			ProtocolNotSupportedException {
		checkSession(sessionId);
		return msgServiceFactory.addMsgService(credentials);
	}
	
	@Override
	public void signOut(String sessionId) throws NotLoggedInException {
		FrontendSession session;
		Iterable<MsgService> msgServices;
		
		this.checkSession(sessionId);
		this.getSessions().remove(sessionId);
		
		/* do not logout - other UIs may access the core */
	}

	@Override
	public void ping(String sessionId) throws IllegalArgumentException,
			NotLoggedInException {
		checkSession(sessionId);

		// TODO
	}

	@Override
	public IFriendlySyncService getSyncService(String sessionId)
			throws NotLoggedInException {
		this.checkSession(sessionId);
		return this.sync;
	}
	
	/* InternalFrontendService implementation*/
	
	private void setIcsManager(ICServicesManager icsManager) {
		this.icsManager = icsManager;
	}

	private ICServicesManager getIcsManager() {
		return icsManager;
	}

	@Override
	public ICService getICSForProject(Project p) {
		ICService result = null;
		try {
			result = this.getIcsManager().getICService(p);
		} catch (ProtocolNotSupportedException e) {
			log.error("Retrieving an ICService for a Project failed: ",e);
		}
		return result;
	}

	@Override
	public IFriendlySyncService getSync() {
		return this.sync;
	}

	@Override
	public Collection<ServiceCredentials> getLastLogins() {
		return this.getServiceCredentialsDao().getAll();
	}
}
