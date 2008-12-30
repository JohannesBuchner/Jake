package com.jakeapp.jake.ics;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

public class TestXmppUserStatus {

	private static final Logger log = Logger
			.getLogger(TestXmppUserStatus.class);

	private ICService ics = null;

	private static XmppUserId offlineUserId = new XmppUserId("foo.bar@"
			+ TestEnvironment.host);

	private static XmppUserId onlineUserId = new XmppUserId("IhasSses@"
			+ TestEnvironment.host);

	private static XmppUserId shortUserid1 = new XmppUserId("foobar@"
			+ TestEnvironment.host);

	private static String testUser1Passwd = "testpasswd1";

	private static String testnamespace = "mynamespace";

	private static String testgroupname = "mygroupname";

	@Before
	public void setUp() throws Exception {
		TestEnvironment.assureUserIdExistsAndConnect(shortUserid1,
				testUser1Passwd);
		TestEnvironment.assureUserIdExistsAndConnect(onlineUserId,
				testUser1Passwd);
		TestEnvironment.assureUserIdExistsAndConnect(offlineUserId,
				testUser1Passwd);

		this.ics = new XmppICService(testnamespace, testgroupname);
		Assert.assertTrue(ics.getStatusService().login(shortUserid1,
				testUser1Passwd));
	}

	@After
	public void teardown() throws Exception {
		if (this.ics != null)
			this.ics.getStatusService().logout();
		TestEnvironment.assureUserDeleted(shortUserid1, testUser1Passwd);
		TestEnvironment.assureUserDeleted(onlineUserId, testUser1Passwd);
		TestEnvironment.assureUserDeleted(offlineUserId, testUser1Passwd);
	}

	@Test
	public void testIsLoggedInOnOurSelves() throws Exception {
		Assert.assertTrue(ics.getStatusService().isLoggedIn(shortUserid1));
	}
	
	/*
	@Test
	public void testIsLoggedInOnOtherUsers() throws Exception {
		Assert.assertFalse(ics.getStatusService().isLoggedIn(offlineUserId));
		Assert.assertTrue(ics.getStatusService().isLoggedIn(onlineUserId));
	}*/

}
