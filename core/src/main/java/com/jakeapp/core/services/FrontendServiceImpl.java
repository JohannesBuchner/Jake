package com.jakeapp.core.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.ICService;

/**
 * Created by IntelliJ IDEA.
 * User: domdorn
 * Date: Dec 31, 2008
 * Time: 12:21:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class FrontendServiceImpl implements IFrontendService {
	
	private Map<String,FrontendSession> sessions;
	
	/**
	 * Checks frontend-credentials and throws exceptions if they
	 * are not correct.
	 * @see #authenticate(Map)
	 * @param credentials The credentials to be checked
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
	 * retrieves a session
	 * @param sessionId The id associated with the session after it was created
	 * @throws IllegalArgumentException if <code>sessionId</code> was null
	 * @throws NotLoggedInException if no Session associated with <code>sessionId</code> exists.
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
	
	public FrontendServiceImpl() {
		this.setSessions(new HashMap<String,FrontendSession>());
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
    public IMetaProjectService getMetaProjectService(String sessionId) throws IllegalArgumentException, NotLoggedInException, IllegalStateException {
        return this.getSession(sessionId).getMetaProjectService();
    }

    @Override
    public List<ICService> getICServices(String sessionId) throws IllegalArgumentException, NotLoggedInException, IllegalStateException {
    	return this.getSession(sessionId).getICServices();
    }

    @Override
    public void ping(String sessionId) throws IllegalArgumentException, NotLoggedInException {
        // TODO
    }
}
