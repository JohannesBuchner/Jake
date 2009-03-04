package com.jakeapp.jake.ics;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.ics.impl.mock.MockICService;
import com.jakeapp.jake.ics.impl.mock.MockUserId;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;


public class TestMockICUsersService {
	private ICService ics = null; 
	
	private static UserId shortUserid1 = new MockUserId("foobar@baz");
	
	@Before
	public void setUp() throws Exception {
		this.ics = new MockICService();
		this.ics.getStatusService().login(shortUserid1, shortUserid1.getUserId());
	}

	@Test
	public void testregisterOnlineStatusListener() throws Exception {

		IOnlineStatusListener mylistener = new IOnlineStatusListener(){
			public void onlineStatusChanged(UserId userid) {
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