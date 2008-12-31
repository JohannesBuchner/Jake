package com.jakeapp.core.dao;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.XMPPUserId;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.exceptions.InvalidUserIdException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.dao.exceptions.NoSuchUserIdException;

import java.util.UUID;
import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * Unit Tests for HibernateXMPPUserIdDaoTest
 */

@ContextConfiguration(locations = {"/com/jakeapp/core/dao/jake_core_test_hibernateGlobal_context.xml"})
public class HibernateXMPPUserIdDaoTest extends AbstractJUnit4SpringContextTests {
    private static final String USERID_DAO_BEAN_ID = "userIdDao";
    private static final String SC_DAO_BEAN_ID = "serviceCredentialsDao";
    private static final String TEMPLATE_BEAN_ID = "hibernateTemplate";


    private IUserIdDao userIdDao ;
	private IServiceCredentialsDao serviceCredentialsDao;
    private HibernateTemplate template;


    public IServiceCredentialsDao getServiceCredentialsDao() {
        return serviceCredentialsDao;
    }

    public void setServiceCredentialsDao(IServiceCredentialsDao serviceCredentialsDao) {
        this.serviceCredentialsDao = serviceCredentialsDao;
    }

    public IUserIdDao getUserIdDao() {
        return userIdDao;
    }

    public void setUserIdDao(IUserIdDao userIdDao) {
        this.userIdDao = userIdDao;
    }

    /**
	 * @return the template
	 */
	private HibernateTemplate getTemplate() {
		return template;
	}

	/**
	 * @param template the template to set
	 */
	private void setTemplate(HibernateTemplate template) {
		this.template = template;
	}

    @Before
    public void setUp() {
        this.setUserIdDao( (IUserIdDao) this.applicationContext.getBean(USERID_DAO_BEAN_ID));
        this.setServiceCredentialsDao( (IServiceCredentialsDao) this.applicationContext.getBean(SC_DAO_BEAN_ID));
    	this.setTemplate( (HibernateTemplate) applicationContext.getBean(HibernateXMPPUserIdDaoTest.TEMPLATE_BEAN_ID) );
    	this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().begin();
    }

    @After
    public void tearDown() {
        this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().commit();
        /* rollback for true unit testing */
        //this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().rollback();
    }


    @Test(expected = InvalidUserIdException.class)
    public final void createNullValue_shouldFail() throws InvalidUserIdException {
      userIdDao.persist(null);
    }


    @Test
    @Transactional
    public final void create_sampleValidUser() throws InvalidUserIdException, UnknownHostException,
            InvalidCredentialsException {
        ServiceCredentials credentials = new ServiceCredentials() ;
        credentials.setUuid(UUID.fromString("0c7e2ef4-9422-4140-b1ff-426c10684357"));
        credentials.setProtocol(ProtocolType.XMPPP);
        credentials.setUserId("michael@mayers.com");
        credentials.setServerAddress(Inet4Address.getLocalHost());
        credentials.setServerPort(5000);

        serviceCredentialsDao.create(credentials);

        UserId user_domi = new XMPPUserId(
                credentials,
                UUID.fromString("fb101301-7af5-4e3d-a7d0-7faed7369bfb"),
                "domdorn@jabber.fsinf.at", "Domi", "Domnik", "Dodo" );
        userIdDao.persist(user_domi);
    }



    @Test
    @Transactional
    public final void createRead_sampleValidUser() throws UnknownHostException, InvalidCredentialsException,
            InvalidUserIdException, NoSuchUserIdException {
       ServiceCredentials credentials = new ServiceCredentials() ;
        credentials.setUuid(UUID.fromString("a4515dd9-8208-412a-943e-5059cc6ce0f5"));
        credentials.setProtocol(ProtocolType.XMPPP);
        credentials.setUserId("stefan@jabber.mueller.com");
        credentials.setServerAddress(Inet4Address.getLocalHost());
        credentials.setServerPort(5000);

        serviceCredentialsDao.create(credentials);

        UserId user_someFriend = new XMPPUserId(
                credentials,
                UUID.fromString("9b1360d9-f8ae-45ed-9005-fbce103dcf0a"),
                "someFriend@jabber.org", "Some", "Friend", "SomeFriend" );
        userIdDao.persist(user_someFriend);



        UserId result = userIdDao.read(UUID.fromString("9b1360d9-f8ae-45ed-9005-fbce103dcf0a"));


        assertEquals(user_someFriend, result);
    }

    


}