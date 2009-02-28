package com.jakeapp.core.domain;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;


import java.util.UUID;

import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import junit.framework.Assert;

/**
 * This tests the correct function of the 
 */
//@ContextConfiguration(locations = {})
public class LogEntrySerializerTest {

    @Mock
    IProjectDao projectDao;


    LogEntrySerializer serializer;


    static final Project sampleProject1 = new Project("sampleName1",
                UUID.fromString("9ffbce4c-9352-46bb-a1e2-2547404241e1"), null, null);

    static final UserId sampleUserId1 = new UserId(ProtocolType.XMPP, "domdorn@jabber.fsinf.at");

    static final JakeObject sampleFileObject1 = new FileObject(sampleProject1, "/bla");

    {
            sampleFileObject1.setUuid(UUID.fromString("52b77f5a-b038-4994-bb4b-322920af11fe"));
    }
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);     
//        projectDao = new Mock(IProjectDao.class);
        serializer = new LogEntrySerializer(projectDao);
    }

    @After
    public void tearDown() {
        // Add your code here
    }

    @Test
    public void testProjectLogEntry_existingProject() throws NoSuchProjectException {


        ProjectLogEntry logEntry = new ProjectLogEntry(sampleProject1, sampleUserId1);
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
    public void testProjectLogEntry_projectNotExisting() throws NoSuchProjectException {

        ProjectLogEntry logEntry = new ProjectLogEntry(sampleProject1, sampleUserId1);
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
    public void testFileObjectLogEntry_newVersion() throws NoSuchProjectException {

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
    public void testFileObjectLogEntry_lock() throws NoSuchProjectException {



        JakeObjectLockLogEntry logEntry = new JakeObjectLockLogEntry(sampleFileObject1, sampleUserId1, "some comment", "CRC32CHECKSUM", false);


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
    public void testFileObjectLogEntry_unlock() throws NoSuchProjectException {



        JakeObjectUnlockLogEntry logEntry = new JakeObjectUnlockLogEntry(sampleFileObject1, sampleUserId1, "some comment", "CRC32CHECKSUM", false);


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
    public void testFileObjectLogEntry_delete() throws NoSuchProjectException {
        JakeObjectDeleteLogEntry logEntry = new JakeObjectDeleteLogEntry(sampleFileObject1, sampleUserId1, "some comment", "CRC32CHECKSUM", false);


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
    public void testDeserialize() {
        // Add your code here
    }
}
