package com.jakeapp.core.dao;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.logentries.*;
import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.services.XMPPMsgService;

@ContextConfiguration(locations = { "/com/jakeapp/core/dao/jake_core_test_hibernateLocal_context.xml" })
public class HibernateLogEntryDaoTest extends AbstractJUnit4SpringContextTests {

	private static Logger log = Logger.getLogger(HibernateLogEntryDaoTest.class);

	private HibernateTemplate hibernateTemplate;

	private ILogEntryDao logEntryDao;

	private User projectMember = new User(ProtocolType.XMPP, "me");
	
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
		this
				.setLogEntryDao((ILogEntryDao) this.applicationContext
						.getBean("logEntryDao"));
		this.setHibernateTemplate((HibernateTemplate) this.applicationContext
				.getBean("hibernateTemplate"));
		this.getHibernateTemplate().getSessionFactory().getCurrentSession()
				.beginTransaction();
	}

	@After
	public void tearDown() {
		// this.getHibernateTemplate().getSessionFactory().getCurrentSession().
		// getTransaction().commit();
		/* rollback for true unit testing */
		this.getHibernateTemplate().getSessionFactory().getCurrentSession()
				.getTransaction().rollback();
	}


	@Transactional
	@Test
	public void testCreate() {
		MsgService msgService = new XMPPMsgService();
		File file = new File(System.getProperty("user.dir"));


		Project testProject = new Project("test", UUID
				.fromString("e0cd2322-6766-40a0-82c5-bcc0fe7a67c2"), msgService, file);


		User projectMember = new User(ProtocolType.XMPP, "me");

		ProjectCreatedLogEntry projectLogEntry = new ProjectCreatedLogEntry(testProject, projectMember);


		logEntryDao.create(projectLogEntry);
	}

	@Transactional
	@Test
	public void testGetAll_xxx() {
		MsgService msgService = new XMPPMsgService();
		File file = new File(System.getProperty("user.dir"));


		Project testProject = new Project("test", UUID
				.fromString("e0cd2322-aaaa-40a0-82c5-bcc0fe7a67c2"), msgService, file);



		ProjectCreatedLogEntry projectLogEntry = new ProjectCreatedLogEntry(testProject, projectMember);

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


		Project testProject = new Project("test", UUID
				.fromString("e0cd2322-aaaa-40a0-82c5-bcc0fe7a67c2"), msgService, file);


		Tag t1 = new Tag("test");
		t1.setObject(new NoteObject(testProject, "foo bar"));

		LogEntry<Tag> tagLogEntry = new TagAddLogEntry(t1, projectMember);

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


		Project testProject = new Project("test", UUID
				.fromString("e0cd2322-aaaa-40a0-cccc-bcc0fe7a67c2"), msgService, file);


		NoteObject note = new NoteObject(UUID
				.fromString("509161b3-999e-4cb8-914b-31816c54c2ca"), testProject,
				"content");

		LogEntry<JakeObject> noteObjectLogEntry = new JakeObjectNewVersionLogEntry(note, projectMember,
				"testGetAll_NoteObjectLogEntry", "testGetAll_NoteObjectLogEntry", false);

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


		Project testProject = new Project("test", UUID
				.fromString("e0cd2322-aaaa-40a0-cccc-bcc0fe7a67c2"), msgService, file);


		FileObject fileObject = new FileObject(UUID
				.fromString("35fd9e4d-7810-4110-a1d1-7db0c1c10068"), testProject, "/tmp");


		LogEntry<JakeObject> fileObjectLogEntry = new JakeObjectNewVersionLogEntry(	fileObject, projectMember,
				"testGetAll_FileObjectLogEntry", "testGetAll_FileObjectLogEntry", false);

		logEntryDao.create(fileObjectLogEntry);

		List<LogEntry<? extends ILogable>> result = logEntryDao.getAll(true);


		log.debug("result.size() = " + result.size());
		Assert.assertTrue(result.size() > 0);

		Assert.assertTrue(result.contains(fileObjectLogEntry));

	}

	@Transactional
	@Test
	public void testGet() throws Exception {
		MsgService msgService = new XMPPMsgService();
		File file = new File(System.getProperty("user.dir"));


		Project testProject = new Project("test", UUID
				.fromString("e0cd2322-6766-40a0-82c5-bcc0fe7a67c2"), msgService, file);

		User projectMember = new User(ProtocolType.XMPP, "me");

		ProjectCreatedLogEntry projectLogEntry = new ProjectCreatedLogEntry(testProject, projectMember);
		UUID uuid = new UUID(31,312);
		projectLogEntry.setUuid(uuid);
		
		logEntryDao.create(projectLogEntry);
		
		LogEntry<? extends ILogable> actual = logEntryDao.get(uuid, true);
		Assert.assertEquals(projectLogEntry, actual);
		
	}
	
	/**
	 * @return
	 */
	private void initProjectAndMembers(User projectMember, User invitedProjectMember) {
		MsgService msgService = new XMPPMsgService();
		File file = new File(System.getProperty("user.dir"));
		Project testProject = new Project("test", UUID
				.fromString("e0cd2322-6766-40a0-82c5-bcc0fe7a67c2"), msgService, file);
		
		//create Project
		ProjectCreatedLogEntry projectLogEntry = new ProjectCreatedLogEntry(testProject, projectMember);
		UUID uuid = new UUID(31,312);
		projectLogEntry.setUuid(uuid);
		logEntryDao.create(projectLogEntry);
		
		StartTrustingProjectMemberLogEntry trustingEntry =
			new StartTrustingProjectMemberLogEntry(invitedProjectMember,projectMember);	
		logEntryDao.create(trustingEntry);
	}
	
	@Transactional
	@Test
	public void getCurrentProjectMembers_shouldReturnProjectUser() {
		User projectMember = new User(ProtocolType.XMPP, "me");
		User invitedProjectMember = new User(ProtocolType.XMPP, "you");
		this.initProjectAndMembers(projectMember,invitedProjectMember);
		
		//get user
		List<User> members = logEntryDao.getCurrentProjectMembers(projectMember);
		Assert.assertThat(members.size(), CoreMatchers.not(0));
		Assert.assertThat(members,JUnitMatchers.hasItem(projectMember));
	}

	@Transactional
	@Test
	public void getCurrentProjectMembers_shouldReturnInvitedUser() {
		User projectMember = new User(ProtocolType.XMPP, "projectFounder@projects.org");
		User invitedProjectMember = new User(ProtocolType.XMPP, "jake@jake.at");
		this.initProjectAndMembers(projectMember,invitedProjectMember);
		
		//get user
		List<User> members = logEntryDao.getCurrentProjectMembers(projectMember);
		Assert.assertThat(members.size(), CoreMatchers.not(0));
		Assert.assertThat(members, JUnitMatchers.hasItem(invitedProjectMember));
	}

}
