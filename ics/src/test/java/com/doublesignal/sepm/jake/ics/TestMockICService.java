package com.doublesignal.sepm.jake.ics;


import junit.framework.TestCase;

import com.doublesignal.sepm.jake.ics.exceptions.NoSuchUseridException;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;

public class TestMockICService extends TestCase {
	IICService ics = null; 
	
	public void setUp(){
		ics = new MockICService();
		System.out.println("TestMockICService setup");
	}
	/* we use firstname.lastname@host or nick@host notation for the Mock */
	public void testGetNames() throws Exception {
		try{
			ics.getFirstname("foo.bar");
			fail();
		}catch (NoSuchUseridException e) {
		}
		assertEquals("foo", ics.getFirstname("foo.bar@baz"));
		try{
			ics.getLastname("foo.bar");
			fail();
		}catch (NoSuchUseridException e) {
		}
		assertEquals("bar", ics.getLastname("foo.bar@baz"));
		assertEquals("", ics.getFirstname("foobar@baz"));
		assertEquals("", ics.getLastname("foobar@baz"));
	}
	public void testIsLoggedIn()  throws Exception {
		assertFalse(ics.isLoggedIn());
		try{
			ics.isLoggedIn("baz@host");
			fail();
		}catch (NotLoggedInException e) {
		}
		
		try{
			ics.login("foo", "bar");
			fail();
		}catch (NoSuchUseridException e) {
		}
		assertTrue(ics.login("foo@host", "foo@host"));
		assertTrue(ics.isLoggedIn("foo@host"));
		assertFalse(ics.isLoggedIn("bar@host"));
		assertTrue(ics.isLoggedIn("IhasSses@host"));
		assertTrue(ics.logout());
		assertFalse(ics.isLoggedIn());
		
		assertTrue(ics.login("bar@host", "bar@host"));
		assertTrue(ics.logout());
	}
	
	public void testregisterOnlineStatusListener() throws Exception {
		assertTrue(ics.login("bar@host", "bar@host"));

		IOnlineStatusListener mylistener = new IOnlineStatusListener(){
			public void onlineStatusChanged(String userid) {
				assertEquals(userid, "bar@host");
			}
		};
		ics.registerOnlineStatusListener(mylistener, "bar@host");
		assertTrue(ics.logout());
		ics = new MockICService(); /* "removing" listener */
		
	}
	
	Boolean messageSaysOk = false;
	Boolean objectSaysOk = false;
	public void testReceiveSend() throws Exception {
		messageSaysOk = false;
		objectSaysOk = false;
		
		IMessageReceiveListener mymsglistener = new IMessageReceiveListener(){
			int i = 0;
			public void receivedMessage(String from_userid, String content) {
				i++;
				System.out.println("receivedMessage: " + i);
				if(i==1){
					assertEquals("hello I", content);
					assertEquals("foo@host", from_userid);
				}else if(i==2){
					assertEquals("bar@host", from_userid);
					assertEquals("hello you! to you too", content);
				}else if(i==3){
					assertEquals("baz@host", from_userid);
					assertEquals("What's up? to you too", content);
					messageSaysOk = true;
				}else{
					fail();
				}
			}
		};
		IObjectReceiveListener myobjlistener = new IObjectReceiveListener(){
			int i = 0;
			public void receivedObject(String from_userid, String identifier, byte[] content) {
				i++;
				System.out.println("receivedObject: " + i);
				if(i == 1){
					assertEquals("foo@host", from_userid);
					assertEquals("42:12", identifier);
					assertEquals(new String(new byte[]{12, 32, 12, 34} ), new String(content));					
				}/* 
				commented since we don't reuse sendObject in the MockImplementation of sendMessage
				else if(i<=3){
					assertEquals(identifier, "message"); 
				}*/
				
				else{
					fail();
				}
				objectSaysOk = (i == 1);
			}
		};
		
		ics.registerReceiveMessageListener(mymsglistener);
		ics.registerReceiveObjectListener(myobjlistener);
		assertTrue(ics.login("foo@host", "foo@host"));
		ics.sendObject("foo@host", "42:12", new byte[]{12, 32, 12, 34} );
		ics.sendMessage("foo@host", "hello I");
		ics.sendMessage("bar@host", "hello you!");
		ics.sendMessage("baz@host", "What's up?");
		assertTrue(objectSaysOk);
		assertTrue(messageSaysOk);
	}

}
