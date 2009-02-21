package com.jakeapp.core.dao;

import java.io.File;
import java.util.Collection;
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
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.services.XMPPMsgService;

@ContextConfiguration(locations = { "/com/jakeapp/core/dao/jake_core_test_hibernateLocal_context.xml" })
public class EmptyTableHibernateLogEntryDaoTest extends AbstractJUnit4SpringContextTests {

	private static Logger log = Logger
			.getLogger(EmptyTableHibernateLogEntryDaoTest.class);

	private HibernateTemplate hibernateTemplate;

	private ILogEntryDao logEntryDao;

	private FileObject file1;

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

		file1 = new FileObject(u[2], project, "foo bar");
	}

	private static final UUID[] u = { new UUID(1, 0), new UUID(1, 1), new UUID(1, 2),
			new UUID(1, 3) };

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
		Assert.assertEquals(0, logEntryDao.getAll(true).size());
		Assert.assertEquals(0, logEntryDao.getAll(false).size());
		Assert.assertEquals(0, logEntryDao.getAllVersions(false).size());
		Assert.assertEquals(0, logEntryDao.getAllVersions(true).size());
		Assert
				.assertEquals(0, logEntryDao.getAllVersionsOfJakeObject(file1, true)
						.size());
		Assert.assertEquals(0, logEntryDao.getAllVersions(true).size());
	}

	@Transactional
	@Test
	public void testJakeObjects() throws Exception {
		Assert.assertNull(logEntryDao.getDeleteState(file1, true));
		Assert.assertNull(logEntryDao.getDeleteState(file1, false));
		Assert.assertEquals(0, logEntryDao.getExistingFileObjects(false).size());
		Assert.assertEquals(0, logEntryDao.getExistingFileObjects(true).size());
		Assert.assertEquals(null, logEntryDao.getLastVersion(file1, false));
		Assert.assertEquals(null, logEntryDao.getLastVersion(file1, true));
	}

	@Transactional
	@Test(expected = NoSuchLogEntryException.class)
	public void testgetLastVersionOfJakeObject_throws() throws Exception {
		logEntryDao.getLastVersionOfJakeObject(file1, false);
	}

	@Transactional
	@Test
	public void testlocks() throws Exception {
		Assert.assertEquals(null, logEntryDao.getLock(file1));
	}

	@Transactional
	@Test
	public void testUnprocessed() throws Exception {
		Collection<LogEntry<JakeObject>> up = logEntryDao.getUnprocessed();
		Assert.assertEquals(0, up.size());
	}

	@Transactional
	@Test(expected = NoSuchLogEntryException.class)
	public void testNextUnprocessed() throws Exception {
		LogEntry<JakeObject> next = logEntryDao.getNextUnprocessed();
	}

	@Transactional
	@Test
	public void testFileTags() throws Exception {
		Collection<Tag> filetags = logEntryDao.getTags(file1);
		Assert.assertEquals(0, filetags.size());
	}

	@Transactional
	@Test(expected = IndexOutOfBoundsException.class)
	public void testProjectMembers_invalid_empty() throws Exception {
		Assert.assertEquals(0, logEntryDao.getCurrentProjectMembers().size());
		Assert.assertEquals(0, logEntryDao.getTrustGraph().size());
	}

	@Transactional
	@Test(expected = IndexOutOfBoundsException.class)
	public void testProjectCreator_invalid_empty() throws Exception {
		Assert.assertEquals("me", logEntryDao.getProjectCreatedEntry().getMember());
	}

}
