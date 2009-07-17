package com.jakeapp.jake.fss;

import java.io.File;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.test.FSTestCommons;

public class FSTestCase {

	protected String mytempdir = null;

	@Before
	public void setUp() throws Exception {
		mytempdir = FSTestCommons.provideTempDir().getAbsolutePath();
	}

	protected boolean recursiveDelete(File f) {
		return FSTestCommons.recursiveDelete(f);
	}

	@After
	public void tearDown() throws Exception {
		File f = new File(mytempdir);
		if (f.exists()) {
			Assert.assertTrue("recursiveDelete", recursiveDelete(f));
		}
		Assert.assertFalse("Cleanup done", f.exists());
	}

	@Test
	public void notest() {
		Assert.assertNull(null);
	}
}
