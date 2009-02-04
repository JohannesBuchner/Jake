package com.jakeapp.core.commander;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;

/**
 * This class is (atm) for running manually in the IDE and not for inclusion in build automation
 *  
 * @author johannes
 */
public class JakeCommanderRuns {

	class FifoStreamer extends InputStream {

		String content = new String();

		int pointer = 0;

		public void addLine(String line) {
			content = content + line + "\n";
		}

		@Override
		public int read() throws IOException {
			byte b;
			try {
				b = content.getBytes()[pointer];
			} catch (ArrayIndexOutOfBoundsException e) {
				return -1;
			}
			pointer++;
			return b;
		}
	}

	@Test
	public void testMinimalRun() {
		FifoStreamer fifo = new FifoStreamer();
		fifo.addLine("coreLogin");
		fifo.addLine("coreLogout");
		fifo.addLine("stop");
		new JakeCommander(fifo);
	}

	@Ignore
	@Test
	public void interactiveRun() {
		new JakeCommander(System.in);
	}
}
