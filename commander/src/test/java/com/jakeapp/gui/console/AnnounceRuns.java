package com.jakeapp.gui.console;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.jakeapp.gui.console.JakeCommander;
import com.jakeapp.jake.test.TestDBEnabledTestCase;


public class AnnounceRuns extends TestDBEnabledTestCase {

	private static final String project = "testproject1";

	private static final String obj = "00000000-0000-000b-0000-000000000001";

	private static final String nonexistantobj = "FFFFFFFF-0000-000b-0000-000000000001";
	
	@Override
	protected String getDbTemplateName() {
		return "oneuserWithOneProjectContainingNotes";
	}

	@Override
	@Before
	public void setup() throws Exception {
		super.setup();
		String pwd = new File(".").getAbsolutePath();
		System.out.println("You are now in " + pwd);
		File projectdir = new File(pwd, project);
		projectdir.mkdir();
		Assert.assertTrue(folderExists(projectdir));
	}

	@Test
	public void testSetup() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin testuser1@localhost mypw");
		fifo.addLine("listProjects");
		fifo.addLine("openProject " + project);
		fifo.addLine("status");
		fifo.addLine("coreLogout");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Test
	public void listObjects() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin testuser1@localhost mypw");
		fifo.addLine("openProject " + project);
		fifo.addLine("listObjects");
		fifo.addLine("coreLogout");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Test
	public void announce() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin testuser1@localhost mypw");
		fifo.addLine("openProject " + project);
		fifo.addLine("announce " + obj);
		fifo.addLine("announce " + obj);
		fifo.addLine("log      " + obj);
		fifo.addLine("objectStatus " + obj);
		fifo.addLine("coreLogout");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Test
	public void objectCycle() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin testuser1@localhost mypw");
		fifo.addLine("openProject " + project);
		fifo.addLine("objectStatus " + obj);
		fifo.addLine("announce     " + obj);
		fifo.addLine("objectStatus " + obj);
		fifo.addLine("modify       " + obj);
		fifo.addLine("objectStatus " + obj);
		fifo.addLine("lock         " + obj);
		fifo.addLine("objectStatus " + obj);
		fifo.addLine("unlock       " + obj);
		fifo.addLine("objectStatus " + obj);
		fifo.addLine("announce     " + obj);
		fifo.addLine("objectStatus " + obj);
		fifo.addLine("delete       " + obj);
		fifo.addLine("objectStatus " + obj);
		fifo.addLine("log          " + obj);
		fifo.addLine("coreLogout");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Test
	public void log() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin testuser1@localhost mypw");
		fifo.addLine("openProject " + project);
		fifo.addLine("log      00000000-0000-000b-0000-000000000001");
		fifo.addLine("coreLogout");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Test
	public void interactiveRun() {
		System.out.println("You are now in " + new File(".").getAbsolutePath());
		new File("testproject1").mkdir();
		new JakeCommander(System.in, true);
	}
}
