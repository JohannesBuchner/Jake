package com.jakeapp.core.commander;

import java.io.File;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jakeapp.jake.test.FSTestCommons;

@Ignore
public class TmpdirEnabledTestCase {

	protected File tmpdir;

	@Before
	public void setup() throws Exception {
		tmpdir = FSTestCommons.provideTempDir();
	}

	@After
	public void teardown() throws Exception {
		if (tmpdir.exists())
			Assert.assertTrue(FSTestCommons.recursiveDelete(tmpdir));
		Assert.assertFalse("Cleanup done", tmpdir.exists());
	}
	
	@Test
	public void notest() throws Exception {
	}

}