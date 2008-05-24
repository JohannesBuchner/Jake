package com.doublesignal.sepm.jake.sync;

import java.util.LinkedList;
import java.util.List;

import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.LogAction;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.ics.IICService;
import com.doublesignal.sepm.jake.ics.MockICService;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.ics.exceptions.OtherUserOfflineException;
import com.doublesignal.sepm.jake.sync.exceptions.ObjectNotConfiguredException;

import junit.framework.TestCase;

public class MockSyncServiceTest extends TestCase {
	
	ISyncService ss = null;
	LinkedList<ProjectMember> pm = null;
	LinkedList<LogEntry> le = null;
	IICService ics = null;
	
	public void setUp() throws Exception{
		ss = new MockSyncService();
		pm = new LinkedList<ProjectMember>();
		pm.add(new ProjectMember("me@host"));
		pm.add(new ProjectMember("someoneWithAnNintheNameAndAnS@host"));
		pm.add(new ProjectMember("ihavejustoldstuff@host"));
		pm.add(new ProjectMember("offline@host"));
		
		le = new LinkedList<LogEntry>();
		ics = new MockICService();
		
		try{
			ss.pull(new JakeObject("foo/bar"));
			fail("ObjectNotConfiguredException");
		}catch (ObjectNotConfiguredException e) {
			
		}
		ss.setICService(ics);
		try{
			ss.pull(new JakeObject("foo/bar"));
			fail("ObjectNotConfiguredException");
		}catch (ObjectNotConfiguredException e) {
			
		}
		ss.setLogEntries(le);
		try{
			ss.pull(new JakeObject("foo/bar"));
			fail("ObjectNotConfiguredException");
		}catch (ObjectNotConfiguredException e) {
			
		}
		ss.setProjectMembers(pm);
		
		try{
			ss.pull(new JakeObject("foo/bar"));
		}catch (ObjectNotConfiguredException e) {
			fail("ObjectNotConfiguredException");
		}catch (Exception e) {
			/* we only test here that the object is now configured. */
		}
		try{
			ss.syncLogAndGetChanges("Imyselfamoffline@host");
			fail("NotLoggedInException");
		}catch (NotLoggedInException e) {
		}
		
		assertTrue(ics.login("me@host", "me@host"));
		assertTrue(ics.isLoggedIn());
	}
	public void testSync() throws Exception{
		assertEquals(le.size(),0);
		assertTrue(ics.isLoggedIn());
		try{
			ss.syncLogAndGetChanges("notaprojectmember@host");
			fail("NotAProjectMemberException");
		}catch (NotAProjectMemberException e) {
		}

		try{
			ss.syncLogAndGetChanges("offline@host");
			fail("OtherUserOfflineException");
		}catch (OtherUserOfflineException e) {
		}
		List<JakeObject> changes;
		changes = ss.syncLogAndGetChanges("ihavejustoldstuff@host");
		assertEquals(le.size(),0);
		assertEquals(changes.size(),0);
		
		changes = ss.syncLogAndGetChanges("someoneWithAnNintheNameAndAnS@host");
		assertEquals(changes.size(),1);
		assertEquals(changes.get(0).getName(), "Projektauftrag/Lastenheft.txt");
		assertEquals(le.size(),1);
		assertEquals(le.get(0).getJakeObjectName(), changes.get(0).getName());
		
		assertEquals(le.get(0).getAction(), LogAction.NEW_VERSION);
		assertNotNull(le.get(0).getTimestamp());
	}
	
	public void testpull() throws Exception{
		assertEquals(le.size(),0);
		assertTrue(ics.isLoggedIn());
		
		ss.syncLogAndGetChanges("someoneWithAnNintheNameAndAnS@host");
		JakeObject jo = new JakeObject("Projektauftrag/Lastenheft.txt"); 
		byte[] bcontent = ss.pull(jo);
		assertNotNull(bcontent);
		String content = new String(bcontent);
		
		assertTrue(content.startsWith("This is " + jo.getName()));
		
	}
	public void testpush() throws Exception{
		assertEquals(le.size(),0);
		assertTrue(ics.isLoggedIn());
		
		JakeObject jo = new JakeObject("Projektauftrag/test1.txt"); 
		ss.push(jo, "I don't like commit messages");
		assertEquals(le.size(),1);
		assertEquals(le.get(0).getJakeObjectName(), jo.getName());
		
		assertEquals(le.get(0).getAction(), LogAction.NEW_VERSION);
		assertNotNull(le.get(0).getTimestamp());
		
		byte[] bcontent = ss.pull(jo);
		assertNotNull(bcontent);
		String content = new String(bcontent);
		
		assertTrue(content.startsWith("This is " + jo.getName()));
	}

}
