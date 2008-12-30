package com.jakeapp.core.dao;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.XMPPUserId;
import com.jakeapp.core.domain.exceptions.InvalidUserIdException;

import java.util.UUID;

/**
 * Unit Tests for HibernateXMPPUserIdDaoTest
 */

@ContextConfiguration(locations = {"/com/jakeapp/core/dao/jake_core_test_hibernateGlobal_context.xml"})
public class HibernateXMPPUserIdDaoTest extends AbstractJUnit4SpringContextTests {
    private static final String DAO_BEAN_ID = "userIdDao";
    private static final String TEMPLATE_BEAN_ID = "hibernateTemplate";


    private IUserIdDao userIdDao ;
	private HibernateTemplate template;


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
        this.setUserIdDao( (IUserIdDao) this.applicationContext.getBean(DAO_BEAN_ID));
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
    public final void create_sampleValidUser() throws InvalidUserIdException {
        UserId user_domi = new XMPPUserId(UUID.fromString("fb101301-7af5-4e3d-a7d0-7faed7369bfb"),
                "domdorn@jabber.fsinf.at", "Domi", "Domnik", "Dodo" );
        userIdDao.persist(user_domi);
    }


    


}