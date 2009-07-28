package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
import com.jakeapp.core.domain.logentries.LogEntry;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@ContextConfiguration
public abstract class EmptyTableAbstractLogEntryDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

	private static Logger log = Logger
			.getLogger(EmptyTableAbstractLogEntryDaoTest.class);



	private ILogEntryDao logEntryDao;

	private FileObject file1;

	private Project project;



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


		// TODO begin transaction


		fill();
	}

	private void fill() throws InvalidTagNameException {
		IMsgService msgService = mock(IMsgService.class);
		when(msgService.getProtocolType()).thenReturn(ProtocolType.XMPP);

		File file = new File(System.getProperty("user.dir"));

		project = new Project("test", u[0], msgService, file);

		file1 = new FileObject(u[2], project, "foo bar");
	}

	private static final UUID[] u = { new UUID(1, 0), new UUID(1, 1), new UUID(1, 2),
			new UUID(1, 3) };

	@After
	public void tearDown() {
		// TODO ROLLBACK TRANSACTION
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
		logEntryDao.getNextUnprocessed();
	}

	@Transactional
	@Test
	public void testFileTags() throws Exception {
		Collection<Tag> filetags = logEntryDao.getTags(file1);
		Assert.assertEquals(0, filetags.size());
	}

	@Transactional
	@Test
	public void testProjectMembers_empty() throws Exception {
		User user = new User(ProtocolType.XMPP, "I don't exist");
		List<User> members = logEntryDao.getCurrentProjectMembers(user);
		Assert.assertEquals(1, members.size());
		Assert.assertEquals(user, members.get(0));
		Assert.assertEquals(0, logEntryDao.getTrustGraph().size());
	}
	
/*
	@Transactional
	@Test
	public void testProjectCreator_invalid_empty() throws Exception {
		Assert.assertEquals("me", logEntryDao.getProjectCreatedEntry().getMember());
	}
*/
}
