package com.jakeapp.core.services;

import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NotLoggedInException;

import java.util.Map;
import java.util.List;

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
    public IProjectService getProjectService(String sessionId) throws IllegalArgumentException, NotLoggedInException, IllegalStateException {
        return null; // TODO
    }

    @Override
    public List<MsgService> getMsgServices(String sessionId) throws IllegalArgumentException, NotLoggedInException, IllegalStateException {
        return null; // TODO
    }

    @Override
    public void ping(String sessionId) throws IllegalArgumentException, NotLoggedInException {
        // TODO
    }
}
