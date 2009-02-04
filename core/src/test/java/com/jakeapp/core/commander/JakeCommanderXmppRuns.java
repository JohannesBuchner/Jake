package com.jakeapp.core.commander;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.junit.ext.Prerequisite;
import com.googlecode.junit.ext.PrerequisiteAwareClassRunner;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.test.XmppTestEnvironment;


@RunWith(PrerequisiteAwareClassRunner.class)
public class JakeCommanderXmppRuns extends JakeCommanderRuns {

	private static XmppUserId testUser1 = new XmppUserId(XmppTestEnvironment
			.getXmppId("foobar"));

	private static String testUser1Passwd = "testpasswd1";

	protected FifoStreamer fifo;

	@Before
	public void setup() throws Exception {
		super.setup();
		fifo = new FifoStreamer();
		fifo.addLine("coreLogin " + testUser1 + " " + testUser1Passwd);

		XmppTestEnvironment.assureUserExists(XmppTestEnvironment.getHost(),
				testUser1.getUsername(), testUser1Passwd);
	}

	@After
	public void teardown() throws Exception {
		super.teardown();
		XmppTestEnvironment.assureUserDeleted(testUser1, testUser1Passwd);
	}

	public void go() {
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Prerequisite(checker = XmppTestEnvironment.class)
	@Test
	public void testMinimalOnlineRun() {
		// fifo.addLine("newProject " + tmpdir.getAbsolutePath());
		fifo.addLine("login");
		go();
	}

	@Test
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void testMinimalRun() {
		go();
	}
}
