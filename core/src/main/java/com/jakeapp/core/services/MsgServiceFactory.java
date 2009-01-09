package com.jakeapp.core.services;

import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.dao.IServiceCredentialsDao;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * Factory Class to create MsgServices by giving ServiceCredentials
 */
public class MsgServiceFactory {
    private static Logger log = Logger.getLogger(MsgServiceFactory.class);

    private IServiceCredentialsDao serviceCredentialsDao;

    public MsgServiceFactory(IServiceCredentialsDao serviceCredentialsDao)
    {
        this.serviceCredentialsDao = serviceCredentialsDao;
    }

    public MsgService getMsgService(ServiceCredentials credentials)
    {
        MsgService result = null;
        if(credentials.getProtocol().equals(ProtocolType.XMPP))
        {
            log.debug("Creating new XMPPMsgService for userId " + credentials.getUserId() );
            result = new XMPPMsgService();
            result.setCredentials(credentials);
        }
        else
        {
            log.warn("Currently unsupported protocol given");
        }

        return result;
    }

    public List<MsgService> getAll()
    {
        List<MsgService> msgServices = new ArrayList<MsgService>();
        List<ServiceCredentials> credentialsList;
        credentialsList = this.serviceCredentialsDao.getAll();

        ServiceCredentials sc1 = new ServiceCredentials("domdorn@jabber.fsinf.at", "somepass");
        sc1.setUuid("02918516-062d-4028-9d7a-ed0393d0a90d");
        sc1.setProtocol(ProtocolType.XMPP);
        try {
            sc1.setServerAddress(Inet4Address.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        sc1.setServerPort(9000);
        sc1.setEncryptionUsed(false);


        ServiceCredentials sc2 = new ServiceCredentials("pstein@jabber.fsinf.at", "somepass");
        sc2.setUuid("48cce803-c878-46d3-b1e6-6165f75dcf88");
        sc2.setProtocol(ProtocolType.XMPP);
        try {
            sc2.setServerAddress(Inet4Address.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        sc2.setServerPort(9000);
        sc2.setEncryptionUsed(false);


        ServiceCredentials sc3 = new ServiceCredentials("pstein@jabber.fsinf.at", "somepass");
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

        for(ServiceCredentials credentials : credentialsList)
        {
            MsgService service = this.getMsgService(credentials);
            msgServices.add(service);
        }
        return msgServices; 
    }
}
