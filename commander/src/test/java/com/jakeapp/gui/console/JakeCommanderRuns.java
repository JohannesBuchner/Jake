package com.jakeapp.gui.console;


import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.test.FSTestCommons;
import com.jakeapp.jake.test.TmpdirEnabledTestCase;
import com.jakeapp.gui.console.JakeCommander;


/**
 * This class is (atm) for running manually in the IDE and not for inclusion in
 * build automation
 * 
 * @author johannes
 */
public class JakeCommanderRuns extends TmpdirEnabledTestCase {

	@Override
	@Before
	public void setup() throws Exception {
		FSTestCommons.recursiveDelete(new File(".jake"));
		super.setup();
	}

	@Test
	public void minimalRun() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin offline@project.creator mypw");
		fifo.addLine("coreLogout");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Test
	public void minimalProjectRun() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin offline@project.creator mypw");
		fifo.addLine("createProject " + tmpdir.getAbsolutePath());
		fifo.addLine("coreLogout");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Test
	public void projectRoundtrip() {
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
	public void listProjects() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin offline@project.creator mypw");
		fifo.addLine("listProjects");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Test
	public void interactiveRun() {
		System.out.println("A empty temporary directory is available for you at " + tmpdir);
		new JakeCommander(System.in, true);
	}
}
