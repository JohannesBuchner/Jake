package com.jakeapp.core.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.Injected;
import com.jakeapp.core.dao.IAccountDao;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.dao.exceptions.NoSuchServiceCredentialsException;
import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NoSuchMsgServiceException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.services.futures.CreateAccountFuture;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.exceptions.NotADirectoryException;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.status.ILoginStateListener;

/**
 * Implementation of the <code>IFrontendService</code> Interface
 */
public class FrontendServiceImpl implements IFrontendService {

	private static Logger log = Logger.getLogger(FrontendServiceImpl.class);

	@Injected
	private IProjectsManagingService projectsManagingService;

	@Injected
	private MsgServiceManager msgServiceFactory;

	private Map<String, FrontendSession> sessions;

	@Injected
	private IAccountDao accountDao;

	@Injected
	private IFriendlySyncService sync;

	/**
	 * Constructor
	 *
	 * @param projectsManagingService
	 * @param msgServiceFactory
	 * @param sync
	 * @param accountDao
	 */
	@Injected
	public FrontendServiceImpl(IProjectsManagingService projectsManagingService,
							   MsgServiceManager msgServiceFactory, IFriendlySyncService sync,
							   IAccountDao accountDao
	) {
		this.setProjectsManagingService(projectsManagingService);
		this.msgServiceFactory = msgServiceFactory;
		this.sync = sync;
		this.accountDao = accountDao;
		this.setSessions(new HashMap<String, FrontendSession>());
	}

	private IProjectsManagingService getProjectsManagingService() {
		return projectsManagingService;
	}

	private IAccountDao getServiceCredentialsDao() {
		return this.accountDao;
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
	 */
	private void checkCredentials(Map<String, String> credentials)
			throws IllegalArgumentException, InvalidCredentialsException {

		if (credentials == null)
			throw new IllegalArgumentException();

		if (!credentials.isEmpty())
			throw new InvalidCredentialsException(
					"You are doing it wrong (don't set any credentials for core login)");
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
	 * {@inheritDoc}
	 */
	@Override
	public String authenticate(Map<String, String> credentials, ChangeListener changeListener)
			throws IllegalArgumentException, InvalidCredentialsException {
		String sessid;

		this.checkCredentials(credentials);

		// create new session
		sessid = makeSessionID();
		this.addSession(sessid, new FrontendSession());

		// move data to projectmanaging service
		if (changeListener != null) {
			this.getProjectsManagingService().addChangeListener(changeListener);
		}

		return sessid;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IProjectsManagingService getProjectsManagingService(String sessionId)
			throws IllegalArgumentException, FrontendNotLoggedInException, IllegalStateException {
		checkSession(sessionId);
		// 3. return ProjectsManagingService
		return this.getProjectsManagingService();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MsgService<User>> getMsgServices(String sessionId)
			throws IllegalArgumentException, FrontendNotLoggedInException, IllegalStateException {
		this.checkSession(sessionId);
		return this.getMsgServices();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AvailableLaterObject<Void> createAccount(String sessionId, Account credentials)
			throws FrontendNotLoggedInException, InvalidCredentialsException,
			ProtocolNotSupportedException, NetworkException {
		checkSession(sessionId);
		MsgService svc = msgServiceFactory.getOrCreate(credentials);

		return new CreateAccountFuture(svc).start();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MsgService addAccount(String sessionId, Account credentials)
			throws FrontendNotLoggedInException, InvalidCredentialsException,
			ProtocolNotSupportedException {
		checkSession(sessionId);

		return msgServiceFactory.getOrCreate(credentials);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void removeAccount(String sessionId, MsgService msg)
			throws FrontendNotLoggedInException, NoSuchMsgServiceException {
		List<Project> projectsOfUser;
		Account toDelete;
		checkSession(sessionId);

		if (msg == null) throw new NoSuchMsgServiceException();
		if (msg.getServiceCredentials() == null) throw new NoSuchMsgServiceException();

		try {
			toDelete = msg.getServiceCredentials();
			//delete all projects that belong to that Account
			//TODO implement this on a lower level (e.g. ON DELETE CASCADE in DB)
			projectsOfUser = this.getProjectsManagingService().getProjectList(msg);
			for (Project p : projectsOfUser)
				this.getProjectsManagingService().deleteProject(p, false);

			this.getServiceCredentialsDao().delete(toDelete);

			//remove the MsgService from the cache.
			this.msgServiceFactory.remove(toDelete);
		} catch (NoSuchServiceCredentialsException e) {
			throw new NoSuchMsgServiceException(e);
		} catch (IllegalArgumentException e) {
			throw new NoSuchMsgServiceException(e);
		} catch (SecurityException e) {
			//empty handling
			log.warn(e);
		} catch (IOException e) {
			//empty handling
			log.warn(e);
		} catch (NotADirectoryException e) {
			//empty handling
			log.warn(e);
		} catch (NoSuchProjectException e) {
			//empty handling
			log.warn(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void signOut(String sessionId) throws FrontendNotLoggedInException {
		this.checkSession(sessionId);
		this.getSessions().remove(sessionId);

		/* do not logout - other UIs may access the core */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void ping(String sessionId) throws IllegalArgumentException,
			FrontendNotLoggedInException {
		checkSession(sessionId);

		// TODO do session refresh
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFriendlySyncService getSyncService(String sessionId)
			throws FrontendNotLoggedInException {
		this.checkSession(sessionId);
		return this.sync;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public AvailableLaterObject<Boolean> login(final String session, final MsgService service, final String password,
											   final boolean rememberPassword, final ILoginStateListener loginListener) {
		Account credentials = new Account();
		credentials.setPlainTextPassword(password);
		credentials.setAutologin(rememberPassword);
		return login(session, service, credentials, loginListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public AvailableLaterObject<Boolean> login(final String session, final MsgService service,
											   final Account credentials, final ILoginStateListener loginListener) {
		AvailableLaterObject<Boolean> ret = new AvailableLaterObject<Boolean>() {

			@Override
			public Boolean calculate() throws NetworkException {
				boolean result;
				checkSession(session);

				// register login event prior logging in (we don't wanna miss the party!)
				service.getMainIcs().getMsgService().registerLoginStateListener(loginListener);

				/* login */
				try {
					result = service.login(credentials);

				} catch (NetworkException ex) {
					try {
						loginListener.connectionStateChanged(ILoginStateListener.ConnectionState.LOGGED_OUT, ex);
					} catch (Exception ignored) {
					}

					throw ex;
				}
//				projectInvitationHandler = new ProjectInvitationHandler(service);
//				projectInvitationHandler
//						.registerInvitationListener(FrontendServiceImpl.this.coreInvitationListener);


//				service.getMainIcs().getMsgService().registerReceiveMessageListener(projectInvitationHandler);

				return result;
			}
		};

		return ret;
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

	private List<MsgService<User>> getMsgServices() throws IllegalStateException {
		return this.msgServiceFactory.getAll();
	}

	//	@Override
	public IFriendlySyncService getSync() {
		return this.sync;
	}

	/**
	 * @param sessionId The id associated with the session after it was created
	 * @return
	 * @throws IllegalArgumentException if <code>sessionId</code> was null
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *                                  if no Session associated with <code>sessionId</code> exists.
	 */
	@SuppressWarnings("unused")
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

}
