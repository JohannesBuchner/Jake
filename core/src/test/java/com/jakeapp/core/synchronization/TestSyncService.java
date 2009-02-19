package com.jakeapp.core.synchronization;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.jake.test.FSTestCommons;
import com.jakeapp.jake.test.TmpdirEnabledTestCase;


public class TestSyncService extends TmpdirEnabledTestCase {

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

	private ProjectMember me;

	@Override
	@Before
	public void setup() throws Exception {
		super.setup();

		FSTestCommons.recursiveDelete(new File(".jake"));
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "/com/jakeapp/core/applicationContext.xml" });
		frontend = (IFrontendService) applicationContext.getBean("frontendService");

		sessionId = frontend.authenticate(new HashMap<String, String>());
		pms = frontend.getProjectsManagingService(sessionId);
		sync = frontend.getSyncService(sessionId);
		db = (ProjectApplicationContextFactory) applicationContext
				.getBean("applicationContextFactory");
		createProjectWorkaround();
		me = pms.getProjectMembers(project).get(0);
		note = new NoteObject(project, ORIGINAL_CONTENT);
	}

	public void createProjectWorkaround() throws Exception {
		ServiceCredentials cred = new ServiceCredentials(id, password);
		cred.setProtocol(ProtocolType.XMPP);
		MsgService msg = frontend.addAccount(sessionId, cred);

		project = pms.createProject(tmpdir.getName(), tmpdir.getAbsolutePath(), msg);
		try {
			pms.assignUserToProject(project, msg.getUserId());
		} catch (IllegalAccessException e) {
			// we ignore that, just like the gui does
		}

		Assert.assertNotNull(project.getMessageService());
		Assert.assertNotNull(project.getUserId());

		Assert.assertEquals(1, pms.getProjectMembers(project).size());
		Assert.assertEquals(project.getUserId().getUuid(), pms.getProjectMembers(project)
				.get(0).getUserId());

		Assert.assertEquals(msg.getUserId(), project.getUserId());
	}


	@Test
	public void testStatus_NonExistantNote() throws Exception {
		AttributedJakeObject<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.NON_EXISTANT, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
	}

	@Test
	public void testStatus_NewNote() throws Exception {
		pms.saveNote(note);
		Assert.assertNotNull(note.getUuid());
		AttributedJakeObject<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_LOCAL, status.getExistence());
		Assert.assertEquals(SyncStatus.MODIFIED_LOCALLY, status.getSyncStatus());
	}


	@Test
	public void testStatus_AnnounceNote() throws Exception {
		testStatus_NewNote();
		sync.announce(note, LogAction.JAKE_OBJECT_NEW_VERSION, "done");

		AttributedJakeObject<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_ON_BOTH, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
	}


	@Test
	public void testStatus_ModifyAnnouncedNote() throws Exception {
		testStatus_AnnounceNote();
		note.setContent("my new content");
		pms.saveNote(note);

		AttributedJakeObject<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_ON_BOTH, status.getExistence());
		Assert.assertEquals(SyncStatus.MODIFIED_LOCALLY, status.getSyncStatus());
	}


	@Test
	public void testStatus_PullModifyNote() throws Exception {
		testStatus_ModifyAnnouncedNote();
		sync.pullObject(note);
		Assert.assertEquals(ORIGINAL_CONTENT, note.getContent());

		AttributedJakeObject<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_ON_BOTH, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
	}


	@Test
	public void testStatus_DeleteNewNote() throws Exception {
		testStatus_NewNote();
		pms.deleteNote(note);

		AttributedJakeObject<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.NON_EXISTANT, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
	}

	@Test
	public void testStatus_DeleteAnnouncedNote() throws Exception {
		testStatus_AnnounceNote();
		pms.deleteNote(note);

		AttributedJakeObject<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_REMOTE, status.getExistence());
		Assert.assertEquals(SyncStatus.MODIFIED_LOCALLY, status.getSyncStatus());
	}

	@Test
	public void testStatus_AddTagAnnouncedNote() throws Exception {
		testStatus_AnnounceNote();
		sync.announce(note, LogAction.TAG_ADD, "mytag1");

		AttributedJakeObject<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_ON_BOTH, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
		Assert.assertTrue(pms.getTagsForJakeObject(note).contains(new Tag("mytag1")));
		Assert.assertEquals(1, pms.getTagsForJakeObject(note).size());
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

		AttributedJakeObject<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_ON_BOTH, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
		Assert.assertEquals(tags, pms.getTagsForJakeObject(note));
	}

	@Test
	public void testStatus_RemoveTagAnnouncedNote() throws Exception {
		testStatus_AddTagsAnnouncedNote();
		sync.announce(note, LogAction.TAG_REMOVE, "mytag1");

		AttributedJakeObject<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_ON_BOTH, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
		Assert.assertTrue(pms.getTagsForJakeObject(note).contains(new Tag("mytag3")));
		Assert.assertTrue(pms.getTagsForJakeObject(note).contains(new Tag("mytag2")));
		Assert.assertFalse(pms.getTagsForJakeObject(note).contains(new Tag("mytag1")));
		Assert.assertEquals(2, pms.getTagsForJakeObject(note).size());
	}

	/* test locks */

	public void testLockStatus_Independence(Method m) throws Exception {
		sync.announce(note, LogAction.JAKE_OBJECT_LOCK, "it is mine!!");

		m.invoke(this, (Object[]) null);

		AttributedJakeObject<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(LockStatus.CLOSED, status.getLockStatus());
		Assert.assertEquals(me, status.getLockOwner());
	}

	public void testUnLockStatus_Independence(Method m) throws Exception {
		sync.announce(note, LogAction.JAKE_OBJECT_LOCK, "it is mine!!");

		m.invoke(this, (Object[]) null);

		AttributedJakeObject<NoteObject> status = sync.getJakeObjectSyncStatus(note);

		Assert.assertEquals(note, status.getJakeObject());
		Assert.assertEquals(LockStatus.CLOSED, status.getLockStatus());
		Assert.assertEquals(me, status.getLockOwner());
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
	@Ignore
	// enable this when all above work
	public void testLockStatus_All_Independence() throws Exception {
		boolean dirty = false;
		for (Method m : this.getClass().getDeclaredMethods()) {
			if (m.getName().startsWith("testStatus_")) {
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
	@Ignore
	// enable this when all above work
	public void testUnLockStatus_All_Independence() throws Exception {
		boolean dirty = false;
		for (Method m : this.getClass().getDeclaredMethods()) {
			if (m.getName().startsWith("testStatus_")) {
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
