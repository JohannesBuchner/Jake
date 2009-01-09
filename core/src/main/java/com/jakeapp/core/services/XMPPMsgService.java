package com.jakeapp.core.services;

import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.JakeMessage;
import com.jakeapp.core.domain.XMPPUserId;
import com.jakeapp.core.domain.exceptions.UserIdFormatException;

import java.util.List;

/**
 * Implementation of the MessageService for the XMPP Messaging Protocol
 */
public class XMPPMsgService extends MsgService<XMPPUserId> {

    @Override
    protected boolean doCredentialsCheck() {
        return false; // TODO
    }

    @Override
    protected boolean doLogin() {
        return false; // TODO
    }

    @Override
    protected void doLogout() {
        // TODO
    }

    @Override
    public void sendMessage(JakeMessage message) {
        // TODO
    }

    @Override
    protected XMPPUserId doRegister() {
        return null; // TODO
    }

    @Override
    public List<XMPPUserId> getUserList() {
        return null; // TODO
    }

    @Override
    public XMPPUserId getUserId(String userId) throws UserIdFormatException {
        return null; // TODO
    }

    @Override
    protected boolean checkFriends(XMPPUserId friend) {
        return false; // TODO
    }

    @Override
    public List<XMPPUserId> findUser(String pattern) {
        return null; // TODO
    }

    @Override
    public String getServiceName() {
        return null; // TODO
    }

    @Override
    public void createAccount() {
        // TODO
    }
}
