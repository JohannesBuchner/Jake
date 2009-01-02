package com.jakeapp.jake.ics;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import local.test.Counter;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;

public class TestXmppICMsgSending {

	private static final Logger log = Logger
			.getLogger(TestXmppICMsgSending.class);

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
	public void setUp() throws Exception {
		TestEnvironment.assureUserIdExists(testUser1, testUser1Passwd);
		TestEnvironment.assureUserIdExists(testUser2, testUser2Passwd);

		this.ics = new XmppICService(testnamespace, testgroupname);
		Assert.assertTrue(this.ics.getStatusService().login(testUser1,
				testUser1Passwd));
		this.ics2 = new XmppICService(testnamespace, testgroupname);
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
	public void testReceiveSend() throws Exception {
		final String testmsgcontent1 = "Testmessagecontent1";
		final String testmsgcontent2 = "Testmessagecontent2";
		this.ics.getMsgService().sendMessage(testUser2, testmsgcontent1);
		final Semaphore s = new Semaphore(0);
		final Counter c = new Counter();

		Assert.assertTrue(this.ics2.getStatusService().login(testUser2,
				testUser2Passwd));
		this.ics2.getMsgService().registerReceiveMessageListener(
				new IMessageReceiveListener() {

					@Override
					public void receivedMessage(UserId from_userid,
							String content) {
						s.release();
						c.inc();
						log.debug("receivedMessage call " + c.getValue());
						log.debug("receivedMessage: " + from_userid + " says "
								+ content);
						if (c.getValue() == 1) {
							Assert.assertEquals(testUser1, from_userid);
							Assert.assertEquals(testmsgcontent1, content);
						} else if (c.getValue() == 2) {
							Assert.assertEquals(testUser1, from_userid);
							Assert.assertEquals(testmsgcontent2, content);
						} else {
							Assert.fail("unexpected call");
						}
					}

				});
		this.ics.getMsgService().sendMessage(testUser2, testmsgcontent2);
		Assert.assertTrue(s.tryAcquire(2, 5, TimeUnit.SECONDS));
	}

	@Test
	public void testReceiveSend_MultipleInteractions() throws Exception {
		final String testmsgcontent1 = "Testmessagecontent1";
		final String testmsgcontent2 = testmsgcontent1 + " >> really?";
		final String testmsgcontent3 = testmsgcontent2 + " >> yeah, srsly!";
		final Semaphore s = new Semaphore(0);
		final Counter c = new Counter();

		Assert.assertTrue(this.ics2.getStatusService().login(testUser2,
				testUser2Passwd));
		this.ics2.getMsgService().registerReceiveMessageListener(
				new IMessageReceiveListener() {

					@Override
					public void receivedMessage(UserId from_userid,
							String content) {
						s.release();
						c.inc();
						log.debug("receivedMessage call " + c.getValue());
						log.debug("receivedMessage: " + from_userid + " says "
								+ content);
						if (c.getValue() == 1) {
							Assert.assertEquals(testUser1, from_userid);
							Assert.assertEquals(testmsgcontent1, content);
							try {
								TestXmppICMsgSending.this.ics2.getMsgService()
										.sendMessage(from_userid,
												content + " >> really?");
							} catch (Exception e) {
								log.error("", e);
								Assert.fail("Unexpected exception");
							}
						} else if (c.getValue() == 2) {
							Assert.assertEquals(testUser1, from_userid);
							Assert.assertEquals(testmsgcontent3, content);
						} else {
							Assert.fail("Unexpected call");
						}
					}

				});
		this.ics.getMsgService().registerReceiveMessageListener(
				new IMessageReceiveListener() {
					// practically a echo service
					@Override
					public void receivedMessage(UserId from_userid,
							String content) {
						try {
							TestXmppICMsgSending.this.ics.getMsgService()
									.sendMessage(from_userid,
											content + " >> yeah, srsly!");
							Assert.assertEquals(testmsgcontent2, content);
						} catch (Exception e) {
							log.error("", e);
							Assert.fail("Unexpected exception");
						}
					}

				});
		this.ics.getMsgService().sendMessage(testUser2, testmsgcontent1);
		Assert.assertTrue(s.tryAcquire(2, 5, TimeUnit.SECONDS));
	}
	@Test
	public void testReceiveSend_XML() throws Exception {
		final String testmsgcontent = "<msg>hello</msg>";
		this.ics.getMsgService().sendMessage(testUser2, testmsgcontent);
		final Semaphore s = new Semaphore(0);
		final Counter c = new Counter();

		Assert.assertTrue(this.ics2.getStatusService().login(testUser2,
				testUser2Passwd));
		this.ics2.getMsgService().registerReceiveMessageListener(
				new IMessageReceiveListener() {

					@Override
					public void receivedMessage(UserId from_userid,
							String content) {
						s.release();
						c.inc();
						log.debug("receivedMessage call " + c.getValue());
						log.debug("receivedMessage: " + from_userid + " says "
								+ content);
						if (c.getValue() == 1) {
							Assert.assertEquals(testUser1, from_userid);
							Assert.assertEquals(testmsgcontent, content);
						} else {
							Assert.fail("unexpected call");
						}
					}

				});
		this.ics.getMsgService().sendMessage(testUser2, testmsgcontent);
		Assert.assertTrue(s.tryAcquire(2, 5, TimeUnit.SECONDS));
	}
}
