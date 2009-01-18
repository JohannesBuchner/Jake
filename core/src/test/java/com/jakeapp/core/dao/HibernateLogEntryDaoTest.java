package com.jakeapp.core.dao;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.apache.log4j.Logger;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.services.XMPPMsgService;
import com.jakeapp.core.services.MsgService;

import java.util.UUID;
import java.util.Date;
import java.util.List;
import java.io.File;

@ContextConfiguration(locations = {"/com/jakeapp/core/dao/jake_core_test_hibernateLocal_context.xml"})
public class HibernateLogEntryDaoTest extends AbstractJUnit4SpringContextTests {

    private static Logger log = Logger.getLogger(HibernateLogEntryDaoTest.class);

    private HibernateTemplate hibernateTemplate;
    private ILogEntryDao logEntryDao;

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public ILogEntryDao getLogEntryDao() {
        return logEntryDao;
    }

    public void setLogEntryDao(ILogEntryDao logEntryDao) {
        this.logEntryDao = logEntryDao;
    }

    @Before
    public void setUp() {
        this.setLogEntryDao((ILogEntryDao) this.applicationContext.getBean("logEntryDao"));
        this.setHibernateTemplate((HibernateTemplate) this.applicationContext.getBean("hibernateTemplate"));
        this.getHibernateTemplate().getSessionFactory().getCurrentSession().beginTransaction();
    }

    @After
    public void tearDown() {
        this.getHibernateTemplate().getSessionFactory().getCurrentSession().getTransaction().commit();
        /* rollback for true unit testing */
//        this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().rollback();
    }


    @Transactional
    @Test
    public void testCreate() {
        MsgService msgService = new XMPPMsgService();
        File file = new File(System.getProperty("user.dir"));


        Project testProject =
                new Project("test", UUID.fromString("e0cd2322-6766-40a0-82c5-bcc0fe7a67c2"), msgService, file);


        ProjectMember projectMember = new ProjectMember(UUID.fromString("e144ad8a-3fd4-4d91-b9b9-73415866048f"),
                "NICKNAME", TrustState.TRUST
        );

        LogEntry<Project> projectLogEntry = new ProjectLogEntry(
                UUID.fromString("e144ad8a-3fd4-4d91-b9b9-73415866048f"),
                LogAction.PROJECT_CREATED, new Date(), testProject, testProject,
                projectMember, "comment", "djskjslkj", false
        );

        
        logEntryDao.create(projectLogEntry);
    }

    @Test
    public void testGet() {
        // Add your code here
    }

    @Transactional
    @Test
    public void testGetAll() {
                MsgService msgService = new XMPPMsgService();
        File file = new File(System.getProperty("user.dir"));


        Project testProject =
                new Project("test", UUID.fromString("e0cd2322-aaaa-40a0-82c5-bcc0fe7a67c2"), msgService, file);


        ProjectMember projectMember = new ProjectMember(UUID.fromString("e144ad8a-aaaa-4d91-b9b9-73415866048f"),
                "NICKNAME", TrustState.TRUST
        );

        LogEntry<Project> projectLogEntry = new ProjectLogEntry(
                UUID.fromString("e144ad8a-aaaa-4d91-b9b9-73415866048f"),
                LogAction.PROJECT_CREATED, new Date(), testProject, testProject,
                projectMember, "testGetAll", "testGetAll_hash", false
        );

        logEntryDao.create(projectLogEntry);





        List<LogEntry<? extends ILogable>> result = logEntryDao.getAll();


        log.debug("result.size() = " + result.size());
        Assert.assertTrue(result.size() > 0);

        Assert.assertTrue(result.contains(projectLogEntry));
        
    }

    @Test
    public void testGetAllOfJakeObject() {
        // Add your code here
    }

    @Test
    public void testGetMostRecentFor() {
        // Add your code here
    }

    @Test
    public void testGetLastPulledFor() {
        // Add your code here
    }

    @Test
    public void testSetProcessed() {
        // Add your code here
    }

    @Test
    public void testGetAllUnprocessed() {
        // Add your code here
    }

    @Test
    public void testGetUnprocessed() {
        // Add your code here
    }
}
