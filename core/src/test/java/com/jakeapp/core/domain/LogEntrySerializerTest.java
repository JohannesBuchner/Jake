package com.jakeapp.core.domain;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;


import java.util.UUID;
import java.io.File;

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
    public void testProjectLogEntry() throws NoSuchProjectException {


        ProjectLogEntry logEntry = new ProjectLogEntry(sampleProject1, sampleUserId1);
        String serializedString = serializer.serialize(logEntry);
        
        System.out.println("serializedString = " + serializedString);

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
    public void testDeserialize() {
        // Add your code here
    }
}
