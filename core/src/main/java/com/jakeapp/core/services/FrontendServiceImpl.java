package com.jakeapp.core.services;

import java.util.List;
import java.util.Map;

import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.msgservice.IMsgService;

/**
 * Created by IntelliJ IDEA.
 * User: domdorn
 * Date: Dec 31, 2008
 * Time: 12:21:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class FrontendServiceImpl implements IFrontendService {
    @Override
    public String authenticate(Map<String, String> credentials) throws IllegalArgumentException, InvalidCredentialsException {
        return null; // TODO
    }

    @Override
    public boolean logout(String sessionId) throws IllegalArgumentException, NotLoggedInException {
        return false; // TODO
    }

    @Override
    public IMetaProjectService getMetaProjectService(String sessionId) throws IllegalArgumentException, NotLoggedInException, IllegalStateException {
        return null; // TODO
    }

    @Override
    public List<IMsgService> getMsgServices(String sessionId) throws IllegalArgumentException, NotLoggedInException, IllegalStateException {
        return null; // TODO
    }

    @Override
    public void ping(String sessionId) throws IllegalArgumentException, NotLoggedInException {
        // TODO
    }
}
