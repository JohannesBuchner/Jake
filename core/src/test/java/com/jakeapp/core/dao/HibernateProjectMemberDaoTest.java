package com.jakeapp.core.dao;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;

import java.util.UUID;
import java.util.List;
import java.io.File;

/**
 * Unit test for the HibernateProjectMemberDao
 *
 * @author Simon
 */
@ContextConfiguration(locations = {"/com/jakeapp/core/dao/jake_core_test_hibernateLocal_context.xml"})
public class HibernateProjectMemberDaoTest extends AbstractJUnit4SpringContextTests {
    private final String PROJECTMEMBER_DAO_ID = "projectMemberDao";
    private final String TEMPLATE_BEAN_ID = "hibernateTemplate";


    private static final Logger log = Logger.getLogger(HibernateProjectMemberDaoTest.class);
    private IProjectMemberDao projectMemberDao;


    private final Project project = new Project("project",
            UUID.fromString("9d934920-f192-4374-b63e-b648b68ee258"), null, new File("test"));


    private final ServiceCredentials credentials = new ServiceCredentials();

    //private static final ProjectMember member1 = new ProjectMember(new XMPPUserId(new UUID(1, 1), "foo", "bar", "", ""), null);
    //private static final ProjectMember member2 = new ProjectMember(new XMPPUserId(new UUID(2, 2), "foo2", "bar2", "", ""), null);

    private HibernateTemplate template;


/*    private final ProjectMember member1 = new ProjectMember(
            new XMPPUserId(credentials, UUID.fromString("b01ceca6-6f21-411b-a998-09f7935f756f"),
                    "maria@jabber.xmas.org", "maria", "maria", "holy"
            ), null, TrustState.TRUST);

    private final ProjectMember member2 = new ProjectMember(
            new XMPPUserId(credentials, UUID.fromString("f8bfdf56-a0d3-42b6-b423-05683a1fae33"),
                    "jesus@jabber.xmas.org", "jesus!", "jesus", "christ"
            ), null, TrustState.TRUST);

    private final ProjectMember member3 = new ProjectMember(
            new XMPPUserId(credentials, UUID.fromString("75165694-e168-435b-b907-68ce82fbdb2d"),
                    "donkey@jabber.xmas.org", "donkey", "firstname", "surname"
            ), null, TrustState.TRUST);*/


    private final ProjectMember member1 = new ProjectMember(UUID.fromString("b01ceca6-6f21-411b-a998-09f7935f756f")
            , null, TrustState.TRUST);

    private final ProjectMember member2 = new ProjectMember(UUID.fromString("f8bfdf56-a0d3-42b6-b423-05683a1fae33")
            , null, TrustState.TRUST);

    private final ProjectMember member3 = new ProjectMember(UUID.fromString("75165694-e168-435b-b907-68ce82fbdb2d")
            , null, TrustState.TRUST);


    {
        credentials.setUuid("36e80380-9d12-4354-8a30-6371b5d617dd");
        credentials.setProtocol(ProtocolType.XMPP);
        credentials.setUserId("someuser@jabber.com");
    }

    public IProjectMemberDao getProjectMemberDao() {
        return projectMemberDao;
    }

    public void setProjectMemberDao(IProjectMemberDao projectMemberDao) {
        this.projectMemberDao = projectMemberDao;
    }

    @Before
    public void setUp() {
        log.debug("setup");
        this.setProjectMemberDao((IProjectMemberDao) this.applicationContext.getBean(PROJECTMEMBER_DAO_ID));
        this.setTemplate((HibernateTemplate) this.applicationContext.getBean(TEMPLATE_BEAN_ID));


        log.debug("CurrentSession: " + this.getTemplate().getSessionFactory().getCurrentSession().getTransaction());

        this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().begin();

    }

    @After
    public void tearDown() {
        log.debug("tearDown()");
        log.debug("CurrentSession: " + this.getTemplate().getSessionFactory().getCurrentSession().getTransaction());
        //this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().commit();
        /* rollback for true unit testing */
        this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().rollback();
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

    @Transactional
    @Test
    public void persist_persist() {
        try {
            log.info("Test: persist_persist...");
            projectMemberDao.persist(project, member1);
        } catch (RuntimeException e) {
            log.debug("failed to create entity...");
            e.printStackTrace();
            fail();
        }
    }

    @Transactional
    @Test
    public void persist_persistReadThroughGetAll() throws NoSuchProjectException {
        log.info("Test: persist_persistRead...");
        projectMemberDao.persist(project, member2);
        assertNotNull(projectMemberDao.getAll(project));


        List<ProjectMember> result = projectMemberDao.getAll(project);
        assertTrue(result.size() > 0);
        log.debug("result.size = " + result.size());
        for (ProjectMember member : result) {
            log.debug("member = " + member);
        }
        assertTrue(result.contains(member2));

    }

    @Transactional
    @Test
    public void persist_persistReadThroughGet() throws NoSuchProjectMemberException {
        log.info("Test: persist_persistRead...");
        projectMemberDao.persist(project, member2);

        ProjectMember member = projectMemberDao.get(member2.getUserId());

        assertNotNull(member);
        assertEquals(member2, member);

    }


    @Transactional
    @Test
    public void makeTransient_persistReadMakeTransientRead() {
        log.info("Test: makeTransient_persistReadMakeTransientRead...");
        projectMemberDao.persist(project, member3);
        try {
            assertNotNull(projectMemberDao.getAll(project));
            assertTrue(projectMemberDao.getAll(project).contains(member3));

            projectMemberDao.delete(project, member3);

            assertNotNull(projectMemberDao.getAll(project));
            assertFalse(projectMemberDao.getAll(project).contains(member3));

        } catch (Exception e) {
            log.debug("exception caught");
            e.printStackTrace();
            fail();
        }
    }

    @Transactional
    @Test(expected = NoSuchProjectMemberException.class)
    public void makeTransient_throwNoSuchProjectException() throws NoSuchProjectMemberException {
        log.info("Test: makeTransient_throwNoSuchProjectException...");
        projectMemberDao.delete(project, member2);
    }


    @Transactional
    @Test
    public void test_persistAndGet() throws NoSuchProjectMemberException {
        projectMemberDao.persist(project, member1);
        ProjectMember member = projectMemberDao.get(member1.getUserId());
        assertEquals("storage works", member1, member);
    }

}
