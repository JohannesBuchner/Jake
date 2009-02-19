package com.jakeapp.core.dao;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObjectLogEntry;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectLogEntry;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.domain.TagLogEntry;
import com.jakeapp.core.domain.TrustState;
import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.services.XMPPMsgService;

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
//        this.getHibernateTemplate().getSessionFactory().getCurrentSession().getTransaction().commit();
        /* rollback for true unit testing */
        this.getHibernateTemplate().getSessionFactory().getCurrentSession().getTransaction().rollback();
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

    @Transactional
    @Test
    public void testGetAll_xxx() {
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


        List<LogEntry<? extends ILogable>> result = logEntryDao.getAll(true);


        log.debug("result.size() = " + result.size());
        Assert.assertTrue(result.size() > 0);

        Assert.assertTrue(result.contains(projectLogEntry));

    }

    @Test
    public void testGetAllOfJakeObject_NonExistant() {
    	NoteObject no = new NoteObject(null, "hello");
        List<LogEntry<NoteObject>> result = logEntryDao.getAllOfJakeObject(no, true);


        log.debug("result.size() = " + result.size());
        Assert.assertEquals(0, result.size());
    }

    @Transactional
    @Test
    public void testGetAll_TagLogEntry() throws InvalidTagNameException {
        MsgService msgService = new XMPPMsgService();
        File file = new File(System.getProperty("user.dir"));


        Project testProject =
                new Project("test", UUID.fromString("e0cd2322-aaaa-40a0-82c5-bcc0fe7a67c2"), msgService, file);


        ProjectMember projectMember = new ProjectMember(UUID.fromString("e144ad8a-aaaa-4d91-b9b9-73415866048f"),
                "NICKNAME", TrustState.TRUST
        );

        Tag t1 = new Tag("test");


        LogEntry<Tag> tagLogEntry = new TagLogEntry(
                UUID.fromString("e144ad8a-0001-1111-cccc-73415866048f"),
                LogAction.TAG_ADD, new Date(), testProject, t1,
                projectMember, "testGetAll_TagLogEntry", "testGetAll_TagLogEntry", false
        );

        logEntryDao.create(tagLogEntry);

        List<LogEntry<? extends ILogable>> result = logEntryDao.getAll(true);


        log.debug("result.size() = " + result.size());
        Assert.assertTrue(result.size() > 0);

        Assert.assertTrue(result.contains(tagLogEntry));

    }

    @Transactional
    @Test
    public void testGetAll_NoteObjectLogEntry() throws InvalidTagNameException {
        MsgService msgService = new XMPPMsgService();
        File file = new File(System.getProperty("user.dir"));


        Project testProject =
                new Project("test", UUID.fromString("e0cd2322-aaaa-40a0-cccc-bcc0fe7a67c2"), msgService, file);


        ProjectMember projectMember = new ProjectMember(UUID.fromString("e144ad8a-aaaa-4d91-b9b9-73415866048f"),
                "NICKNAME", TrustState.TRUST
        );

        NoteObject note = new NoteObject(UUID.fromString("509161b3-999e-4cb8-914b-31816c54c2ca"), testProject, "content");

        JakeObjectLogEntry noteObjectLogEntry = new JakeObjectLogEntry(
                UUID.fromString("e144ad8a-0002-2222-cccc-73415866048f"),
                LogAction.TAG_ADD, new Date(), testProject, note,
                projectMember, "testGetAll_NoteObjectLogEntry", "testGetAll_NoteObjectLogEntry", false
        );

        logEntryDao.create(noteObjectLogEntry);

        List<LogEntry<? extends ILogable>> result = logEntryDao.getAll(true);


        log.debug("result.size() = " + result.size());
        Assert.assertTrue(result.size() > 0);

        Assert.assertTrue(result.contains(noteObjectLogEntry));

    }


    @Transactional
    @Test
    public void testGetAll_FileObjectLogEntry() throws InvalidTagNameException {
        MsgService msgService = new XMPPMsgService();
        File file = new File(System.getProperty("user.dir"));


        Project testProject =
                new Project("test", UUID.fromString("e0cd2322-aaaa-40a0-cccc-bcc0fe7a67c2"), msgService, file);


        ProjectMember projectMember = new ProjectMember(UUID.fromString("e144ad8a-aaaa-4d91-b9b9-73415866048f"),
                "NICKNAME", TrustState.TRUST
        );


        FileObject fileObject = new FileObject(UUID.fromString("35fd9e4d-7810-4110-a1d1-7db0c1c10068"), testProject, "/tmp");


        JakeObjectLogEntry fileObjectLogEntry = new JakeObjectLogEntry(
                UUID.fromString("e144ad8a-0003-2222-cccc-73415866048f"),
                LogAction.TAG_ADD, new Date(), testProject, fileObject,
                projectMember, "testGetAll_FileObjectLogEntry", "testGetAll_FileObjectLogEntry", false
        );

        logEntryDao.create(fileObjectLogEntry);

        List<LogEntry<? extends ILogable>> result = logEntryDao.getAll(true);


        log.debug("result.size() = " + result.size());
        Assert.assertTrue(result.size() > 0);

        Assert.assertTrue(result.contains(fileObjectLogEntry));

    }

}
