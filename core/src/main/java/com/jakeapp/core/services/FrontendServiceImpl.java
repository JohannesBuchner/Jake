package com.jakeapp.core.services;

import java.util.*;

import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NotLoggedInException;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.dao.IServiceCredentialsDao;
import com.jakeapp.jake.ics.ICService;

/**
 * Implementation of the FrontendServiceInterface
 */
public class FrontendServiceImpl implements IFrontendService {
    private IProjectsManagingService projectsManagingService;
    private List<MsgService> msgServices = new ArrayList<MsgService>();
    private MsgServiceFactory msgServiceFactory;

    /**
     * Constructor
     * @param projectsManagingService
     * @param msgServiceFactory

     */
    public FrontendServiceImpl(
            IProjectsManagingService projectsManagingService,
            MsgServiceFactory msgServiceFactory
    ) {
        this.setProjectsManagingService(projectsManagingService);
        this.setSessions(new HashMap<String,FrontendSession>());
        this.msgServiceFactory = msgServiceFactory;



    }















    private IProjectsManagingService getProjectsManagingService() {
        return projectsManagingService;
    }

    private void setProjectsManagingService(IProjectsManagingService projectsManagingService) {
        this.projectsManagingService = projectsManagingService;
    }

    private Map<String,FrontendSession> sessions;
	
	/**
	 * Checks frontend-credentials and throws exceptions if they
	 * are not correct.
	 * @see #authenticate(Map)
	 * @param credentials The credentials to be checked
     * @throws com.jakeapp.core.domain.exceptions.InvalidCredentialsException
     * @throws IllegalArgumentException
	 */
	private void checkCredentials(Map<String, String> credentials)
		throws IllegalArgumentException, InvalidCredentialsException { 
		
		if (credentials == null) throw new IllegalArgumentException();
		
		//TODO do further checking for later versions
	}
	
	private void setSessions(Map<String,FrontendSession> sessions) {
		this.sessions = sessions;
	}

	private Map<String,FrontendSession> getSessions() {
		return sessions;
	}
	
	private void addSession(String sessid,FrontendSession session) {
		this.getSessions().put(sessid,session);
	}
	
	private boolean removeSession(String sessid) {
		FrontendSession fes;
		
		fes = this.getSessions().remove(sessid);
		
		return fes != null;
	}
	
	/**
     * /// TODO Do we really need this!? -- Dominik
     *
	 * retrieves a session
	 * @param sessionId The id associated with the session after it was created
	 * @throws IllegalArgumentException if <code>sessionId</code> was null
	 * @throws NotLoggedInException if no Session associated with <code>sessionId</code> exists.
     * @return // TODO
	 */
	private FrontendSession getSession(String sessionId) throws IllegalArgumentException, NotLoggedInException {
		FrontendSession result;
		
		if (sessionId == null) throw new IllegalArgumentException();
		result = this.getSessions().get(sessionId);
		
		if (result == null) throw new NotLoggedInException();
		
		return result;
	}
	
    /**
     * @return A sessionid for a new session
     */
	private String makeSessionID() {
		return UUID.randomUUID().toString();
	}
	

    @Override
    public String authenticate(Map<String, String> credentials) throws IllegalArgumentException, InvalidCredentialsException {
    	String sessid;
    	
    	this.checkCredentials(credentials);
    	
    	//create new session
    	sessid = makeSessionID();
    	this.addSession(sessid, new FrontendSession());
    	
        return sessid;
    }

    @Override
    public boolean logout(String sessionId) throws IllegalArgumentException, NotLoggedInException {
    	boolean successfullyRemoved;
    	
    	if (sessionId == null) throw new IllegalArgumentException();
    	
    	successfullyRemoved = this.removeSession(sessionId);
    	if (!successfullyRemoved) throw new NotLoggedInException();
    	
        return true;
    }

    @Override
    public IProjectsManagingService getProjectsManagingService(String sessionId) throws IllegalArgumentException, NotLoggedInException, IllegalStateException {
        sessionCheck(sessionId);
        // 3. return ProjectsManagingService
            return this.getProjectsManagingService();
    }

    private void sessionCheck(String sessionId) throws NotLoggedInException {
        // 1. check if session is null, if so throw  IllegalArgumentException
        if(sessionId == null || sessionId.isEmpty())
            throw new IllegalArgumentException("invalid sessionid");

        // 2. check session validity
        if(!sessions.containsKey(sessionId))
                throw new NotLoggedInException("Invalid Session; Not logged in");
    }

    @Override
    public List<ICService> getICServices(String sessionId) throws IllegalArgumentException, NotLoggedInException, IllegalStateException {
    	return this.getSession(sessionId).getICServices();
    }

    @Override
    public List<MsgService> getMsgServices(String sessionId) throws IllegalArgumentException, NotLoggedInException, IllegalStateException {
        sessionCheck(sessionId);
        return this.msgServiceFactory.getAll();
    }

    @Override
    public boolean registerAccount(String sessionId, ServiceCredentials credentials) throws NotLoggedInException, InvalidCredentialsException {
        return false; // TODO
    }

    @Override
    public MsgService addAccount(String sessionId, ServiceCredentials credentials) throws NotLoggedInException, InvalidCredentialsException {
        sessionCheck(sessionId);
        return msgServiceFactory.addAccount(credentials);
    }

    @Override
    public void ping(String sessionId) throws IllegalArgumentException, NotLoggedInException {
        sessionCheck(sessionId);

        // TODO
    }
}
