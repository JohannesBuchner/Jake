package com.jakeapp.jake.ics;

import junit.framework.TestCase;

import org.junit.Before;

import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.impl.mock.MockUserId;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IObjectReceiveListener;

public class TestXmppICStatusService extends TestCase {
	private ICService ics = null; 
	
	private static XmppUserId wrongUserid1 = new XmppUserId("foo.bar");
	private static XmppUserId offlineUserId = new XmppUserId("foo.bar@" + TestEnvironment.host);
	private static XmppUserId onlineUserId = new XmppUserId("IhasSses@" + TestEnvironment.host);
	private static XmppUserId shortUserid1 = new XmppUserId("foobar@" + TestEnvironment.host);
	
	private static String somePassword = "bar";

	private static String testUser1Passwd = "testpasswd1";

	private static String testnamespace = "mynamespace";

	private static String testgroupname = "mygroupname";

	@Before
	public void setUp() throws Exception {
		TestEnvironment.assureUserIdExistsAndConnect(shortUserid1, testUser1Passwd);
		TestEnvironment.assureUserIdExistsAndConnect(onlineUserId, testUser1Passwd);
		TestEnvironment.assureUserIdExistsAndConnect(offlineUserId, testUser1Passwd);

		this.ics = new XmppICService(testnamespace, testgroupname);
	}
	
	/* we use firstname.lastname@host or nick@host notation for the Mock */
	public void testGetNames() throws Exception {
		try{
			ics.getStatusService().getFirstname(wrongUserid1);
			fail();
		}catch (NoSuchUseridException e) {
		}
		assertEquals("foo", ics.getStatusService().getFirstname(offlineUserId));
		try{
			ics.getStatusService().getLastname(wrongUserid1);
			fail();
		}catch (NoSuchUseridException e) {
		}
		assertEquals("bar", ics.getStatusService().getLastname(offlineUserId));
		assertEquals("", ics.getStatusService().getFirstname(shortUserid1));
		assertEquals("", ics.getStatusService().getLastname(shortUserid1));
	}
	
	public void testIsLoggedIn()  throws Exception {
		assertFalse(ics.getStatusService().isLoggedIn());
		try{
			ics.getStatusService().isLoggedIn(shortUserid1);
			fail();
		}catch (NotLoggedInException e) {
		}
		
		try{
			ics.getStatusService().login(wrongUserid1, somePassword);
			fail();
		}catch (NoSuchUseridException e) {
		}
		assertTrue(ics.getStatusService().login(shortUserid1, testUser1Passwd));
		assertTrue(ics.getStatusService().isLoggedIn(shortUserid1));
		assertFalse(ics.getStatusService().isLoggedIn(offlineUserId));
		assertTrue(ics.getStatusService().isLoggedIn(onlineUserId));
		ics.getStatusService().logout();
		assertFalse(ics.getStatusService().isLoggedIn());
		
		assertTrue(ics.getStatusService().login(offlineUserId, testUser1Passwd));
		ics.getStatusService().logout();
	}
	private Boolean messageSaysOk = false;
	private Boolean objectSaysOk = false;
	public void testReceiveSend() throws Exception {
		messageSaysOk = false;
		objectSaysOk = false;
		
		IMessageReceiveListener mymsglistener = new IMessageReceiveListener(){
			private int i = 0;
			public void receivedMessage(UserId from_userid, String content) {
				i++;
				if(i==1){
					assertEquals(shortUserid1, from_userid.getUserId());
					assertEquals("hello I", content);
				}else if(i==2){
					assertEquals("bar@host", from_userid.getUserId());
					assertEquals("hello you! to you too", content);
				}else if(i==3){
					assertEquals("baz@host", from_userid.getUserId());
					assertEquals("What's up? to you too", content);
					messageSaysOk = true;
				}else{
					fail();
				}
			}
		};
		IObjectReceiveListener myobjlistener = new IObjectReceiveListener(){
			private int i = 0;
			public void receivedObject(UserId from_userid, String identifier, byte[] content) {
				i++;
				if(i == 1){
					assertEquals(shortUserid1, from_userid.getUserId());
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
		
		ics.getMsgService().registerReceiveMessageListener(mymsglistener);
		ics.getMsgService().registerReceiveObjectListener(myobjlistener);
		assertTrue(ics.getStatusService().login(shortUserid1, testUser1Passwd));
		ics.getMsgService().sendObject(shortUserid1, "42:12", new byte[]{12, 32, 12, 34} );
		ics.getMsgService().sendMessage(shortUserid1, "hello I");
		ics.getMsgService().sendMessage(new MockUserId("bar@host"), "hello you!");
		ics.getMsgService().sendMessage(new MockUserId("baz@host"), "What's up?");
		assertTrue(objectSaysOk);
		assertTrue(messageSaysOk);
	}

}
