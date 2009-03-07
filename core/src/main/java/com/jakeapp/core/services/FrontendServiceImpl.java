package com.jakeapp.core.services;

import com.jakeapp.core.Injected;
import com.jakeapp.core.dao.IServiceCredentialsDao;
import com.jakeapp.core.dao.exceptions.NoSuchServiceCredentialsException;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NoSuchMsgServiceException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.services.futures.CreateAccountFuture;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.status.ILoginStateListener;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the FrontendServiceInterface
 */
public class FrontendServiceImpl implements IFrontendService {

	private static Logger log = Logger.getLogger(FrontendServiceImpl.class);

	@Injected
	private IProjectsManagingService projectsManagingService;

	@Injected
	private MsgServiceManager msgServiceFactory;

	private Map<String, FrontendSession> sessions;
	
	@Injected
	private IServiceCredentialsDao serviceCredentialsDao;

	@Injected
	private IFriendlySyncService sync;


	/*= new ProjectInvitationHandler(service);
				projectInvitationHandler
						.registerInvitationListener(FrontendServiceImpl.this.coreInvitationListener);
	*/

	/**
	 * Constructor
	 *
	 * @param projectsManagingService
	 * @param msgServiceFactory
	 * @param sync
	 * @param serviceCredentialsDao
	 */
	@Injected
	public FrontendServiceImpl(IProjectsManagingService projectsManagingService,
										MsgServiceManager msgServiceFactory, IFriendlySyncService sync,
										IServiceCredentialsDao serviceCredentialsDao
		) {
		this.setProjectsManagingService(projectsManagingService);
		this.msgServiceFactory = msgServiceFactory;
		this.sync = sync;
		this.serviceCredentialsDao = serviceCredentialsDao;
		this.setSessions(new HashMap<String, FrontendSession>());
	}

	private IProjectsManagingService getProjectsManagingService() {
		return projectsManagingService;
	}

	private IServiceCredentialsDao getServiceCredentialsDao() {
		return this.serviceCredentialsDao;
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
	 * @param sessionId The id associated with the session after it was created
	 * @throws IllegalArgumentException if <code>sessionId</code> was null
	 * @throws com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException
	 *                                  if no Session associated with <code>sessionId</code> exists.
	 * @return
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


	@Override
	public String authenticate(Map<String, String> credentials, ChangeListener changeListener)
			  throws IllegalArgumentException, InvalidCredentialsException {
		String sessid;

		this.checkCredentials(credentials);

		// create new session
		sessid = makeSessionID();
		this.addSession(sessid, new FrontendSession());

		// move data to projectmanaging service
		if(changeListener != null) {
			this.getProjectsManagingService().setChangeListener(changeListener);
		}

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
	public List<MsgService<User>> getMsgServices(String sessionId)
			  throws IllegalArgumentException, FrontendNotLoggedInException, IllegalStateException {
		this.checkSession(sessionId);
		return this.getMsgServices();
	}

	private List<MsgService<User>> getMsgServices() throws IllegalStateException {
		return this.msgServiceFactory.getAll();
	}

	@SuppressWarnings("unchecked")
	@Override
	public AvailableLaterObject<Void> createAccount(String sessionId, ServiceCredentials credentials)
			  throws FrontendNotLoggedInException, InvalidCredentialsException,
			  ProtocolNotSupportedException, NetworkException {
		checkSession(sessionId);
		MsgService svc = msgServiceFactory.getOrCreate(credentials);

		return new CreateAccountFuture(svc);
	}

	@SuppressWarnings("unchecked")
	@Override
	public MsgService addAccount(String sessionId, ServiceCredentials credentials)
			  throws FrontendNotLoggedInException, InvalidCredentialsException,
			  ProtocolNotSupportedException {
		checkSession(sessionId);

		return msgServiceFactory.getOrCreate(credentials);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removeAccount(String sessionId, MsgService msg)
					throws FrontendNotLoggedInException, NoSuchMsgServiceException {
		checkSession(sessionId);
		
		if (msg==null) throw new NoSuchMsgServiceException();
		if (msg.getServiceCredentials()==null) throw new NoSuchMsgServiceException();
		
		try {
			this.getServiceCredentialsDao().delete(msg.getServiceCredentials());
		} catch (NoSuchServiceCredentialsException e) {
			throw new NoSuchMsgServiceException(e);
		}
	}

	@Override
	public void signOut(String sessionId) throws FrontendNotLoggedInException {
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
	public IFriendlySyncService getSyncService(String sessionId)
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

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public AvailableLaterObject<Boolean> login(final String session, final MsgService service, final String password,
			final boolean rememberPassword, final ILoginStateListener loginListener) {
		ServiceCredentials credentials = new ServiceCredentials();
		credentials.setPlainTextPassword(password);
		credentials.setAutologin(rememberPassword);
		return login(session, service, credentials, loginListener);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public AvailableLaterObject<Boolean> login(final String session, final MsgService service,
					final ServiceCredentials credentials, final ILoginStateListener loginListener) {
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

				}catch(NetworkException ex) {
					loginListener.connectionStateChanged(ILoginStateListener.ConnectionState.LOGGED_OUT, ex);
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
}
