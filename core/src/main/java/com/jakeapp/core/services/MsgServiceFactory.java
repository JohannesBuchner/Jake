package com.jakeapp.core.services;

import com.jakeapp.core.dao.IServiceCredentialsDao;
import com.jakeapp.core.dao.IUserIdDao;
import com.jakeapp.core.dao.exceptions.NoSuchUserIdException;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.XMPPUserId;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.InvalidUserIdException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.services.futures.CreateAccountFuture;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Factory Class to create MsgServices by giving ServiceCredentials
 */
@Transactional
public class MsgServiceFactory {

    private static Logger log = Logger.getLogger(MsgServiceFactory.class);

    private IServiceCredentialsDao serviceCredentialsDao;
    private IUserIdDao userIdDao;


    private List<MsgService> msgServices = new ArrayList<MsgService>();

    private boolean initialised = false;

    private void ensureInitialised() {
        log.debug("calling ensureInitialised");
        if (!initialised) {
            log.debug("was not initialized");
            initialised = true;
            createTestdata();

        }
    }

    public MsgServiceFactory() {
        log.debug("calling empty Constructor");
    }

    public MsgServiceFactory(IServiceCredentialsDao serviceCredentialsDao, IUserIdDao userIdDao) {
        log.debug("calling constructor with serviceCredentialsDao");
        this.serviceCredentialsDao = serviceCredentialsDao;
        this.userIdDao = userIdDao;
        // can not initialise here, this produces spring/hibernate errors!
    }

    private IServiceCredentialsDao getServiceCredentialsDao() {
        return serviceCredentialsDao;
    }


    @Transactional
    private void createTestdata() {
        log.debug("creating testData");
        List<ServiceCredentials> credentialsList = new ArrayList<ServiceCredentials>();

        credentialsList = this.serviceCredentialsDao.getAll();

        ServiceCredentials sc1 = new ServiceCredentials("domdorn@jabber.fsinf.at",
                "somepass");
        sc1.setUuid("02918516-062d-4028-9d7a-ed0393d0a90d");
        sc1.setProtocol(ProtocolType.XMPP);
        try {
            sc1.setServerAddress(Inet4Address.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        sc1.setServerPort(9000);
        sc1.setEncryptionUsed(false);


        ServiceCredentials sc2 = new ServiceCredentials("pstein@jabber.fsinf.at",
                "somepass");
        sc2.setUuid("48cce803-c878-46d3-b1e6-6165f75dcf88");
        sc2.setProtocol(ProtocolType.XMPP);
        try {
            sc2.setServerAddress(Inet4Address.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        sc2.setServerPort(9000);
        sc2.setEncryptionUsed(false);


        ServiceCredentials sc3 = new ServiceCredentials("pstein@jabber.fsinf.at",
                "somepass");
        sc3.setUuid("db9ac8a3-581f-42cc-ad81-2900eb74c390");
        sc3.setProtocol(ProtocolType.XMPP);
        try {
            sc3.setServerAddress(Inet4Address.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        sc3.setServerPort(9000);
        sc3.setEncryptionUsed(false);

        credentialsList.add(sc1);
        credentialsList.add(sc2);
        credentialsList.add(sc3);

        for (ServiceCredentials credentials : credentialsList) {
            MsgService service = null;
            try {
                service = this.createMsgService(credentials);
            } catch (ProtocolNotSupportedException e) {
            }
            msgServices.add(service);
        }
    }

    public MsgService createMsgService(ServiceCredentials credentials)
            throws ProtocolNotSupportedException {

        log.debug("calling createMsgService ");

        ensureInitialised();
        MsgService result = null;
        if (credentials.getProtocol().equals(ProtocolType.XMPP)) {
            log.debug("Creating new XMPPMsgService for userId "
                    + credentials.getUserId());
            result = new XMPPMsgService();
            result.setCredentials(credentials);


            try {
                if(!credentials.getUuid().isEmpty())
                {
                    log.debug("uuid is not empty");

                    if(userIdDao == null)
                        log.debug("userIdDao = null");

                    UserId userId = userIdDao.get(
                            UUID.fromString(
                                    credentials.getUuid()
                            )
                    );
                    
                    log.debug("setting userid");
                    result.setUserId(userId);

                }
                else
                {
                    log.debug("uuid is empty");



                }

            } catch (InvalidUserIdException e) {
                e.printStackTrace();
            } catch (NoSuchUserIdException e) {
                e.printStackTrace();
            }

        } else {
            log.warn("Currently unsupported protocol given");
            throw new ProtocolNotSupportedException();
        }

        return result;
    }


    @Transactional
    public List<MsgService> getAll() {
        log.debug("calling getAll");
        ensureInitialised();
        return msgServices;
    }

    /**
     * create a account with the given credentials. You are not logged in
     * afterwards
     *
     * @param credentials
     * @param listener
     * @return success state
     * @throws ProtocolNotSupportedException
     * @throws Exception
     */
    public AvailableLaterObject<Void> createAccount(ServiceCredentials credentials, AvailabilityListener listener)
            throws ProtocolNotSupportedException, NetworkException {
        log.debug("calling AvailableLaterObject");
        MsgService svc = createMsgService(credentials);

        return new CreateAccountFuture(svc, listener);
    }


    /**
     * creates and adds a msgservice for the right protocol
     *
     * @param credentials
     * @return the service
     * @throws InvalidCredentialsException
     * @throws ProtocolNotSupportedException
     */
    public MsgService addMsgService(ServiceCredentials credentials)
            throws InvalidCredentialsException, ProtocolNotSupportedException {
        log.debug("calling addMsgService");

        MsgService svc = this.createMsgService(credentials);
        msgServices.add(svc);

        //add account in database
        this.getServiceCredentialsDao().create(credentials);

		return svc;
	}
}
