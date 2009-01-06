package com.jakeapp.jake.fss;

import java.awt.Desktop;
import java.io.File;
import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junitext.Prerequisite;

public class FSServiceTestCase extends FSTestCase {
	static Logger log = Logger.getLogger(FSServiceTestCase.class);
	protected FSService fss = null;
	
	protected void wipeRoot() {
		File f = new File(fss.getRootPath());
		Assert.assertTrue(f.exists() && f.isDirectory());
		Assert.assertTrue(recursiveDelete(f));
		f.mkdirs();
		Assert.assertTrue(f.exists() && f.isDirectory() && f.list().length == 0);
	}
	
	@Override
	@Prerequisite(requires = "hasSupportedDesktop")
	public void setUp() throws Exception {
		super.setUp();
		
		try {
			fss = new FSService();
		} catch (NoSuchAlgorithmException e) {
			log.warn("Not supported, skipping tests!", e);
		}
		fss.setRootPath(mytempdir);
		Assert.assertEquals("rootpath",mytempdir,fss.getRootPath());
	}
	
	public boolean hasSupportedDesktop(){
		return Desktop.isDesktopSupported();
	}
}
