package com.jakeapp.jake.ics;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;

public class TestXmppICUsersService {

	private ICService ics = null;

	private static XmppUserId testUser1 = new XmppUserId("testuser1@"
			+ TestEnvironment.host);

	private static String testUser1Passwd = "testpasswd1";

	private static String testnamespace = "mynamespace";

	private static String testgroupname = "mygroupname";

	@Before
	public void setUp() throws Exception {
		TestEnvironment.assureUserIdExists(testUser1, testUser1Passwd);

		this.ics = new XmppICService(testnamespace, testgroupname);
		Assert.assertTrue(this.ics.getStatusService().login(testUser1,
				testUser1.getUserId()));
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
		this.ics.getStatusService().logout();
		TestEnvironment.assureUserDeleted(testUser1, testUser1Passwd);
	}
}
