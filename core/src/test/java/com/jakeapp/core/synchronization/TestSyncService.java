package com.jakeapp.core.synchronization;

import com.googlecode.junit.ext.Prerequisite;
import com.googlecode.junit.ext.PrerequisiteAwareClassRunner;
import com.jakeapp.core.AllowSlowChecker;
import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.jake.test.FSTestCommons;
import com.jakeapp.jake.test.TmpdirEnabledTestCase;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


@RunWith(PrerequisiteAwareClassRunner.class)
public class TestSyncService extends TmpdirEnabledTestCase {

	private static Logger log = Logger.getLogger(TestSyncService.class);


	private static final String MODIFIED_CONTENT = "my new content";

	private HibernateTemplate hibernateTemplate;

	private static final String ORIGINAL_CONTENT = "hello foo bar\nbla";

	private IFrontendService frontend;

	private String sessionId;

	private IProjectsManagingService pms;

	private IFriendlySyncService sync;

	private ProjectApplicationContextFactory db;

	private Project project;

	private static final String id = "testuser1@my.provider";

	private static final String password = "mypasswd";

	private NoteObject note;

	private User me;

	private ILogEntryDao logEntryDao;

	@Override
	@Before
	@Transactional
	public void setup() throws Exception {
		super.setup();
		FSTestCommons.recursiveDelete(new File(".jake"));
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "/com/jakeapp/core/applicationContext.xml" });
		frontend = (IFrontendService) applicationContext.getBean("frontendService");

		sessionId = frontend.authenticate(new HashMap<String, String>(), null);
		pms = frontend.getProjectsManagingService(sessionId);
		sync = frontend.getSyncService(sessionId);
		db = (ProjectApplicationContextFactory) applicationContext
				.getBean("applicationContextFactory");
		createProject();
		testLogEntriesCount(1);
		me = pms.getProjectUsers(project).get(0);
		note = new NoteObject(project, ORIGINAL_CONTENT);
	}

	@Transactional
	public void createProject() throws Exception {
		Account cred = new Account(id, password, ProtocolType.XMPP);
		MsgService msg = frontend.addAccount(sessionId, cred);

		project = pms.createProject(tmpdir.getName(), tmpdir.getAbsolutePath(), msg);

		testLogEntriesCount(0);

		Assert.assertNotNull(project.getMessageService());
		Assert.assertNotNull(project.getUserId());

		Assert.assertEquals(1, pms.getProjectUsers(project).size());
		Assert.assertNotNull(pms.getProjectUsers(project).get(0));
		Assert.assertEquals(project.getUserId().getUserId(), pms.getProjectUsers(project)
				.get(0).getUserId());

		Assert.assertEquals(msg.getUserId(), project.getUserId());
		testLogEntriesCount(1);
	}

	@Transactional
	private void testLogEntriesCount(int count) {
		// this throws a noSessionBoundToThreadException. why?
		//Assert.assertEquals(count, db.getUnprocessedAwareLogEntryDao(project)
		//		.getAll(true).size());
	}


	@Test
	@Transactional
	public void testStatus_NonExistantNote() throws Exception {
		Attributed<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		testLogEntriesCount(1);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.NON_EXISTANT, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
		testLogEntriesCount(1);

	}

	@Test
	public void testStatus_NewNote() throws Exception {
		pms.saveNote(note);
		Assert.assertNotNull(note.getUuid());
		Attributed<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_LOCAL, status.getExistence());
		Assert.assertEquals(SyncStatus.MODIFIED_LOCALLY, status.getSyncStatus());
	}


	@Test
	public void testStatus_AnnounceNote() throws Exception {
		testStatus_NewNote();
		sync.announce(note, LogAction.JAKE_OBJECT_NEW_VERSION, "done");

		Attributed<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_ON_BOTH, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
	}


	@Test
	public void testStatus_ModifyAnnouncedNote() throws Exception {
		testStatus_AnnounceNote();
		note.setContent(MODIFIED_CONTENT);
		pms.saveNote(note);

		Attributed<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_ON_BOTH, status.getExistence());
		Assert.assertEquals(SyncStatus.MODIFIED_LOCALLY, status.getSyncStatus());
	}


	@Test
	public void testStatus_PullModifyNote() throws Exception {
		testStatus_ModifyAnnouncedNote();
		NoteObject revertedNote = sync.pullObject(note);
		Assert.assertEquals(ORIGINAL_CONTENT, revertedNote.getContent());
		Assert.assertEquals(MODIFIED_CONTENT, note.getContent());

		Attributed<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(ORIGINAL_CONTENT, status.getJakeObject().getContent());
		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_ON_BOTH, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
	}


	@Test
	public void testStatus_DeleteNewNote() throws Exception {
		testStatus_NewNote();
		pms.deleteNote(note);

		Attributed<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.NON_EXISTANT, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
	}

	@Test
	public void     testStatus_DeleteAnnouncedNote() throws Exception {
		testStatus_AnnounceNote();
		pms.deleteNote(note);

		Attributed<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_REMOTE, status.getExistence());
		Assert.assertEquals(SyncStatus.MODIFIED_LOCALLY, status.getSyncStatus());
	}

	private <T> Collection<T> arrayToCollection(T[] values) {
		List<T> result = new LinkedList<T>();
		for (T v : values) {
			result.add(v);
		}
		return result;
	}

	@Test
	public void testStatus_AddTagsAnnouncedNote() throws Exception {
		testStatus_AnnounceNote();
		Tag[] tags_ = { new Tag("mytag1"), new Tag("mytag2"), new Tag("mytag3") };
		Set<Tag> tags = new HashSet<Tag>(arrayToCollection(tags_));

		pms.setTagsForJakeObject(note, tags);

		Attributed<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_ON_BOTH, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
		assertEqualsNoOrder(tags_, pms.getTagsForJakeObject(note));
	}

	private <T> void assertEqualsNoOrder(T[] expected, Iterable<T> values) {
		List<T> vl = new LinkedList<T>();
		for (T v : values) {
			vl.add(v);
		}
		for (T anExpected : expected) {
			Assert.assertTrue("User " + anExpected + " expected", vl.remove(anExpected));
		}
		Assert
				.assertEquals("no remaining users expected[" + vl.size() + "]: "
						+ (vl.size() > 0 ? "especially not you, " + vl.get(0) : ""), 0,
						vl.size());
	}

	/* test locks */

	public void testLockStatus_Independence(Method m) throws Exception {
		pms.lock(note, "it is mine!!");

		m.invoke(this, (Object[]) null);

		Attributed<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(LockStatus.CLOSED, status.getLockStatus());
		Assert.assertEquals(me, status.getLockLogEntry().getMember());
	}

	public void testUnLockStatus_Independence(Method m) throws Exception {
		pms.unlock(note, "it is not mine!!");

		m.invoke(this, (Object[]) null);

		Attributed<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(LockStatus.OPEN, status.getLockStatus());
		Assert.assertEquals(null, status.getLockLogEntry().getMember());
	}

	public void myteardown() throws Exception {
		pms.deleteProject(project, true);
		super.teardown();
		FSTestCommons.recursiveDelete(new File(".jake"));
		frontend = null;
		sessionId = null;
		pms = null;
		sync = null;
		me = null;
		note = null;
	}

	@Test
	// @Ignore
	// enable this when all above work
	@Prerequisite(checker = AllowSlowChecker.class)
	public void testLockStatus_All_Independence() throws Exception {
		boolean dirty = false;
		for (Method m : this.getClass().getDeclaredMethods()) {
			if (m.getName().startsWith("testStatus_")) {
				log.info("testing independence of " + m.getName());
				if (dirty) {
					myteardown();
					this.setup();
				}
				testLockStatus_Independence(m);
				dirty = true;
			}
		}
	}

	@Test
	// @Ignore
	// enable this when all above work
	@Prerequisite(checker = AllowSlowChecker.class)
	public void testUnLockStatus_All_Independence() throws Exception {
		boolean dirty = false;
		for (Method m : this.getClass().getDeclaredMethods()) {
			if (m.getName().startsWith("testStatus_")) {
				log.info("testing independence of " + m.getName());
				if (dirty) {
					myteardown();
					this.setup();
				}
				testUnLockStatus_Independence(m);
				dirty = true;
			}
		}
	}


}
