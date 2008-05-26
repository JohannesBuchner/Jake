package com.doublesignal.sepm.jake.fss;

import java.io.File;

import com.doublesignal.sepm.jake.fss.FSService;


public class FSServiceTestCase extends FSTestCase {
	protected FSService fss = null;
	
	protected void wipeRoot() {
		File f = new File(fss.getRootPath());
		assertTrue(f.exists() && f.isDirectory());
		assertTrue(recursiveDelete(f));
		f.mkdirs();
		assertTrue(f.exists() && f.isDirectory() && f.list().length == 0);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		fss = new FSService();
		fss.setRootPath(mytempdir);
		assertEquals("rootpath",mytempdir,fss.getRootPath());
		assertFalse(fss.getRootPath().startsWith("/home"));
	}
}
