package com.jakeapp.core.dao;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
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

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.JakeObjectLogEntry;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.LogEntryGenerator;
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

@ContextConfiguration(locations = { "/com/jakeapp/core/dao/jake_core_test_hibernateLocal_context.xml" })
public class ExtendedHibernateLogEntryDaoTest extends AbstractJUnit4SpringContextTests {

	private static Logger log = Logger.getLogger(ExtendedHibernateLogEntryDaoTest.class);

	private HibernateTemplate hibernateTemplate;

	private ILogEntryDao logEntryDao;

	private FileObject file1;

	private FileObject nofile;

	private NoteObject note1;

	private ProjectMember me;

	private Project project;

	public HibernateTemplate getHibernateTemplate() {
		return this.hibernateTemplate;
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public ILogEntryDao getLogEntryDao() {
		return this.logEntryDao;
	}

	public void setLogEntryDao(ILogEntryDao logEntryDao) {
		this.logEntryDao = logEntryDao;
	}

	@Before
	public void setUp() throws Exception {
		this
				.setLogEntryDao((ILogEntryDao) this.applicationContext
						.getBean("logEntryDao"));
		this.setHibernateTemplate((HibernateTemplate) this.applicationContext
				.getBean("hibernateTemplate"));
		this.getHibernateTemplate().getSessionFactory().getCurrentSession()
				.beginTransaction();

		fill();


	}

	private void fill() throws InvalidTagNameException {
		MsgService msgService = new XMPPMsgService();
		File file = new File(System.getProperty("user.dir"));


		project = new Project("test", u[0], msgService, file);


		me = new ProjectMember(u[1], "NICKNAME", TrustState.TRUST);

		LogEntry<Project> projectLogEntry = new ProjectLogEntry(u[2],
				LogAction.PROJECT_CREATED, new Date(2009, 01, 03), project, project,
				me, "comment", null, true);


		logEntryDao.create(projectLogEntry);

		note1 = new NoteObject(u[3], project, "foo bar");
		JakeObjectLogEntry note1announce = LogEntryGenerator.newLogEntry(note1,
				LogAction.JAKE_OBJECT_NEW_VERSION, project, me,
				"initial checkin", null, true);
		logEntryDao.create(note1announce);

		JakeObjectLogEntry note1announce2 = LogEntryGenerator.newLogEntry(note1,
				LogAction.JAKE_OBJECT_NEW_VERSION, project, me,
				"improved version", null, false);
		logEntryDao.create(note1announce2);

		file1 = new FileObject(u[5], project, "foo bar");
		JakeObjectLogEntry file1announce = LogEntryGenerator.newLogEntry(file1,
				LogAction.JAKE_OBJECT_NEW_VERSION, project, me, "my version",
				"mychecksum", true);
		logEntryDao.create(file1announce);
		JakeObjectLogEntry file1lock = LogEntryGenerator.newLogEntry(file1,
				LogAction.JAKE_OBJECT_LOCK, project, me, "locking ...", null,
				true);
		logEntryDao.create(file1lock);
		JakeObjectLogEntry file1unlock = LogEntryGenerator.newLogEntry(file1,
				LogAction.JAKE_OBJECT_UNLOCK, project, me, "unlocking ...",
				null, true);
		logEntryDao.create(file1unlock);
		JakeObjectLogEntry file1lock2 = LogEntryGenerator.newLogEntry(file1,
				LogAction.JAKE_OBJECT_LOCK, project, me, "locking ...", null,
				true);
		logEntryDao.create(file1lock2);
		JakeObjectLogEntry file1delete = LogEntryGenerator.newLogEntry(file1,
				LogAction.JAKE_OBJECT_DELETE, project, me, "I hate this file",
				"mychecksum", false);
		logEntryDao.create(file1delete);

		Tag tag = new Tag("tag1");
		tag.setObject(file1);
		Tag tag2 = new Tag("tag2");
		tag.setObject(file1);

		TagLogEntry file1tag = LogEntryGenerator.newLogEntry(tag, LogAction.TAG_ADD,
				project, me);
		logEntryDao.create(file1tag);
		TagLogEntry file1tag2 = LogEntryGenerator.newLogEntry(tag2, LogAction.TAG_ADD,
				project, me);
		logEntryDao.create(file1tag2);
		TagLogEntry file1untag = LogEntryGenerator.newLogEntry(tag, LogAction.TAG_REMOVE,
				project, me);
		logEntryDao.create(file1untag);

		nofile = new FileObject(u[13], project, "I dont exist");
	}

	private UUID[] u = { new UUID(1, 2), new UUID(1, 3), new UUID(1, 4), new UUID(1, 5),
			new UUID(1, 6), new UUID(1, 7), new UUID(1, 8), new UUID(1, 9),
			new UUID(1, 10), new UUID(1, 11), new UUID(1, 12), new UUID(1, 13),
			new UUID(1, 14), new UUID(1, 15) };

	@After
	public void tearDown() {
		this.getHibernateTemplate().getSessionFactory().getCurrentSession()
				.getTransaction().rollback();
	}

	@Transactional
	@Test
	// @Ignore
	public void testDump() {
		List<LogEntry<? extends ILogable>> results = logEntryDao.getAll(true);
		log.debug("DUMPING LOGENTRIES: ___");
		for (LogEntry<? extends ILogable> r : results) {
			log.debug((r.isProcessed() ? "P " : "UP") + " - " + r);
		}
		log.debug("DUMPING LOGENTRIES DONE");
	}

	@Transactional
	@Test
	public void testGetting() throws Exception {
		Assert.assertEquals(11, logEntryDao.getAll(true).size());
		Assert.assertEquals(9, logEntryDao.getAll(false).size());
		Assert.assertEquals(2, logEntryDao.getAllVersions(false).size());
		Assert.assertEquals(4, logEntryDao.getAllVersions(true).size());
		Assert
				.assertEquals(2, logEntryDao.getAllVersionsOfJakeObject(note1, true)
						.size());
		Assert
				.assertEquals(1, logEntryDao.getAllVersionsOfJakeObject(file1, true)
						.size());
		Assert.assertEquals(4, logEntryDao.getAllVersions(true).size());
	}

	@Transactional
	@Test
	public void testJakeObjects() throws Exception {
		Assert.assertEquals(Boolean.FALSE, logEntryDao.getDeleteState(note1, false));
		Assert.assertEquals(Boolean.FALSE, logEntryDao.getDeleteState(file1, false));
		Assert.assertEquals(Boolean.TRUE, logEntryDao.getDeleteState(file1, true));
		Assert.assertEquals(1, logEntryDao.getExistingFileObjects(false).size());
		Assert.assertEquals(0, logEntryDao.getExistingFileObjects(true).size());
		Assert.assertEquals(LogAction.JAKE_OBJECT_NEW_VERSION, logEntryDao
				.getLastOfJakeObject(note1, false).getLogAction());
		Assert.assertEquals(LogAction.TAG_REMOVE, logEntryDao.getLastOfJakeObject(file1,
				false).getLogAction());
		Assert.assertEquals(null, logEntryDao.getLastVersion(nofile, false));
		Assert.assertEquals("my version", logEntryDao.getLastVersion(file1, false)
				.getComment());
		Assert.assertEquals(null, logEntryDao.getLastVersion(file1, true));
		Assert.assertEquals("initial checkin", logEntryDao.getLastVersion(note1, false)
				.getComment());
		Assert.assertEquals("improved version", logEntryDao.getLastVersion(note1, true)
				.getComment());
	}

	@Transactional
	@Test(expected = NoSuchLogEntryException.class)
	public void testgetLastVersionOfJakeObject_throws() throws Exception {
		logEntryDao.getLastVersionOfJakeObject(nofile, false);
	}

	@Transactional
	@Test
	public void testgetLastVersionOfJakeObject() throws Exception {
		Assert.assertEquals("my version", logEntryDao.getLastVersionOfJakeObject(file1,
				true).getComment());
		Assert.assertEquals("my version", logEntryDao.getLastVersionOfJakeObject(file1,
				false).getComment());
		Assert.assertEquals("initial checkin", logEntryDao.getLastVersionOfJakeObject(
				note1, false).getComment());
		Assert.assertEquals("improved version", logEntryDao.getLastVersionOfJakeObject(
				note1, true).getComment());
	}

	@Transactional
	@Test
	public void testlocks() throws Exception {
		Assert.assertEquals(null, logEntryDao.getLock(note1));
		Assert.assertEquals(null, logEntryDao.getLock(nofile));
		Assert.assertEquals("locking ...", logEntryDao.getLock(file1).getComment());
	}

	@Transactional
	@Test
	public void testUnprocessed() throws Exception {
		Collection<LogEntry<JakeObject>> up = logEntryDao.getUnprocessed();
		Assert.assertEquals(2, up.size());
		Iterator<LogEntry<JakeObject>> it = up.iterator();
		Assert.assertFalse(it.next().isProcessed());
		Assert.assertFalse(it.next().isProcessed());
		LogEntry<JakeObject> next = logEntryDao.getNextUnprocessed();
		Assert.assertFalse(next.isProcessed());
		Assert.assertEquals(u[3], next.getBelongsTo().getUuid());
		logEntryDao.setProcessed(next);
		Assert.assertTrue(next.isProcessed());
		Assert.assertEquals(u[5], logEntryDao.getNextUnprocessed().getBelongsTo()
				.getUuid());
		Assert.assertEquals(1, logEntryDao.getUnprocessed().size());
		Assert.assertFalse(logEntryDao.hasUnprocessed(note1));
		Assert.assertTrue(logEntryDao.hasUnprocessed(file1));
	}

	@Transactional
	@Test
	public void testProjectMembers_empty() throws Exception {
		Assert.assertEquals(0, logEntryDao.getCurrentProjectMembers().size());

		Assert.assertEquals(1, logEntryDao.getTrustGraph().size());
	}

	@Transactional
	@Test
	public void testProjectCreator() throws Exception {
		Assert.assertEquals("NICKNAME", logEntryDao.getProjectCreatedEntry().getMember()
				.getNickname());
	}

	@Transactional
	@Test
	public void testProjectMembers() throws Exception {
		ProjectMember member1 = new ProjectMember(UUID.randomUUID(), "max",
				TrustState.TRUST);
		ProjectMember member2 = new ProjectMember(UUID.randomUUID(), "jack",
				TrustState.TRUST);
		ProjectMember member3 = new ProjectMember(UUID.randomUUID(), "rick",
				TrustState.TRUST);
		logEntryDao.create(LogEntryGenerator.newLogEntry(member1,
				LogAction.START_TRUSTING_PROJECTMEMBER, project, me));
		logEntryDao.create(LogEntryGenerator.newLogEntry(member2,
				LogAction.START_TRUSTING_PROJECTMEMBER, project, me));
		logEntryDao.create(LogEntryGenerator.newLogEntry(member2,
				LogAction.START_TRUSTING_PROJECTMEMBER, project, member1));
		logEntryDao.create(LogEntryGenerator.newLogEntry(member3,
				LogAction.START_TRUSTING_PROJECTMEMBER, project, member1));

		Assert.assertEquals(3, logEntryDao.getCurrentProjectMembers().size());

		Assert.assertEquals(4, logEntryDao.getTrustGraph().size());
		logEntryDao.create(LogEntryGenerator.newLogEntry(member3,
				LogAction.STOP_TRUSTING_PROJECTMEMBER, project, member1));
		Assert.assertEquals(2, logEntryDao.getCurrentProjectMembers().size());

		Assert.assertEquals(4, logEntryDao.getTrustGraph().size());
	}


}
