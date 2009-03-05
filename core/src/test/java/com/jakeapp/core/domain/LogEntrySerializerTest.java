package com.jakeapp.core.domain;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;
import org.springframework.test.annotation.ExpectedException;


import java.util.UUID;

import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.dao.INoteObjectDao;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
import com.jakeapp.core.domain.logentries.*;
import com.jakeapp.core.synchronization.exceptions.InvalidDeserializerCallException;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import junit.framework.Assert;

/**
 * This tests the correct function of the
 */
//@ContextConfiguration(locations = {})
public class LogEntrySerializerTest {

	@Mock
	IProjectDao projectDao;
	@Mock
	IFileObjectDao fileObjectDao;

	@Mock
	INoteObjectDao noteObjectDao;


	@Mock
	ProjectApplicationContextFactory applicationContextFactory;

	LogEntrySerializer serializer;


	static final Project sampleProject1 = new Project("sampleProjectName1",
			UUID.fromString("9ffbce4c-9352-46bb-a1e2-2547404241e1"), null, null);

	static final UserId sampleUserId1 = new UserId(ProtocolType.XMPP, "test1@jabber.jakeapp.com");
	static final UserId sampleUserId2 = new UserId(ProtocolType.XMPP, "test2@jabber.jakeapp.com");

	static final FileObject sampleFileObject1 = new FileObject(sampleProject1, "/sampleFileObject1");
	static final NoteObject sampleNoteObject1 = new NoteObject(sampleProject1, "this is simpleNoteObject1 and a \n test");

	{
		sampleFileObject1.setUuid(UUID.fromString("52b77f5a-b038-4994-bb4b-322920af11fe"));
		sampleNoteObject1.setUuid(UUID.fromString("2184126f-5b5a-43cc-a1cf-c2c3ad33be5a"));
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		serializer = new LogEntrySerializer();
		serializer.setProjectDao(projectDao);
		serializer.setFileObjectDao(fileObjectDao);
		serializer.setNoteObjectDao(noteObjectDao);
		serializer.setApplicationContextFactory(applicationContextFactory);
	}

	@After
	public void tearDown() {
		// Add your code here
	}

	@Test
	public void testProjectCreatedLogEntry_existingProject() throws NoSuchProjectException, InvalidTagNameException {


		ProjectCreatedLogEntry logEntry = new ProjectCreatedLogEntry(sampleProject1, sampleUserId1);
		String serializedString = serializer.serialize(logEntry);
		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);

		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof ProjectLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}

	@Test
	public void testDeSerializeProjectCreatedLog() throws NoSuchProjectException, InvalidTagNameException {
		String str =  "XXAAAXXX970eb9a8-5ff2-4ffb-84f2-2d98466c4715XXAAAXXX1235941151365XXAAAXXX0XXAAAXXX0XXAAAXXXjohn@localhostXXAAAXXXa501bc91-a0e4-4b5a-993e-ed13d23ff2fdXXAAAXXXjohnXXAAAXXX";
		sampleProject1.setProjectId("970eb9a8-5ff2-4ffb-84f2-2d98466c4715");
		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);

		LogEntry result = serializer.deserialize(str);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof ProjectLogEntry);

		ProjectLogEntry logEntry = (ProjectLogEntry) result;
		System.out.println("resulting le: " + result.toString());
		Assert.assertEquals(logEntry.getLogAction(), LogAction.PROJECT_CREATED);
		Assert.assertEquals(logEntry.getTimestamp().getTime(), 1235941151365L);
		Assert.assertNull(logEntry.getObjectuuid());
		Assert.assertEquals("970eb9a8-5ff2-4ffb-84f2-2d98466c4715", logEntry.getProject().getProjectId());
		Assert.assertEquals("a501bc91-a0e4-4b5a-993e-ed13d23ff2fd", logEntry.getUuid().toString());

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}

	@Test
	public void testProjectCreatedLogEntry_projectNotExisting() throws NoSuchProjectException, InvalidTagNameException {

		ProjectCreatedLogEntry logEntry = new ProjectCreatedLogEntry(sampleProject1, sampleUserId1);
		String serializedString = serializer.serialize(logEntry);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenThrow(new NoSuchProjectException());

		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof ProjectLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);

	}


	@Test
	public void testFileObjectLogEntry_newVersion() throws NoSuchProjectException, InvalidTagNameException {

		JakeObjectNewVersionLogEntry logEntry = new JakeObjectNewVersionLogEntry(sampleFileObject1, sampleUserId1, "some comment", "CRC32CHECKSUM", false);


		String serializedString = serializer.serialize(logEntry, sampleProject1);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);

		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof JakeObjectNewVersionLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}


	@Test
	public void testFileObjectLogEntry_lock() throws NoSuchProjectException, InvalidTagNameException {


		JakeObjectLockLogEntry logEntry = new JakeObjectLockLogEntry(sampleFileObject1, sampleUserId1, "some comment", false);


		String serializedString = serializer.serialize(logEntry, sampleProject1);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);

		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof JakeObjectLockLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}

	@Test
	public void testFileObjectLogEntry_unlock() throws NoSuchProjectException, InvalidTagNameException {


		JakeObjectUnlockLogEntry logEntry = new JakeObjectUnlockLogEntry(sampleFileObject1, sampleUserId1, "some comment", false);


		String serializedString = serializer.serialize(logEntry, sampleProject1);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);

		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof JakeObjectUnlockLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}


	@Test
	public void testFileObjectLogEntry_delete() throws NoSuchProjectException, InvalidTagNameException {
		JakeObjectDeleteLogEntry logEntry = new JakeObjectDeleteLogEntry(sampleFileObject1, sampleUserId1, "some comment", false);


		String serializedString = serializer.serialize(logEntry, sampleProject1);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);

		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof JakeObjectDeleteLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}

	@Test
	public void testTagAddLogEntry_fileObject() throws InvalidTagNameException, NoSuchProjectException, NoSuchJakeObjectException {
		Tag sampleTag = new Tag("test");
		sampleTag.setObject(sampleFileObject1);

		TagAddLogEntry logEntry = new TagAddLogEntry(sampleTag, sampleUserId1);

		String serializedString = serializer.serialize(logEntry, sampleProject1);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);

		when(applicationContextFactory.getFileObjectDao(sampleProject1)).thenReturn(fileObjectDao);

		when(fileObjectDao.get(sampleFileObject1.getUuid())).thenReturn(sampleFileObject1);

		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof TagAddLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}

	@Test
	public void testTagAddLogEntry_noteObject() throws InvalidTagNameException, NoSuchProjectException, NoSuchJakeObjectException {
		Tag sampleTag = new Tag("test");
		sampleTag.setObject(sampleNoteObject1);

		TagAddLogEntry logEntry = new TagAddLogEntry(sampleTag, sampleUserId1);

		String serializedString = serializer.serialize(logEntry, sampleProject1);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);
		when(applicationContextFactory.getNoteObjectDao(sampleProject1)).thenReturn(noteObjectDao);
		when(noteObjectDao.get(sampleNoteObject1.getUuid())).thenReturn(sampleNoteObject1);

		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof TagAddLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}


	@Test
	public void testTagRemoveLogEntry_fileObject() throws InvalidTagNameException, NoSuchProjectException, NoSuchJakeObjectException {
		Tag sampleTag = new Tag("test");
		sampleTag.setObject(sampleFileObject1);

		TagRemoveLogEntry logEntry = new TagRemoveLogEntry(sampleTag, sampleUserId1);

		String serializedString = serializer.serialize(logEntry, sampleProject1);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);
		when(applicationContextFactory.getFileObjectDao(sampleProject1)).thenReturn(fileObjectDao);
		when(fileObjectDao.get(sampleFileObject1.getUuid())).thenReturn(sampleFileObject1);

		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof TagRemoveLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}


	@Test
	public void testTagRemoveLogEntry_noteObject() throws InvalidTagNameException, NoSuchProjectException, NoSuchJakeObjectException {
		Tag sampleTag = new Tag("test");
		sampleTag.setObject(sampleNoteObject1);

		TagRemoveLogEntry logEntry = new TagRemoveLogEntry(sampleTag, sampleUserId1);

		String serializedString = serializer.serialize(logEntry, sampleProject1);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);
		when(applicationContextFactory.getNoteObjectDao(sampleProject1)).thenReturn(noteObjectDao);
		when(noteObjectDao.get(sampleNoteObject1.getUuid())).thenReturn(sampleNoteObject1);

		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof TagRemoveLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}


	@Test
	public void testProjectCreatedLogEntry_ProjectDoesNotExist() throws NoSuchProjectException {
		ProjectCreatedLogEntry logEntry = new ProjectCreatedLogEntry(sampleProject1, sampleUserId1);

		String serializedString = serializer.serialize(logEntry);
		System.out.println("serializedString = " + serializedString);
		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenThrow(new NoSuchProjectException());


		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof ProjectCreatedLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}

	@Test
	public void testProjectCreatedLogEntry_ProjectAlreadyExists() throws NoSuchProjectException {
		ProjectCreatedLogEntry logEntry = new ProjectCreatedLogEntry(sampleProject1, sampleUserId1);

		String serializedString = serializer.serialize(logEntry);
		System.out.println("serializedString = " + serializedString);
		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);


		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof ProjectCreatedLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}

	@Test
	public void testProjectJoinedLogEntry_ProjectDoesNotExist() throws NoSuchProjectException {
		ProjectCreatedLogEntry logEntry = new ProjectCreatedLogEntry(sampleProject1, sampleUserId1);

		String serializedString = serializer.serialize(logEntry);
		System.out.println("serializedString = " + serializedString);
		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenThrow(new NoSuchProjectException());


		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof ProjectCreatedLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}

	@Test
	public void testProjectJoinedLogEntry_ProjectAlreadyExists() throws NoSuchProjectException {
		ProjectJoinedLogEntry logEntry = new ProjectJoinedLogEntry(sampleProject1, sampleUserId1);

		String serializedString = serializer.serialize(logEntry);
		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);


		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof ProjectJoinedLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}

	@Test
	public void testStartTrustingProjectMemberLogEntry_existingProject() throws NoSuchProjectException {

		StartTrustingProjectMemberLogEntry logEntry = new StartTrustingProjectMemberLogEntry(sampleUserId1, sampleUserId2);

		String serializedString = serializer.serialize(logEntry, sampleProject1);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);

		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof StartTrustingProjectMemberLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}

	@Test(expected = InvalidDeserializerCallException.class)
	public void testStartTrustingProjectMemberLogEntry_nonExistingProject() throws NoSuchProjectException {
		StartTrustingProjectMemberLogEntry logEntry = new StartTrustingProjectMemberLogEntry(sampleUserId1, sampleUserId2);

		String serializedString = serializer.serialize(logEntry, sampleProject1);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenThrow(new NoSuchProjectException());

		serializer.deserialize(serializedString);
		Assert.fail(); // exception should be thrown
	}

	@Test
	public void testStopTrustingProjectMemberLogEntry_existingProject() throws NoSuchProjectException {

		StopTrustingProjectMemberLogEntry logEntry = new StopTrustingProjectMemberLogEntry(sampleUserId1, sampleUserId2);

		String serializedString = serializer.serialize(logEntry, sampleProject1);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);

		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof StopTrustingProjectMemberLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}

	@Test(expected = InvalidDeserializerCallException.class)
	public void testStopTrustingProjectMemberLogEntry_nonExistingProject() throws NoSuchProjectException {
		StopTrustingProjectMemberLogEntry logEntry = new StopTrustingProjectMemberLogEntry(sampleUserId1, sampleUserId2);

		String serializedString = serializer.serialize(logEntry, sampleProject1);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenThrow(new NoSuchProjectException());

		serializer.deserialize(serializedString);
		Assert.fail(); // exception should be thrown
	}

	@Test
	public void testFollowTrustingProjectMemberLogEntry_existingProject() throws NoSuchProjectException {

		FollowTrustingProjectMemberLogEntry logEntry = new FollowTrustingProjectMemberLogEntry(sampleUserId1, sampleUserId2);

		String serializedString = serializer.serialize(logEntry, sampleProject1);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenReturn(sampleProject1);

		LogEntry result = serializer.deserialize(serializedString);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof FollowTrustingProjectMemberLogEntry);

		Assert.assertTrue(logEntry.equals(result));
		Assert.assertTrue(result.equals(logEntry));

		Assert.assertEquals(logEntry.hashCode(), result.hashCode());
		Assert.assertEquals(logEntry, result);
	}

	@Test(expected = InvalidDeserializerCallException.class)
	public void testFollowTrustingProjectMemberLogEntry_nonExistingProject() throws NoSuchProjectException {
		FollowTrustingProjectMemberLogEntry logEntry = new FollowTrustingProjectMemberLogEntry(sampleUserId1, sampleUserId2);

		String serializedString = serializer.serialize(logEntry, sampleProject1);

		when(projectDao.read(UUID.fromString(sampleProject1.getProjectId()))).thenThrow(new NoSuchProjectException());

		serializer.deserialize(serializedString);
		Assert.fail(); // exception should be thrown
	}


	@Test
	public void testDeserialize() {
		// Add your code here
	}
}
