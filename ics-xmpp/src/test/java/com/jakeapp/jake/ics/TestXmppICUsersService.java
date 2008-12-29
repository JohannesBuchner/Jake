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
	
	private static UserId shortUserid1 = new XmppUserId("foobar@baz");
	private static String testnamespace = "mynamespace";
	private static String testgroupname = "mygroupname";

	@Before
	public void setUp() throws Exception {
		this.ics = new XmppICService(testnamespace, testgroupname);
		Assert.assertTrue(this.ics.getStatusService().login(shortUserid1, shortUserid1.getUserId()));
	}

	@Test
	public void testregisterOnlineStatusListener() throws Exception {

		IOnlineStatusListener mylistener = new IOnlineStatusListener(){
			public void onlineStatusChanged(String userid) {
				Assert.assertEquals(userid, shortUserid1);
			}
		};
		this.ics.getUsersService().registerOnlineStatusListener(mylistener);
	}
	
	@After
	public void teardown() throws Exception {
		this.ics.getStatusService().logout();
	}
}
