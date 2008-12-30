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

import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;

public class TestXmppICUsersService {

	private static final Logger log = Logger
			.getLogger(TestXmppICUsersService.class);

	private ICService ics;

	private XmppICService ics2;

	private static XmppUserId testUser1 = new XmppUserId(TestEnvironment
			.getXmppId("testuser1"));

	private static String testUser1Passwd = "testpasswd1";

	private static XmppUserId testUser2 = new XmppUserId(TestEnvironment
			.getXmppId("testuser2"));

	private static String testUser2Passwd = "testpasswd2";

	private static String testnamespace = "mynamespace";

	private static String testgroupname = "mygroupname";

	@Before
	public void setUp() throws Exception {
		TestEnvironment.assureUserIdExists(testUser1, testUser1Passwd);
		TestEnvironment.assureUserIdExists(testUser2, testUser2Passwd);

		this.ics = new XmppICService(testnamespace, testgroupname);
		Assert.assertTrue(this.ics.getStatusService().login(testUser1,
				testUser1Passwd));
	}

	@After
	public void teardown() throws Exception {
		if (this.ics != null)
			this.ics.getStatusService().logout();
		if (this.ics2 != null)
			this.ics2.getStatusService().logout();
		TestEnvironment.assureUserDeleted(testUser1, testUser1Passwd);
		TestEnvironment.assureUserDeleted(testUser2, testUser2Passwd);
	}

	@Test
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
	public void testIsLoggedInAfterRequest() throws Exception {
		testOnlineStatusListener();
		Assert.assertTrue(this.ics.getStatusService().isLoggedIn(testUser2));
	}

	@Test
	public void testGetUserList_ContainsOtherUser() throws Exception {

		this.ics.getUsersService().addUser(testUser2, "other");

		int i = 0;
		for (UserId u : this.ics.getUsersService().getUsers()) {
			log.debug(u.toString());
			Assert.assertEquals(testUser2, u);
			i++;
		}
		Assert.assertEquals(1, i);
	}

	@Test
	public void testGetUserList_ContainsOtherUsers() throws Exception {

		this.ics.getUsersService().addUser(testUser2, "other");
		XmppUserId randomGuy = new XmppUserId("justmademe@up.to/proveapoint");
		this.ics.getUsersService().addUser(randomGuy, "randomguy");

		int i = 0;
		boolean testUser2found = false;
		boolean randomGuyFound = false;
		for (UserId u : this.ics.getUsersService().getUsers()) {
			log.debug(u.toString());
			if (testUser2.equals(u))
				testUser2found = true;
			if (randomGuy.equals(u))
				randomGuyFound = true;
			i++;
		}
		Assert.assertEquals(2, i);
		Assert.assertTrue(testUser2found);
		Assert.assertTrue(randomGuyFound);
	}

	@Test
	public void testRemoveUserList() throws Exception {

		this.ics.getUsersService().addUser(testUser2, "other");
		XmppUserId randomGuy = new XmppUserId("justmademe@up.to/proveapoint");
		this.ics.getUsersService().addUser(randomGuy, "randomguy");
		this.ics.getUsersService().removeUser(
				new XmppUserId("justmademe@up.to/otherressource"));

		int i = 0;
		boolean testUser2found = false;
		boolean randomGuyFound = false;
		for (UserId u : this.ics.getUsersService().getUsers()) {
			log.debug(u.toString());
			if (testUser2.equals(u))
				testUser2found = true;
			if (randomGuy.equals(u))
				randomGuyFound = true;
			i++;
		}
		Assert.assertEquals(1, i);
		Assert.assertTrue(testUser2found);
		Assert.assertFalse(randomGuyFound);
	}

	private boolean containsExactlyUsers(Iterable<UserId> users,
			UserId[] expected) {
		List<UserId> e = new LinkedList<UserId>();
		for(UserId u : users){
			e.add(u);
		}
		for(int i = 0 ; i<expected.length; i++){
			
		}
	}

	@Test
	public void testRemoveAddUserDoubles() throws Exception {

		this.ics.getUsersService().addUser(testUser2, "other");
		this.ics.getUsersService().addUser(testUser2, "other");
		this.ics.getUsersService().addUser(testUser2, "other");
		XmppUserId randomGuy = new XmppUserId("justmademe@up.to/proveapoint");
		this.ics.getUsersService().addUser(randomGuy, "randomguy");
		this.ics.getUsersService().addUser(randomGuy, "randomguy");
		this.ics.getUsersService().removeUser(
				new XmppUserId("justmademe@up.to/otherressource"));
		this.ics.getUsersService().removeUser(
				new XmppUserId("justmademe@up.to/otherressource"));

		int i = 0;
		boolean testUser2found = false;
		boolean randomGuyFound = false;
		for (UserId u : this.ics.getUsersService().getUsers()) {
			log.debug(u.toString());
			if (testUser2.equals(u))
				testUser2found = true;
			if (randomGuy.equals(u))
				randomGuyFound = true;
			i++;
		}
		Assert.assertEquals(1, i);
		Assert.assertTrue(testUser2found);
		Assert.assertFalse(randomGuyFound);
	}
}
