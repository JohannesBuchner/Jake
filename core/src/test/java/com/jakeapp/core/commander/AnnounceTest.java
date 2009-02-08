package com.jakeapp.core.commander;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;


public class AnnounceTest extends TestDBEnabledTestCase {

	@Override
	protected String getDbTemplateName() {
		return "oneuserWithOneProjectContainingNotes";
	}

	@Test
	public void announce() {
		String pwd = new File(".").getAbsolutePath();
		System.out.println("You are now in " + pwd);
		String project = "testproject1";
		File projectdir = new File(pwd, project);
		projectdir.mkdir();
		Assert.assertTrue(folderExists(projectdir));
		projectdir.getAbsolutePath();
		
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin testuser1@localhost mypw");
		//fifo.addLine("createProject " + project);
		//fifo.addLine("closeProject");
		fifo.addLine("listProjects");
		fifo.addLine("openProject " + project);
		//fifo.addLine("deleteProject");
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
