package com.jakeapp.jake.fss;

import java.io.File;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.junit.ext.Prerequisite;
import com.googlecode.junit.ext.PrerequisiteAwareClassRunner;

@RunWith(PrerequisiteAwareClassRunner.class)
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
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void setUp() throws Exception {
		super.setUp();

		fss = new FSService();
		fss.setRootPath(mytempdir);
		Assert.assertEquals("rootpath", mytempdir, fss.getRootPath());
	}
}
