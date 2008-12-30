package com.jakeapp.jake.ics;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import com.jakeapp.jake.ics.impl.mock.MockUserId;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IObjectReceiveListener;

public class TestXmppICMsgSending {

	private static final Logger log = Logger.getLogger(TestXmppICMsgSending.class);

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
		Assert.assertTrue(ics.getStatusService().login(shortUserid1, testUser1Passwd));
	}
	
	@After
	public void teardown() throws Exception {
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
					Assert.assertEquals(new String(new byte[] { 12, 32, 12, 34 }),
							new String(content));
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
