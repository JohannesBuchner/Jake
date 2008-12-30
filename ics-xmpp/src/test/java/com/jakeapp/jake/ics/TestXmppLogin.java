package com.jakeapp.jake.ics;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.impl.mock.MockUserId;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IObjectReceiveListener;

public class TestXmppLogin {

	private static final Logger log = Logger.getLogger(TestXmppLogin.class);

	private ICService ics = null;

	private static XmppUserId wrongUserid1 = new XmppUserId("foo.bar");

	private static XmppUserId offlineUserId = new XmppUserId("foo.bar@"
			+ TestEnvironment.host);

	private static XmppUserId onlineUserId = new XmppUserId("IhasSses@"
			+ TestEnvironment.host);

	private static XmppUserId shortUserid1 = new XmppUserId("foobar@"
			+ TestEnvironment.host);

	private static String somePassword = "bar";

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
	}

	@After
	public void teardown() throws Exception {
		if (this.ics != null)
			this.ics.getStatusService().logout();
		TestEnvironment.assureUserDeleted(shortUserid1, testUser1Passwd);
		TestEnvironment.assureUserDeleted(onlineUserId, testUser1Passwd);
		TestEnvironment.assureUserDeleted(offlineUserId, testUser1Passwd);
	}

	/* we use firstname.lastname@host or nick@host notation for the Mock */
	@Test(expected = NoSuchUseridException.class)
	public void testGetFirstNameThrowsNoSuchUseridException() throws Exception {
		ics.getStatusService().getFirstname(wrongUserid1);
	}
	@Test(expected = NoSuchUseridException.class)
	public void testGetLastNameThrowsNoSuchUseridException() throws Exception {
		ics.getStatusService().getLastname(wrongUserid1);
	}
	@Test
	public void testGetFirstName() throws Exception {
		Assert.assertEquals("foo", ics.getStatusService().getFirstname(
				offlineUserId));
	}
	@Test
	public void testGetLastName() throws Exception {
		Assert.assertEquals("bar", ics.getStatusService().getLastname(
				offlineUserId));
	}

	@Test
	public void testGetNames() throws Exception {
		Assert.assertEquals("", ics.getStatusService().getFirstname(
				shortUserid1));
		Assert.assertEquals("", ics.getStatusService()
				.getLastname(shortUserid1));
	}

	@Test
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

	private Boolean messageSaysOk = false;

	private Boolean objectSaysOk = false;

	public void testReceiveSend() throws Exception {
		messageSaysOk = false;
		objectSaysOk = false;

		IMessageReceiveListener mymsglistener = new IMessageReceiveListener() {

			private int i = 0;

			public void receivedMessage(UserId from_userid, String content) {
				i++;
				if (i == 1) {
					Assert.assertEquals(shortUserid1, from_userid.getUserId());
					Assert.assertEquals("hello I", content);
				} else if (i == 2) {
					Assert.assertEquals("bar@host", from_userid.getUserId());
					Assert.assertEquals("hello you! to you too", content);
				} else if (i == 3) {
					Assert.assertEquals("baz@host", from_userid.getUserId());
					Assert.assertEquals("What's up? to you too", content);
					messageSaysOk = true;
				} else {
					Assert.fail();
				}
			}
		};
		IObjectReceiveListener myobjlistener = new IObjectReceiveListener() {

			private int i = 0;

			public void receivedObject(UserId from_userid, String identifier,
					byte[] content) {
				i++;
				if (i == 1) {
					Assert.assertEquals(shortUserid1, from_userid.getUserId());
					Assert.assertEquals("42:12", identifier);
					Assert
							.assertEquals(new String(new byte[] { 12, 32, 12,
									34 }), new String(content));
				}/*
				 * commented since we don't reuse sendObject in the
				 * MockImplementation of sendMessage else if(i<=3){
				 * assertEquals(identifier, "message"); }
				 */

				else {
					Assert.fail();
				}
				objectSaysOk = (i == 1);
			}
		};

		ics.getMsgService().registerReceiveMessageListener(mymsglistener);
		ics.getMsgService().registerReceiveObjectListener(myobjlistener);
		Assert.assertTrue(ics.getStatusService().login(shortUserid1,
				testUser1Passwd));
		ics.getMsgService().sendObject(shortUserid1, "42:12",
				new byte[] { 12, 32, 12, 34 });
		ics.getMsgService().sendMessage(shortUserid1, "hello I");
		ics.getMsgService().sendMessage(new MockUserId("bar@host"),
				"hello you!");
		ics.getMsgService().sendMessage(new MockUserId("baz@host"),
				"What's up?");
		Assert.assertTrue(objectSaysOk);
		Assert.assertTrue(messageSaysOk);
	}

}
