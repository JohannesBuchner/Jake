package com.doublesignal.sepm.jake.fss;

public class FSServiceTest extends FSTestCase {
	FSService fss = null;
	
	public void setUp() throws Exception{
		fss = new FSService();
		super.setUp();
	}
	
	void testIsValidRelpath() throws Exception{
		assertTrue(true);
		assertTrue(fss.isValidRelpath("/"));
		assertTrue(fss.isValidRelpath("foo"));
		assertTrue(fss.isValidRelpath("foo.bar"));
		assertTrue(fss.isValidRelpath("rANdoM.xls"));
		assertTrue(fss.isValidRelpath("fold/er.txt"));
		assertTrue(fss.isValidRelpath("crazy/named/file/that.has.more.ext.s"));
		assertTrue(fss.isValidRelpath("cool+-035.x_chars"));
		assertTrue(fss.isValidRelpath("a"));
		assertTrue(fss.isValidRelpath("ac (also cool).file"));
		
		String[] s = {"~","*","..","#","=","}","{","$","\"",
				"'","!","%","&","<",">","|","§","@","°","^","\\" };
		
		for (int i = 0; i < s.length; i++) {
			assertFalse(s[i], fss.isValidRelpath("~"));
		}
		assertFalse(fss.isValidRelpath("windows\\path"));
		assertFalse(fss.isValidRelpath("\\"));
		assertFalse(fss.isValidRelpath(":"));
				
		assertFalse(fss.isValidRelpath("note:random"));
		
	}
	
	
}
