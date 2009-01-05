package com.jakeapp.core.dao;

import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.apache.log4j.Logger;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.dao.exceptions.NoSuchServiceCredentialsException;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;


@ContextConfiguration(locations = {"/com/jakeapp/core/dao/jake_core_test_hibernateGlobal_context.xml"})
public class HibernateServiceCredentialsDaoTest extends AbstractJUnit4SpringContextTests {
    private static Logger log = Logger.getLogger(HibernateServiceCredentialsDaoTest.class);
    private static IServiceCredentialsDao serviceCredentialsDao;

    ServiceCredentials validCredentials;

    public static void setServiceCredentialsDao(IServiceCredentialsDao serviceCredentialsDao) {
        HibernateServiceCredentialsDaoTest.serviceCredentialsDao = serviceCredentialsDao;
    }


    @Before
    public void setUp() throws UnknownHostException {
        setServiceCredentialsDao((IServiceCredentialsDao) applicationContext.getBean("serviceCredentialsDao"));


        validCredentials = new ServiceCredentials();
        validCredentials.setUuid("6c8b815b-c50c-4b49-a74a-3eefe9fa2977");
        validCredentials.setUserId("domdorn@jabber.fsinf.at");
        validCredentials.setPlainTextPassword("somePassword");
        validCredentials.setEncryptionUsed(true);

        InetAddress addr = InetAddress.getLocalHost();

        validCredentials.setServerAddress(addr);
        validCredentials.setServerPort(5222);

        validCredentials.setProtocol(ProtocolType.XMPP);


        HibernateTemplate template = (HibernateTemplate) applicationContext.getBean("hibernateTemplate");

        template.getSessionFactory().getCurrentSession().getTransaction().begin();

    }

    @After
    public void tearDown() {
        HibernateTemplate template = (HibernateTemplate) applicationContext.getBean("hibernateTemplate");

        /* commit transactions to test if they are really working */
        template.getSessionFactory().getCurrentSession().getTransaction().commit();

        /* rollback for true unit testing */
//        template.getSessionFactory().getCurrentSession().getTransaction().rollback();

    }

    /**
     * This test tries to persist a null value which should not work.
     * @throws com.jakeapp.core.domain.exceptions.InvalidCredentialsException
     */
    @Test(expected = InvalidCredentialsException.class)
    @Transactional
    public final void create_shouldFailPersistNull() throws InvalidCredentialsException {
        serviceCredentialsDao.create(null);
    }


    /**
     * This test tries to persist empty service credentials, which should not work.
     * @throws com.jakeapp.core.domain.exceptions.InvalidCredentialsException
     */
    @Test(expected = InvalidCredentialsException.class)
    @Transactional
    public final void create_persistEmptyCredentials() throws InvalidCredentialsException {
        ServiceCredentials credentials = new ServiceCredentials();

        serviceCredentialsDao.create(credentials);
    }


    /**
     * Basic test that simply tries to create valid ServiceCredentials, to find out if this
     * is the problem when other tests fail.
     *
     * @throws InvalidCredentialsException
     * @throws UnknownHostException
     */
    @Test
    @Transactional
    public final void basicSetCredentialsTest() throws InvalidCredentialsException, UnknownHostException {
        ServiceCredentials credentials = new ServiceCredentials();
        //credentials.setUuid("6c8b815b-c50c-4b49-a74a-3eefe9fa2977");
        credentials.setUuid("57e81674-03a1-4422-b05e-c9c9b6eeeb2a");
        credentials.setUserId("domdorn@jabber.fsinf.at");
        credentials.setPlainTextPassword("somePassword");
        credentials.setEncryptionUsed(true);
        credentials.setServerAddress(InetAddress.getLocalHost());
        credentials.setServerPort(5222);

        log.info("basicSetCredentialsTest succeeded.");
    }

    /**
     * Simple test to persist valid credentials
     *
     * @throws InvalidCredentialsException
     * @throws UnknownHostException
     */
    @Test
    @Transactional
    public final void create_persistBasicCredentialsTest() throws InvalidCredentialsException, UnknownHostException {


        serviceCredentialsDao.create(validCredentials);
    }


    @Test
    @Transactional
    public final void createRead_test() throws InvalidCredentialsException, NoSuchServiceCredentialsException {
        ServiceCredentials result;
        validCredentials.setUuid("9c16a0d1-5ee1-4df9-9a3c-f5e4b5dcc0b3");
        serviceCredentialsDao.create(validCredentials);

        result = serviceCredentialsDao.read(UUID.fromString(validCredentials.getUuid()));

        assertEquals(validCredentials, result);


    }


}
