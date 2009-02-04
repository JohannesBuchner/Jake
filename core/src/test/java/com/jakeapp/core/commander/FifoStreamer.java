package com.jakeapp.core.commander;

import java.io.IOException;
import java.io.InputStream;

/**
 * strings to inputstream converter  
 * @author johannes
 */
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