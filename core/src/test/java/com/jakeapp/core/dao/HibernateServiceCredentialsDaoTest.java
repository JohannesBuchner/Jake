package com.jakeapp.core.dao;

import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.log4j.Logger;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.net.InetAddress;


@ContextConfiguration(locations = {"/com/jakeapp/core/dao/jake_core_test_hibernateServiceCredentialsDao_context.xml"})
public class HibernateServiceCredentialsDaoTest extends AbstractJUnit4SpringContextTests {
    private static Logger log = Logger.getLogger(HibernateServiceCredentialsDaoTest.class);
    private static IServiceCredentialsDao serviceCredentialsDao;


    public static void setServiceCredentialsDao(IServiceCredentialsDao serviceCredentialsDao) {
        HibernateServiceCredentialsDaoTest.serviceCredentialsDao = serviceCredentialsDao;
    }


    @Before
    public void setUp() {
        setServiceCredentialsDao((IServiceCredentialsDao) applicationContext.getBean("serviceCredentialsDao"));

    }

    /**
     * This test tries to persist a null value which should not work.
     */
    @Test(expected = InvalidCredentialsException.class)
    public final void create_shouldFailPersistNull() throws InvalidCredentialsException {
        serviceCredentialsDao.create(null);
    }


    /**
     * This test tries to persist empty service credentials, which should not work.
     */
    @Test(expected = InvalidCredentialsException.class)
    public final void create_persistEmptyCredentials() throws InvalidCredentialsException {
        ServiceCredentials credentials = new ServiceCredentials();

        serviceCredentialsDao.create(credentials);
    }


    @Test
    public final void basicSetCredentialsTest() throws InvalidCredentialsException {
        ServiceCredentials credentials = new ServiceCredentials();
        credentials.setUuid("6c8b815b-c50c-4b49-a74a-3eefe9fa2977");
        credentials.setUserId("domdorn@jabber.fsinf.at");
        credentials.setPlainTextPassword("somePassword");
        credentials.setEncryptionUsed(true);
        credentials.setServerAddress("localhost");
        credentials.setServerPort(5222);

        log.info("basicSetCredentialsTest succeeded.");
    }


    @Test
    public final void create_persistBasicCredentialsTest() throws InvalidCredentialsException, UnknownHostException {
               ServiceCredentials credentials = new ServiceCredentials();
        credentials.setUuid("6c8b815b-c50c-4b49-a74a-3eefe9fa2977");
        credentials.setUserId("domdorn@jabber.fsinf.at");
        credentials.setPlainTextPassword("somePassword");
        credentials.setEncryptionUsed(true);

        InetAddress addr = Inet4Address.getLocalHost();

        credentials.setServerAddress(addr);
        credentials.setServerPort(5222);

        serviceCredentialsDao.create(credentials);
    }


}
