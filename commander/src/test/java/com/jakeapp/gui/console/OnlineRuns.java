package com.jakeapp.gui.console;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.jakeapp.gui.console.JakeCommander;
import com.jakeapp.jake.test.TestDBEnabledTestCase;


public class OnlineRuns extends TestDBEnabledTestCase {

	private static final String project = "testproject1";

	private FifoStreamer fifo = new FifoStreamer();

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
		fifo.addLine("coreLogin testuser1@localhost mypw");
		fifo.addLine("openProject " + project);
	}

	private void go() {
		fifo.addLine("coreLogout");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Test
	public void login() {
		fifo.addLine("login");
		go();
	}

	@Test
	public void logout() {
		fifo.addLine("login");
		go();
	}

	@Test
	public void loginlogout() {
		fifo.addLine("login");
		fifo.addLine("logout");
		go();
	}
}
