package com.jakeapp.jake.ics;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.junit.ext.Prerequisite;
import com.googlecode.junit.ext.PrerequisiteAwareClassRunner;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;
import com.jakeapp.jake.ics.users.IUsersService;

@RunWith(PrerequisiteAwareClassRunner.class)
public class TestXmppICUsersService {

	private static final Logger log = Logger
			.getLogger(TestXmppICUsersService.class);

	private ICService ics;

	private ICService ics2;

	private static XmppUserId testUser1 = new XmppUserId(TestEnvironment
			.getXmppId("testuser1"));

	private static String testUser1Passwd = "testpasswd1";

	private static XmppUserId testUser2 = new XmppUserId(TestEnvironment
			.getXmppId("testuser2"));

	private static String testUser2Passwd = "testpasswd2";

	private static String testnamespace = "mynamespace";

	private static String testgroupname = "mygroupname";

	@Before
	@Prerequisite(checker = TestEnvironment.class)
	public void setUp() throws Exception {
		TestEnvironment.assureUserIdExists(testUser1, testUser1Passwd);
		TestEnvironment.assureUserIdExists(testUser2, testUser2Passwd);

		this.ics = new XmppICService(testnamespace, testgroupname);
		Assert.assertTrue(this.ics.getStatusService().login(testUser1,
				testUser1Passwd));
	}

	@After
	@Prerequisite(checker = TestEnvironment.class)
	public void teardown() throws Exception {
		if (this.ics != null)
			this.ics.getStatusService().logout();
		if (this.ics2 != null)
			this.ics2.getStatusService().logout();
		TestEnvironment.assureUserDeleted(testUser1, testUser1Passwd);
		TestEnvironment.assureUserDeleted(testUser2, testUser2Passwd);
	}

	private void assertContainsExactlyUsers(Iterable<UserId> users,
			UserId[] expected) {
		List<String> ul = new LinkedList<String>();
		for (UserId u : users) {
			ul.add(new XmppUserId(u).getUserIdWithResource());
			log.debug("have " + u);
		}
		for (int i = 0; i < expected.length; i++) {
			log.debug("want "
					+ new XmppUserId(expected[i]).getUserIdWithOutResource());
			Assert.assertTrue("User " + expected[i] + " expected",
					ul.remove(new XmppUserId(expected[i])
							.getUserIdWithOutResource()));
		}
		Assert.assertEquals("no remaining users: "
				+ (ul.size() > 0 ? "especially not you, " + ul.get(0) : ""), 0,
				ul.size());
	}

	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testOnlineStatusListener() throws Exception {
		final Semaphore s = new Semaphore(0);

		this.ics.getUsersService().addUser(testUser2, "other");

		this.ics.getUsersService().registerOnlineStatusListener(
				new IOnlineStatusListener() {

					@Override
					public void onlineStatusChanged(UserId userid) {
						log.debug("onlineStatusChanged: " + userid);
						try {
							log.debug(TestXmppICUsersService.this.ics
									.getStatusService().isLoggedIn(testUser1));
							Assert.assertEquals(testUser2, userid);
							s.release();
						} catch (NoSuchUseridException e) {
							Assert.fail();
						} catch (NotLoggedInException e) {
							Assert.fail();
						} catch (TimeoutException e) {
							Assert.fail();
						} catch (NetworkException e) {
							Assert.fail();
						}
					}
				});
		this.ics2 = new XmppICService(testnamespace, testgroupname);
		Assert.assertTrue(this.ics2.getStatusService().login(testUser2,
				testUser2Passwd));
		this.ics.getUsersService().requestOnlineNotification(testUser2);
		Assert.assertTrue(s.tryAcquire(5, TimeUnit.SECONDS));
	}

	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testIsLoggedInAfterRequestingStatus() throws Exception {
		testOnlineStatusListener();
		Thread.sleep(10);
		Assert.assertTrue(this.ics.getStatusService().isLoggedIn(testUser2));
	}

	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testGetUserList_ContainsOtherUser() throws Exception {

		this.ics.getUsersService().addUser(testUser2, "other");
		
		UserId[] expected = { testUser2 };
		assertContainsExactlyUsers(this.ics.getUsersService().getUsers(),
				expected);
	}

	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testGetUserList_ContainsOtherUsers() throws Exception {

		this.ics.getUsersService().addUser(testUser2, "other");
		XmppUserId randomGuy = new XmppUserId("justmademe@up.to/proveapoint");
		this.ics.getUsersService().addUser(randomGuy, "randomguy");

		UserId[] expected = { randomGuy, testUser2 };
		assertContainsExactlyUsers(this.ics.getUsersService().getUsers(),
				expected);
	}

	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testRemoveUserList() throws Exception {

		this.ics.getUsersService().addUser(testUser2, "other");
		XmppUserId randomGuy = new XmppUserId("justmademe@up.to/proveapoint");
		this.ics.getUsersService().addUser(randomGuy, "randomguy");
		this.ics.getUsersService().removeUser(
				new XmppUserId("justmademe@up.to/otherressource"));

		UserId[] expected = { testUser2 };
		assertContainsExactlyUsers(this.ics.getUsersService().getUsers(),
				expected);
	}

	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testRemoveAddUserDoubles() throws Exception {
		XmppUserId randomGuy = new XmppUserId("justmademe@up.to/proveapoint");
		XmppUserId randomGuyOtherRessource = new XmppUserId(
				"justmademe@up.to/otherressource");
		UserId[] state0 = {};
		UserId[] state1 = { testUser2 };
		UserId[] state2 = { testUser2, randomGuy };
		UserId[] state3 = { testUser2, randomGuy };
		UserId[] state4 = { testUser2 };
		UserId[] state5 = {};
		UserId[] state6 = {};

		IUsersService us = this.ics.getUsersService();

		assertContainsExactlyUsers(us.getUsers(), state0);
		us.addUser(testUser2, "other");
		assertContainsExactlyUsers(us.getUsers(), state1);
		us.addUser(testUser2, "other");
		us.addUser(testUser2, "other");
		assertContainsExactlyUsers(us.getUsers(), state1);
		us.addUser(randomGuy, "randomguy");
		assertContainsExactlyUsers(us.getUsers(), state2);
		us.addUser(randomGuyOtherRessource, "randomguy");
		assertContainsExactlyUsers(us.getUsers(), state3);
		us.removeUser(randomGuy);
		assertContainsExactlyUsers(us.getUsers(), state4);
		us.removeUser(testUser2);
		assertContainsExactlyUsers(us.getUsers(), state5);
		us.removeUser(randomGuy);
		assertContainsExactlyUsers(us.getUsers(), state6);
	}
}
