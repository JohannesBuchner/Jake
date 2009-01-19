package com.jakeapp.core.services;

import com.jakeapp.core.domain.JakeMessage;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.XMPPUserId;
import com.jakeapp.core.domain.exceptions.UserIdFormatException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.InvalidUserIdException;
import com.jakeapp.core.dao.IUserIdDao;
import com.jakeapp.core.dao.exceptions.NoSuchUserIdException;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.UserId;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * Implementation of the MessageService for the XMPP Messaging Protocol
 */
public class XMPPMsgService extends MsgService<XMPPUserId> {

    private static Logger log = Logger.getLogger(XMPPMsgService.class);

    public static final String namespace = "http://jakeapp.com/protocols/xmpp/versions/1";

    private XmppICService ics = new XmppICService(namespace, "Jake");

    private XmppUserId icsXmppUserId;

    private String host;


    public XMPPMsgService() {


//
//        this.setUserId(new XMPPUserId(this.getServiceCredentials(), UUID.randomUUID(),
//                "todo useridstring", "todo nickname", "todo firstname", "todo surname"));

    }

    @Override
    protected boolean doCredentialsCheck() {
        ServiceCredentials cred = this.getServiceCredentials();
        this.icsXmppUserId = new XmppUserId(new XmppUserId(cred.getUserId())
                // TODO 4 Johannes: This doesn't work!
//				.getUserIdWithOutResource()
//				+"/Jake"
        );
        if (!this.icsXmppUserId.isOfCorrectUseridFormat()) {
            this.icsXmppUserId = null;
            return false;
        }
        this.host = cred.getServerAddress();
        if (this.host == "" || this.host == null) {
            this.host = this.icsXmppUserId.getHost();
        }
        return true;
    }

    @Override
    protected boolean doLogin() throws NetworkException {
        boolean success = this.ics.getStatusService().login(this.icsXmppUserId,
                this.getServiceCredentials().getPlainTextPassword());


        if (success) {
            

//            log.debug("login success");
//            try {
//                com.jakeapp.core.domain.UserId result = getUserIdDao().get(this.getUserId());
//                this.setUserId(XMPPUserId.createFromUserId(result));
//            } catch (NoSuchUserIdException e) {
//                e.printStackTrace();
//            } catch (InvalidUserIdException e) {
//                XMPPUserId xmppResult = new XMPPUserId(
//                        this.getServiceCredentials(),
//                        UUID.fromString(this.getServiceCredentials().getUuid()),
//                        this.getServiceCredentials().getUserId(),
//                        this.getServiceCredentials().getUserId(), "", "");
//
//                try {
//                    getUserIdDao().create(xmppResult);
//                    this.setUserId(xmppResult);
//                } catch (InvalidUserIdException e1) {
//                    e1.printStackTrace();
//                }
//            }
        }

        return success;
    }

    @Override
    protected void doLogout() throws Exception {
        log.debug("XMPPMsgService -> logout");

        this.ics.getStatusService().logout();
    }

    @Override
    public void sendMessage(JakeMessage message) {
        // TODO
    }

    @Override
    public List<XMPPUserId> getUserList() {
        List<com.jakeapp.core.domain.XMPPUserId> result = new ArrayList<com.jakeapp.core.domain.XMPPUserId>();

//        List<com.jakeapp.core.domain.UserId> dbInput;
//        List<String> userIds = new LinkedList<String>();
//
//
//        try {
//            dbInput = getUserIdDao().getAll(this.getServiceCredentials());
//
//            for (com.jakeapp.core.domain.UserId user : result) {
//                userIds.add(user.getUserId());
//            }
//
//            for (UserId user : this.ics.getUsersService().getUsers()) {
//
//                if (!userIds.contains(user.getUserId()))
//                    try {
//                        result.add(
//                                XMPPUserId.createFromUserId(
//                                        getUserIdDao().create(
//
//                                                new XMPPUserId(
//                                                        this.getServiceCredentials(),
//                                                        UUID.randomUUID(),
//                                                        user.getUserId(),
//                                                        user.getUserId(), "", ""
//                                                )
//                                        )
//                                )
//                        );
//                        userIds.add(user.getUserId());
//                    } catch (InvalidUserIdException e) {
//                        e.printStackTrace();
//                    }
//            }
//
//
//        } catch (InvalidCredentialsException e) {
//            e.printStackTrace();
//        } catch (NotLoggedInException e) {
//            e.printStackTrace();
//        }

        return result;
    }

    @Override
    public XMPPUserId getUserId(String userId) throws UserIdFormatException {
        log.debug("calling getUserId");

        XMPPUserId result = new XMPPUserId(this.getServiceCredentials(), UUID
                .randomUUID(), "TODO test", "todo nickname", "todo firstname",
                "todo surname");
        return result;

//
//        if (super.getUserId() == null) {
//            log.debug("current userid is null");
//
//            try {
//                setUserId(
//                        XMPPUserId.createFromUserId(getUserIdDao().get(UUID.fromString(this.getServiceCredentials().getUuid())))
//
//                );
//                return this.userId;
//            } catch (InvalidUserIdException e) {
//                log.debug("InvalidUserIdException couldn't get UserId");
//                e.printStackTrace();
//            } catch (NoSuchUserIdException e) {
//                log.debug("NoSuchUserIdException couldn't get UserId");
//                e.printStackTrace();
//            }
//
//            if (super.getUserId() == null) {
//                log.debug("userid is still null");
//                XMPPUserId result = new XMPPUserId(this.getServiceCredentials(), UUID
//                        .randomUUID(), "TODO test", "todo nickname", "todo firstname",
//                        "todo surname");
//                return result;
//            }
//            else
//            {
//                log.debug("userid is not null");
//                return this.userId;
//            }
//
//        }
//
//        return this.userId;

    }

    @Override
    public XMPPUserId getUserId() {
//        this.setUserId(new XMPPUserId(this.getServiceCredentials(), UUID.randomUUID(),
//                "todo useridstring", "todo nickname", "todo firstname", "todo surname"));


        if (this.userId == null) {
            log.debug("current userid is null");

            try {
                setUserId(
                        XMPPUserId.createFromUserId(getUserIdDao().get(UUID.fromString(this.getServiceCredentials().getUuid())))

                );
                return this.userId;
            } catch (InvalidUserIdException e) {
                log.debug("InvalidUserIdException couldn't get UserId");
                e.printStackTrace();
            } catch (NoSuchUserIdException e) {
                log.debug("NoSuchUserIdException couldn't get UserId");
                e.printStackTrace();
            }

            if (this.userId == null) {
                log.debug("userid is still null");
                XMPPUserId result = new XMPPUserId(this.getServiceCredentials(), UUID
                        .randomUUID(), "TODO test", "todo nickname", "todo firstname",
                        "todo surname");
                return result;
            } else {
                log.debug("userid is not null");
                return this.userId;
            }

        }

        return this.userId;


//        return super.getUserId();
    }

    @Override
    protected boolean checkFriends(XMPPUserId friend) {
        return false; // TODO
    }

    @Override
    public List<XMPPUserId> findUser(String pattern) {
        List<XMPPUserId> result = new ArrayList<XMPPUserId>();
        // TODO
        return result;
    }

    @Override
    public String getServiceName() {
        return "XMPP";
    }

    @Override
    public void createAccount() throws NetworkException {
        ics.getStatusService().createAccount(icsXmppUserId,
                this.getServiceCredentials().getPlainTextPassword());
    }
}
