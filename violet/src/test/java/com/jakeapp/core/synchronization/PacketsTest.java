package com.jakeapp.core.synchronization;

import java.io.IOException;
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
import com.jakeapp.violet.protocol.files.IRequestMarshaller;
import com.jakeapp.violet.protocol.files.RequestFileMessage;
import com.jakeapp.violet.protocol.files.RequestMarshaller;
import com.jakeapp.violet.protocol.msg.ILogEntryMarshaller;
import com.jakeapp.violet.protocol.msg.IMessageMarshaller;
import com.jakeapp.violet.protocol.msg.PokeMessage;
import com.jakeapp.violet.protocol.msg.impl.LogEntryMarshaller;
import com.jakeapp.violet.protocol.msg.impl.MessageMarshaller;

public class PacketsTest {

	private static final String REQUEST_MSG = "00000000-0000-0015-0000-00000000002a.file.00000000-0000-002a-0000-000000000015";

	private IMessageMarshaller mm = new MessageMarshaller();

	private ILogEntryMarshaller lm = new LogEntryMarshaller();

	private IRequestMarshaller rm = new RequestMarshaller();

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
	public void testPoke() throws IOException {
		PokeMessage msg = PokeMessage.createPokeMessage(projectid, userid, le);
		Assert.assertEquals("00000000-0000-0015-0000-00000000002a.",
				mm.serialize(msg));
	}

	@Test
	public void testRequestLogs() {
		RequestFileMessage msg = RequestFileMessage.createRequestLogsMessage(
				projectid, userid);
		Assert.assertEquals("00000000-0000-0015-0000-00000000002a.logs.",
				rm.serialize(msg));
	}

	@Test
	public void testRequestFile() {
		RequestFileMessage msg = RequestFileMessage.createRequestFileMessage(
				projectid, userid, le);
		Assert.assertEquals(REQUEST_MSG, rm.serialize(msg));
	}

	@Test
	public void testDecodeUUIDRequestFile() {
		RequestFileMessage req = rm.decodeRequestFileMessage(REQUEST_MSG,
				userid);
		Assert.assertEquals(le.getId(), UUID.fromString(req.getIdentifier()));
	}

	@Test
	public void testDecodeProjectUUIDRequestFile() {
		RequestFileMessage req = rm.decodeRequestFileMessage(REQUEST_MSG,
				userid);
		Assert.assertEquals(projectid, req.getProjectId());
	}

	@Test
	public void testDecodeUUID_InvalidRequestFile() {
		RequestFileMessage req = rm.decodeRequestFileMessage("blabla", userid);
		Assert.assertNull(req);
	}

}
