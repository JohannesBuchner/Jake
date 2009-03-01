package com.jakeapp.gui.console;


import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jakeapp.jake.test.FSTestCommons;
import com.jakeapp.jake.test.TmpdirEnabledTestCase;
import com.jakeapp.jake.test.XmppTestEnvironment;
import com.jakeapp.jake.test.TestDBEnabledTestCase;
import com.jakeapp.gui.console.JakeCommander;
import com.googlecode.junit.ext.PrerequisiteAwareClassRunner;
import com.googlecode.junit.ext.Prerequisite;


/**
 * This class is (atm) for running manually in the IDE and not for inclusion in
 * build automation
 *
 * @author johannes
 */
@RunWith(PrerequisiteAwareClassRunner.class)
public class PrimitivePokeRunWithMySpecialDirectory extends TestDBEnabledTestCase {



	private String user;
	private String password;

	@Override
	@Before
	public void setup() throws Exception {
		super.setup();

		this.user = XmppTestEnvironment.getXmppId("testuser1");
		this.password = "testpasswd1";
		XmppTestEnvironment.assureUserExists(XmppTestEnvironment.getHost(), "testuser1", this.password);
	}

	@Override
	protected String getDbTemplateName() {
		// it's EPIC, man!
		return "oneuserWithOneProjectContainingNotes";
	}

	@Test
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void bugRun() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin " + this.user + " " + this.password);
		fifo.addLine("login");
		fifo.addLine("listProjects");
		fifo.addLine("selectFirstProject");
		fifo.addLine("startProject");
		fifo.addLine("poke " + this.user);
		fifo.addLine("coreLogout");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}
}