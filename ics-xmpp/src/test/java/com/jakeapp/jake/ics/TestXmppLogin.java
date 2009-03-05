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
import com.jakeapp.jake.test.XmppTestEnvironment;

@RunWith(PrerequisiteAwareClassRunner.class)
public class TestXmppLogin {

	private static final Logger log = Logger.getLogger(TestXmppLogin.class);

	private ICService ics = null;

	private static XmppUserId wrongUserid1 = new XmppUserId("foo.bar");

	private static XmppUserId offlineUserId = new XmppUserId("foo.bar@"
			+ XmppTestEnvironment.getHost());

	private static XmppUserId onlineUserId = new XmppUserId("IhasSses@"
			+ XmppTestEnvironment.getHost());

	private static XmppUserId shortUserid1 = new XmppUserId("foobar@"
			+ XmppTestEnvironment.getHost());

	private static String somePassword = "bar";

	private static String testUser1Passwd = "testpasswd1";

	private static String testnamespace = "mynamespace";

	private static String testgroupname = "mygroupname";

	@Before
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void setUp() throws Exception {
		XmppTestEnvironment.assureUserIdExistsAndConnect(shortUserid1,
				testUser1Passwd);
		XmppTestEnvironment.assureUserIdExistsAndConnect(onlineUserId,
				testUser1Passwd);
		XmppTestEnvironment.assureUserIdExistsAndConnect(offlineUserId,
				testUser1Passwd);

		this.ics = new XmppICService(testnamespace, testgroupname);
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

	/* we use firstname.lastname@host or nick@host notation for the Mock */
	@Test(expected = NoSuchUseridException.class)
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void testGetFirstNameThrowsNoSuchUseridException() throws Exception {
		ics.getStatusService().getFirstname(wrongUserid1);
	}
	@Test(expected = NoSuchUseridException.class)
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void testGetLastNameThrowsNoSuchUseridException() throws Exception {
		ics.getStatusService().getLastname(wrongUserid1);
	}
	@Test
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void testGetFirstName() throws Exception {
		Assert.assertEquals("foo", ics.getStatusService().getFirstname(
				offlineUserId));
	}
	@Test
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void testGetLastName() throws Exception {
		Assert.assertEquals("bar", ics.getStatusService().getLastname(
				offlineUserId));
	}

	@Test
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void testGetNames() throws Exception {
		Assert.assertEquals("", ics.getStatusService().getFirstname(
				shortUserid1));
		Assert.assertEquals("", ics.getStatusService()
				.getLastname(shortUserid1));
	}

	@Test
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void testLoginAndIsLoggedIn() throws Exception {
		Assert.assertFalse(ics.getStatusService().isLoggedIn());
		try {
			ics.getStatusService().isLoggedIn(shortUserid1);
			Assert.fail();
		} catch (NotLoggedInException e) {
		}

		try {
			ics.getStatusService().login(wrongUserid1, somePassword, null, 0);
			Assert.fail();
		} catch (NoSuchUseridException e) {
		}
		ics.getStatusService().login(shortUserid1,
				testUser1Passwd, null, 0);
		Assert.assertTrue(ics.getStatusService().isLoggedIn());
		ics.getStatusService().logout();
		Assert.assertFalse(ics.getStatusService().isLoggedIn());

		ics.getStatusService().login(offlineUserId,
				testUser1Passwd, null, 0);
		ics.getStatusService().logout();
	}
}
