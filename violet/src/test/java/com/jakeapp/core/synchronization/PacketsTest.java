package com.jakeapp.core.synchronization;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.PokeMessage;
import com.jakeapp.violet.protocol.RequestFileMessage;
import com.jakeapp.violet.protocol.RequestLogsMessage;
import com.jakeapp.violet.synchronization.request.MessageMarshaller;

public class PacketsTest {

	private static final String REQUEST_MSG = "00000000-0000-0015-0000-00000000002a.00000000-0000-002a-0000-000000000015";

	private MessageMarshaller mm = new MessageMarshaller();

	private UUID projectid = new UUID(21, 42);
	private UUID leid = new UUID(42, 21);
	private Timestamp time = new Timestamp(new Date().getTime());
	private User user = new User("me@localhost");
	private String commitmsg = "fdsafdsafsafsd";
	private String hash = "1234124125124313123124125124";
	private JakeObject fo = new JakeObject("/my/file");

	private LogEntry le;

	private UserId userid;

	@Before
	public void setup() {
		le = new LogEntry(leid, time, user, fo, commitmsg, hash, true);
		userid = DI.getUserId(user.getUserId());
	}

	@Test
	public void testPoke() {
		PokeMessage msg = new PokeMessage();
		msg.setProjectId(projectid);
		msg.setUser(userid);

		Assert.assertEquals(
				"<project>00000000-0000-0015-0000-00000000002a</project><poke/>",
				mm.serialize(msg));
	}

	@Test
	public void testRequestLogs() {
		RequestLogsMessage msg = new RequestLogsMessage();
		msg.setProjectId(projectid);
		msg.setUser(userid);
		Assert.assertEquals(
				"<project>00000000-0000-0015-0000-00000000002a</project><requestlogs/>",
				mm.serialize(msg));
	}

	@Test
	public void testRequestFile() {
		RequestFileMessage msg = new RequestFileMessage();
		msg.setProjectId(projectid);
		msg.setUser(userid);
		msg.setLogEntry(le);
		Assert.assertEquals(REQUEST_MSG, mm.serialize(msg));
	}

	@Test
	public void testDecodeUUIDRequestFile() {
		Assert.assertEquals(le.getId(),
				mm.getLogEntryUUIDFromRequestMessage(REQUEST_MSG));
	}

	@Test
	public void testDecodeProjectUUIDRequestFile() {
		Assert.assertEquals(projectid,
				mm.getProjectUUIDFromRequestMessage(REQUEST_MSG));
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
