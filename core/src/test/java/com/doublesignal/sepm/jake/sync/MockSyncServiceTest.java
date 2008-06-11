package com.doublesignal.sepm.jake.sync;

import java.util.List;

import com.doublesignal.sepm.jake.core.dao.HsqlJakeDatabase;
import com.doublesignal.sepm.jake.core.dao.IJakeDatabase;
import com.doublesignal.sepm.jake.core.dao.JdbcStupidDatabaseDaoTest;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.LogAction;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.fss.FSService;
import com.doublesignal.sepm.jake.fss.IFSService;
import com.doublesignal.sepm.jake.ics.IICService;
import com.doublesignal.sepm.jake.ics.MockICService;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.ics.exceptions.OtherUserOfflineException;
import com.doublesignal.sepm.jake.sync.exceptions.NotAProjectMemberException;
import com.doublesignal.sepm.jake.sync.exceptions.ObjectNotConfiguredException;

public class MockSyncServiceTest extends FSTestCase {
	
	ISyncService ss = null;
	IICService ics = null;
	IFSService fss = null;
	HsqlJakeDatabase db = null;
	
	public void setUp() throws Exception{
		super.setUp();
		db = JdbcStupidDatabaseDaoTest.setUpDatabase();
		JdbcStupidDatabaseDaoTest.teardownDatabase(db);
		db = JdbcStupidDatabaseDaoTest.setUpDatabase();
		ss = new MockSyncService();
		db.getProjectMemberDao().save(new ProjectMember("me@host"));
		db.getProjectMemberDao().save(new ProjectMember("someoneWithAnNintheNameAndAnS@host"));
		db.getProjectMemberDao().save(new ProjectMember("ihavejustoldstuff@host"));
		db.getProjectMemberDao().save(new ProjectMember("offline@host"));
		
		ics = new MockICService();
		fss = new FSService();
		assertNotNull(mytempdir);
		fss.setRootPath(mytempdir);
		
		try{
			ss.pull(new FileObject("foo/bar"));
			fail("ObjectNotConfiguredException");
		}catch (ObjectNotConfiguredException e) {
			
		}
		ss.setICService(ics);
		try{
			ss.pull(new FileObject("foo/bar"));
			fail("ObjectNotConfiguredException");
		}catch (ObjectNotConfiguredException e) {
			
		}
		ss.setDatabase(db);
		
		try{
			ss.pull(new FileObject("foo/bar"));
			fail("ObjectNotConfiguredException");
		}catch (ObjectNotConfiguredException e) {
			
		}
		ss.setFSService(fss);
		
		try{
			ss.pull(new FileObject("foo/bar"));
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
		List<LogEntry> le = db.getLogEntryDao().getAll();
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
		List<JakeObject> changes = ss.syncLogAndGetChanges("ihavejustoldstuff@host");
		le = db.getLogEntryDao().getAll();
		assertEquals(le.size(),0);
		assertEquals(changes.size(),0);
		
		changes = ss.syncLogAndGetChanges("someoneWithAnNintheNameAndAnS@host");
		le = db.getLogEntryDao().getAll();
		assertEquals(changes.size(),1);
		assertEquals(changes.get(0).getName(), "Projektauftrag/Lastenheft.txt");
		assertEquals(le.size(),1);
		assertEquals(le.get(0).getJakeObjectName(), changes.get(0).getName());
		
		assertEquals(le.get(0).getAction(), LogAction.NEW_VERSION);
		assertNotNull(le.get(0).getTimestamp());
	}
	
	public void testpull() throws Exception{
		List<LogEntry> le = db.getLogEntryDao().getAll();
		assertEquals(le.size(),0);
		assertTrue(ics.isLoggedIn());
		
		ss.syncLogAndGetChanges("someoneWithAnNintheNameAndAnS@host");
		FileObject jo = new FileObject("Projektauftrag/Lastenheft.txt"); 
		byte[] bcontent = ss.pull(jo);
		assertNotNull(bcontent);
		String content = new String(bcontent);
		
		assertTrue(content.startsWith("This is " + jo.getName()));
		
	}
	public void testpush() throws Exception{
		List<LogEntry> le = db.getLogEntryDao().getAll();
		assertEquals(le.size(),0);
		assertTrue(ics.isLoggedIn());
		
		FileObject jo = new FileObject("Projektauftrag/test1.txt");
		fss.writeFile("Projektauftrag/test1.txt", "Helo funky boy!".getBytes());
		
		ss.push(jo, "me@host", "I don't like commit messages");
		le = db.getLogEntryDao().getAll();
		assertEquals(le.size(),1);
		assertEquals(le.get(0).getJakeObjectName(), jo.getName());
		
		assertEquals(le.get(0).getAction(), LogAction.NEW_VERSION);
		assertNotNull(le.get(0).getTimestamp());
		
		byte[] bcontent = ss.pull(jo);
		assertNotNull(bcontent);
		String content = new String(bcontent);
		
		assertEquals(content,"Helo funky boy!");
	}

}
