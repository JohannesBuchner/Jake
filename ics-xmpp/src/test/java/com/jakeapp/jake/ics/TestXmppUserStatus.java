package com.jakeapp.jake.ics;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.junit.ext.Prerequisite;
import com.googlecode.junit.ext.PrerequisiteAwareClassRunner;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.test.XmppTestEnvironment;

@RunWith(PrerequisiteAwareClassRunner.class)
public class TestXmppUserStatus {

	private static final Logger log = Logger.getLogger(TestXmppUserStatus.class);

	private ICService ics = null;

	private static XmppUserId offlineUserId = new XmppUserId("foo.bar@"
			+ XmppTestEnvironment.getHost());

	private static XmppUserId onlineUserId = new XmppUserId("IhasSses@"
			+ XmppTestEnvironment.getHost());

	private static XmppUserId shortUserid1 = new XmppUserId("foobar@"
			+ XmppTestEnvironment.getHost());

	private static String testUser1Passwd = "testpasswd1";

	private static String testnamespace = "mynamespace";

	private static String testgroupname = "mygroupname";

	@Before
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void setUp() throws Exception {
		// if (TestEnvironment.serverIsAvailable()) {
		XmppTestEnvironment.assureUserIdExistsAndConnect(shortUserid1, testUser1Passwd);
		XmppTestEnvironment.assureUserIdExistsAndConnect(onlineUserId, testUser1Passwd);
		XmppTestEnvironment.assureUserIdExistsAndConnect(offlineUserId, testUser1Passwd);

		this.ics = new XmppICService(testnamespace, testgroupname);
		Assert.assertTrue(ics.getStatusService().login(shortUserid1, testUser1Passwd));
		// }
	}

	@After
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void teardown() throws Exception {
		if (this.ics != null)
			this.ics.getStatusService().logout();
		XmppTestEnvironment.assureUserDeleted(shortUserid1, testUser1Passwd);
		XmppTestEnvironment.assureUserDeleted(onlineUserId, testUser1Passwd);
		XmppTestEnvironment.assureUserDeleted(offlineUserId, testUser1Passwd);
	}

	@Test
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void testIsLoggedInOnOurSelves() throws Exception {
		Assert.assertTrue(ics.getStatusService().isLoggedIn(shortUserid1));
	}

	/*
	 * @Prerequisite(checker = TestEnvironment.class)
	 * 
	 * @Test public void testIsLoggedInOnOtherUsers() throws Exception {
	 * Assert.assertFalse(ics.getStatusService().isLoggedIn(offlineUserId));
	 * Assert.assertTrue(ics.getStatusService().isLoggedIn(onlineUserId)); }
	 */

}
