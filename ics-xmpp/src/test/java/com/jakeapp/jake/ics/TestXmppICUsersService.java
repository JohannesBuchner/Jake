package com.jakeapp.jake.ics;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;

public class TestXmppICUsersService {
	private static final Logger log = Logger.getLogger(TestXmppICUsersService.class);

	private ICService ics;

	private static XmppUserId testUser1 = new XmppUserId(TestEnvironment
			.getXmppId("testuser1"));

	private static String testUser1Passwd = "testpasswd1";

	private static String testnamespace = "mynamespace";

	private static String testgroupname = "mygroupname";

	@Before
	public void setUp() throws Exception {
		log.debug("using" + testUser1.toString());
		TestEnvironment.assureUserIdExists(testUser1, testUser1Passwd);

		this.ics = new XmppICService(testnamespace, testgroupname);
		Assert.assertTrue(this.ics.getStatusService().login(testUser1,
				testUser1Passwd));
	}

	@Test
	public void testregisterOnlineStatusListener() throws Exception {

		IOnlineStatusListener mylistener = new IOnlineStatusListener() {

			public void onlineStatusChanged(String userid) {
				Assert.assertEquals(userid, testUser1);
			}
		};
		this.ics.getUsersService().registerOnlineStatusListener(mylistener);
	}

	@After
	public void teardown() throws Exception {
		log.debug("using" + testUser1.toString());
		if (this.ics != null)
			this.ics.getStatusService().logout();
		TestEnvironment.assureUserDeleted(testUser1, testUser1Passwd);
	}
}
