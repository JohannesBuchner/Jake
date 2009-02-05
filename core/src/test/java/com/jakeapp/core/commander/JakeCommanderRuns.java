package com.jakeapp.core.commander;


import java.io.File;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jakeapp.jake.test.FSTestCommons;

/**
 * This class is (atm) for running manually in the IDE and not for inclusion in
 * build automation
 * 
 * @author johannes
 */
public class JakeCommanderRuns {

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
	public void testMinimalRun() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin offline@project.creator mypw");
		fifo.addLine("coreLogout");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Test
	public void testMinimalProjectRun() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin offline@project.creator mypw");
		fifo.addLine("createProject " + tmpdir.getAbsolutePath());
		fifo.addLine("coreLogout");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Test
	public void testProjectRoundtrip() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin offline@project.creator mypw");
		fifo.addLine("createProject " + tmpdir.getAbsolutePath());
		fifo.addLine("closeProject");
		fifo.addLine("listProjects");
		fifo.addLine("openProject " + tmpdir.getAbsolutePath());
		fifo.addLine("deleteProject");
		fifo.addLine("coreLogout");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}
	
	@Test
	public void testListProjects() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin offline@project.creator mypw");
		fifo.addLine("listProjects");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Test
	@Ignore
	public void interactiveRun() {
		System.out.println("A empty temporary directory is available for you at " + tmpdir );
		new JakeCommander(System.in, true);
	}
}
