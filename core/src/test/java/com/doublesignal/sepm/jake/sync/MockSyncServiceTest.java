package com.doublesignal.sepm.jake.sync;

import java.util.LinkedList;

import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.ics.MockICService;
import com.doublesignal.sepm.jake.sync.exceptions.ObjectNotConfiguredException;

import junit.framework.TestCase;

public class MockSyncServiceTest extends TestCase {
	
	MockSyncService ss = new MockSyncService();
	LinkedList<ProjectMember> pm = new LinkedList<ProjectMember>();
	LinkedList<LogEntry> le = new LinkedList<LogEntry>();
	
	public void setup() throws Exception{
		try{
			ss.pull(new JakeObject("foo/bar"));
			fail("ObjectNotConfiguredException");
		}catch (ObjectNotConfiguredException e) {
			
		}
		ss.setICService(new MockICService());
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
	}
	public void testpull() throws Exception{
		
		
	}
	
	
}
