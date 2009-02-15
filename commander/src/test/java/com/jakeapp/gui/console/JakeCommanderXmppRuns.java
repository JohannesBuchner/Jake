package com.jakeapp.gui.console;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.junit.ext.Prerequisite;
import com.googlecode.junit.ext.PrerequisiteAwareClassRunner;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.test.FSTestCommons;
import com.jakeapp.jake.test.TmpdirEnabledTestCase;
import com.jakeapp.jake.test.XmppTestEnvironment;
import com.jakeapp.gui.console.JakeCommander;


@RunWith(PrerequisiteAwareClassRunner.class)
public class JakeCommanderXmppRuns extends TmpdirEnabledTestCase {

	private static XmppUserId testUser1 = new XmppUserId(XmppTestEnvironment.getXmppId("testuser1"));

	private static String testUser1Passwd = "testpasswd1";

	protected FifoStreamer fifo;

	@Override
	@Before
	public void setup() throws Exception {
		super.setup();
		FSTestCommons.recursiveDelete(new File(".jake"));
		fifo = new FifoStreamer();
		fifo.addLine("coreLogin " + testUser1 + " " + testUser1Passwd);

		XmppTestEnvironment.assureUserExists(XmppTestEnvironment.getHost(),
				testUser1.getUsername(), testUser1Passwd);
	}

	@Override
	@After
	public void teardown() throws Exception {
		super.teardown();
		XmppTestEnvironment.assureUserDeleted(testUser1, testUser1Passwd);
	}

	public void go() {
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Test
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void testMinimalRun() {
		go();
	}

	@Prerequisite(checker = XmppTestEnvironment.class)
	@Test
	public void testMinimalOnlineRun() {
		// fifo.addLine("newProject " + tmpdir.getAbsolutePath());
		fifo.addLine("login");
		go();
	}
}
