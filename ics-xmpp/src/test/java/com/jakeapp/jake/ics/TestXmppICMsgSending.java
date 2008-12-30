package com.jakeapp.jake.ics;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import com.jakeapp.jake.ics.impl.mock.MockUserId;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;

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
	
	@Ignore
	// TODO! 
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

		ics.getMsgService().registerReceiveMessageListener(mymsglistener);
		ics.getMsgService().sendMessage(shortUserid1, "hello I");
		ics.getMsgService().sendMessage(new MockUserId("bar@host"),
				"hello you!");
		ics.getMsgService().sendMessage(new MockUserId("baz@host"),
				"What's up?");
		Assert.assertTrue(messageSaysOk);
	}

}
