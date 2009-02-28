package com.jakeapp.gui.console;


import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.test.FSTestCommons;
import com.jakeapp.jake.test.TmpdirEnabledTestCase;
import com.jakeapp.jake.test.XmppTestEnvironment;
import com.jakeapp.gui.console.JakeCommander;


/**
 * This class is (atm) for running manually in the IDE and not for inclusion in
 * build automation
 *
 * @author johannes
 */
public class PrimitivePokeRun extends TmpdirEnabledTestCase {

	
	
	private String user;

	@Override
	@Before
	public void setup() throws Exception {
		FSTestCommons.recursiveDelete(new File(".jake"));
		super.setup();
		
		this.user = XmppTestEnvironment.getXmppId("poker");
		XmppTestEnvironment.assureUserExists(XmppTestEnvironment.getHost(), "poker", "poker");
	}

	@Test
	public void bugRun() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin " + this.user + " poker");
		fifo.addLine("login");
		fifo.addLine("openProject " + this.tmpdir);
		fifo.addLine("startProject");
		fifo.addLine("poke " + this.user);
		fifo.addLine("coreLogout");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}
}
