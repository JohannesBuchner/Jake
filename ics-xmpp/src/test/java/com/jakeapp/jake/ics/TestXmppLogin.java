package com.jakeapp.jake.ics;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.junit.ext.Prerequisite;
import com.googlecode.junit.ext.PrerequisiteAwareClassRunner;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

@RunWith(PrerequisiteAwareClassRunner.class)
public class TestXmppLogin {

	private static final Logger log = Logger.getLogger(TestXmppLogin.class);

	private ICService ics = null;

	private static XmppUserId wrongUserid1 = new XmppUserId("foo.bar");

	private static XmppUserId offlineUserId = new XmppUserId("foo.bar@"
			+ TestEnvironment.getHost());

	private static XmppUserId onlineUserId = new XmppUserId("IhasSses@"
			+ TestEnvironment.getHost());

	private static XmppUserId shortUserid1 = new XmppUserId("foobar@"
			+ TestEnvironment.getHost());

	private static String somePassword = "bar";

	private static String testUser1Passwd = "testpasswd1";

	private static String testnamespace = "mynamespace";

	private static String testgroupname = "mygroupname";

	@Before
	@Prerequisite(checker = TestEnvironment.class)
	public void setUp() throws Exception {
		TestEnvironment.assureUserIdExistsAndConnect(shortUserid1,
				testUser1Passwd);
		TestEnvironment.assureUserIdExistsAndConnect(onlineUserId,
				testUser1Passwd);
		TestEnvironment.assureUserIdExistsAndConnect(offlineUserId,
				testUser1Passwd);

		this.ics = new XmppICService(testnamespace, testgroupname);
	}

	@After
	@Prerequisite(checker = TestEnvironment.class)
	public void teardown() throws Exception {
		if (this.ics != null)
			this.ics.getStatusService().logout();
		TestEnvironment.assureUserDeleted(shortUserid1, testUser1Passwd);
		TestEnvironment.assureUserDeleted(onlineUserId, testUser1Passwd);
		TestEnvironment.assureUserDeleted(offlineUserId, testUser1Passwd);
	}

	/* we use firstname.lastname@host or nick@host notation for the Mock */
	@Test(expected = NoSuchUseridException.class)
	@Prerequisite(checker = TestEnvironment.class)
	public void testGetFirstNameThrowsNoSuchUseridException() throws Exception {
		ics.getStatusService().getFirstname(wrongUserid1);
	}
	@Test(expected = NoSuchUseridException.class)
	@Prerequisite(checker = TestEnvironment.class)
	public void testGetLastNameThrowsNoSuchUseridException() throws Exception {
		ics.getStatusService().getLastname(wrongUserid1);
	}
	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testGetFirstName() throws Exception {
		Assert.assertEquals("foo", ics.getStatusService().getFirstname(
				offlineUserId));
	}
	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testGetLastName() throws Exception {
		Assert.assertEquals("bar", ics.getStatusService().getLastname(
				offlineUserId));
	}

	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testGetNames() throws Exception {
		Assert.assertEquals("", ics.getStatusService().getFirstname(
				shortUserid1));
		Assert.assertEquals("", ics.getStatusService()
				.getLastname(shortUserid1));
	}

	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testLoginAndIsLoggedIn() throws Exception {
		Assert.assertFalse(ics.getStatusService().isLoggedIn());
		try {
			ics.getStatusService().isLoggedIn(shortUserid1);
			Assert.fail();
		} catch (NotLoggedInException e) {
		}

		try {
			ics.getStatusService().login(wrongUserid1, somePassword);
			Assert.fail();
		} catch (NoSuchUseridException e) {
		}
		Assert.assertTrue(ics.getStatusService().login(shortUserid1,
				testUser1Passwd));
		Assert.assertTrue(ics.getStatusService().isLoggedIn());
		ics.getStatusService().logout();
		Assert.assertFalse(ics.getStatusService().isLoggedIn());

		Assert.assertTrue(ics.getStatusService().login(offlineUserId,
				testUser1Passwd));
		ics.getStatusService().logout();
	}
}
