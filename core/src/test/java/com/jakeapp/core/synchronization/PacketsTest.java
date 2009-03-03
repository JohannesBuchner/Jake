package com.jakeapp.core.synchronization;

import java.io.File;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntrySerializer;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.logentries.JakeObjectNewVersionLogEntry;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.synchronization.helpers.MessageMarshaller;


public class PacketsTest {

	private static final String REQUEST_MSG = "00000000-0000-0015-0000-00000000002a.00000000-0000-002a-0000-000000000015";

	private MessageMarshaller mm = new MessageMarshaller(new LogEntrySerializer());

	private Project project = new Project("foo", new UUID(21, 42), null, new File(
			"fdsafdsafsafsd"));

	private LogEntry<JakeObject> le;

	@Before
	public void setup() {
		FileObject fo = new FileObject(new UUID(12, 34), project, "/my/file");

		le = new JakeObjectNewVersionLogEntry(fo, new UserId(ProtocolType.XMPP, "u@h"),
				null, "bar", true);
		le.setUuid(new UUID(42, 21));
	}

	@Test
	public void testPoke() {
		Assert.assertEquals(
				"<project>00000000-0000-0015-0000-00000000002a</project><poke/>", mm
						.pokeProject(project));
	}

	@Test
	public void testRequestLogs() {
		Assert.assertEquals(
				"<project>00000000-0000-0015-0000-00000000002a</project><requestlogs/>",
				mm.requestLogs(project));
	}

	@Test
	public void testRequestFile() {
		Assert.assertEquals(REQUEST_MSG, mm.requestFile(project, le));
	}

	@Test
	public void testDecodeUUIDRequestFile() {
		Assert.assertEquals(le.getUuid(), mm
				.getLogEntryUUIDFromRequestMessage(REQUEST_MSG));
	}

	@Test
	public void testDecodeProjectUUIDRequestFile() {
		Assert.assertEquals(project.getProjectId(), mm.getProjectUUIDFromRequestMessage(
				REQUEST_MSG).toString());
	}


	@Test
	public void testDecodeUUID_InvalidRequestFile() {
		Assert.assertNull(mm.getLogEntryUUIDFromRequestMessage("blabla"));
	}

	@Test
	public void testDecodeProjectUUID_InvalidRequestFile() {
		Assert.assertNull(mm.getProjectUUIDFromRequestMessage("blabla"));
	}

}
