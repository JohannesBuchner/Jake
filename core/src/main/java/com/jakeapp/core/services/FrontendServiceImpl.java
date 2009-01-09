package com.jakeapp.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NotLoggedInException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.synchronization.SyncServiceImpl;

/**
 * Implementation of the FrontendServiceInterface
 */
public class FrontendServiceImpl implements IFrontendService {

	private static Logger log = Logger.getLogger(FrontendServiceImpl.class);

	private IProjectsManagingService projectsManagingService;

	private List<MsgService> msgServices = new ArrayList<MsgService>();

	private MsgServiceFactory msgServiceFactory;

	private Map<String, FrontendSession> sessions;

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
		checkSession(sessionId);
		return this.getSession(sessionId).getMsgServices();
		// or
		// return this.msgServiceFactory.getAll();
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
	public void ping(String sessionId) throws IllegalArgumentException,
			NotLoggedInException {
		checkSession(sessionId);

		// TODO
	}

	@Override
	public IFriendlySyncService getSyncService(String sessionId)
			throws NotLoggedInException {
		return getSession(sessionId).getSync();
	}
}
