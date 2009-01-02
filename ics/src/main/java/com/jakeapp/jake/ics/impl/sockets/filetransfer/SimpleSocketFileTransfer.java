/**
 * 
 */
package com.jakeapp.jake.ics.impl.sockets.filetransfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;

public class SimpleSocketFileTransfer extends FileTransfer implements Runnable, IFileTransfer {

	private static Logger log = Logger.getLogger(SimpleSocketFileTransfer.class);

	public static final String FILE_REQUEST = "file?";

	private IFileTransfer ft;

	private InetSocketAddress other;

	private SocketChannel s;

	SimpleSocketFileTransfer(FileRequest r, InetSocketAddress other)
			throws IOException {
		this.request = r;
		this.other = other;
		localFile = File.createTempFile("socket", "output");
		status = Status.negotiated;
	}

	public IFileTransfer getFileTransfer() {
		return this.ft;
	}

	public void run() {
		try {
			s = SocketChannel.open(other);
			status = Status.in_progress;
			s.write(ByteBuffer.wrap((FILE_REQUEST + request.getFileName() + "|")
					.getBytes()));
			s.socket().getOutputStream().flush();
			status = Status.in_progress;
			long size = new FileOutputStream(localFile).getChannel()
					.transferFrom(s, 0, request.getFileSize());
			if (size != request.getFileSize()) {
				setError("File incomplete");
				status = Status.complete;
			} else {
				status = Status.complete;
			}
		} catch (IOException e) {
			SimpleSocketFileTransferFactory.log.error("cancel failed", e);
			setError(e.getMessage());
		}
	}

	@Override
	public void cancel() {
		super.cancel();
		try {
			s.close();
		} catch (IOException e) {
			log.error("cancel failed", e);
			setError(e.getMessage());
		}
	}

	@Override
	public long getAmountWritten() {
		if (status == Status.complete)
			return getFileSize();
		else
			return 0;
	}

	@Override
	public Boolean isReceiving() {
		return true;
	}
}