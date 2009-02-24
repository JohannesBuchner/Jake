package com.jakeapp.gui.console;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.junit.ext.Prerequisite;
import com.googlecode.junit.ext.PrerequisiteAwareClassRunner;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.test.FSTestCommons;
import com.jakeapp.jake.test.TestDBEnabledTestCase;
import com.jakeapp.jake.test.XmppTestEnvironment;


@RunWith(PrerequisiteAwareClassRunner.class)
public class JakeCommanderXmppRuns extends TestDBEnabledTestCase {

	private static XmppUserId testUser1 = new XmppUserId(XmppTestEnvironment
			.getXmppId("testuser1"));

	private static String testUser1Passwd = "testpasswd1";

	private static final String project = "testproject1";

	@Override
	protected String getDbTemplateName() {
		return "oneuser";
	}


	protected FifoStreamer fifo;

	@Override
	@Before
	public void setup() throws Exception {
		super.setup();
		FSTestCommons.recursiveDelete(new File(".jake"));
		
		String pwd = new File(".").getAbsolutePath();
		System.out.println("You are now in " + pwd);
		File projectdir = new File(pwd, project);
		projectdir.mkdir();
		Assert.assertTrue(folderExists(projectdir));
		fifo = new FifoStreamer();
		fifo.addLine("coreLogin " + testUser1 + " " + testUser1Passwd);
		fifo.addLine("openProject " + project);

		XmppTestEnvironment.assureUserExists(XmppTestEnvironment.getHost(), testUser1
				.getUsername(), testUser1Passwd);
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

	@Prerequisite(checker = XmppTestEnvironment.class)
	@Test
	public void testWithProjectOnlineRun() {
		fifo.addLine("openProject " + project);
		fifo.addLine("startProject " + project);
		fifo.addLine("login");
		go();
	}


	@Test
	public void logout() {
		fifo.addLine("logout");
		go();
	}

	@Prerequisite(checker = XmppTestEnvironment.class)
	@Test
	public void loginlogout() {
		fifo.addLine("login");
		fifo.addLine("logout");
		go();
	}
}
