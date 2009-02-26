package com.jakeapp.gui.console;

import java.io.IOException;
import java.io.InputStream;

/**
 * strings to inputstream converter  
 * @author johannes
 */
class FifoStreamer extends InputStream {

	private String content = "";

	private int pointer = 0;

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