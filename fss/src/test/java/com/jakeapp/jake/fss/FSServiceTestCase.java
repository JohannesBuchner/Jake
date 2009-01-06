package com.jakeapp.jake.fss;

import java.awt.Desktop;
import java.io.File;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junitext.Prerequisite;

@Ignore
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

		fss = new FSService();
		fss.setRootPath(mytempdir);
		Assert.assertEquals("rootpath", mytempdir, fss.getRootPath());
	}

	public boolean hasSupportedDesktop() {
		if (Desktop.isDesktopSupported()) {
			return true;
		} else {
			log.warn("Desktop not supported, skipping tests!");
			return false;
		}
	}
}
