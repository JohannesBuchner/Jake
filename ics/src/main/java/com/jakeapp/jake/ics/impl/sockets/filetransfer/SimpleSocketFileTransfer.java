/**
 * 
 */
package com.jakeapp.jake.ics.impl.sockets.filetransfer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;

public class SimpleSocketFileTransfer extends FileTransfer implements Runnable {

	private static Logger log = Logger.getLogger(SimpleSocketFileTransfer.class);

	public static final String FILE_REQUEST = "file?";

	private static final int BLOCKSIZE = 1024 * 1024;

	private IFileTransfer ft;

	private InetSocketAddress other;

	private UUID transferKey;

	private long amountWritten = 0;

	private Socket s;

	public SimpleSocketFileTransfer(FileRequest r, InetSocketAddress other,
			UUID transferKey, int maximalRequestAgeSeconds) throws IOException {
		this.request = r;
		this.other = other;
		this.transferKey = transferKey;
		this.localFile = File.createTempFile("socket", "output");
		this.s = new Socket();
		log.debug("connecting to socket:  " + this.other );
		this.s.connect(this.other, maximalRequestAgeSeconds * 1000);
		this.status = Status.negotiated;
	}

	public IFileTransfer getFileTransfer() {
		return this.ft;
	}

	public void run() {

		try {
			log.info("starting transfer from " + this.other);
			byte[] b = new byte[BLOCKSIZE];

			if (this.status == Status.negotiated)
				this.status = Status.in_progress;

			OutputStream socketOut = this.s.getOutputStream();
			BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(
					this.localFile));

			log.debug("sending request");
			socketOut.write(this.transferKey.toString().getBytes());
			socketOut.flush();
			InputStream in = this.s.getInputStream();

			while (this.status == Status.in_progress) {
				log.debug("receiving content ... ");
				int len = in.read(b);
				log.debug("received " + len + " bytes ... ");
				if (len == -1)
					break;
				if (len > 0) {
					this.amountWritten += len;
					fileOut.write(b, 0, len);
				}
			}
			fileOut.flush();

			/**
			 * before with Channels, didn't work
			 * 
			 * <pre>
			 * this.s = SocketChannel.open(this.other);
			 * this.status = Status.in_progress;
			 * log.debug(&quot;sending request&quot;);
			 * this.s.write(ByteBuffer.wrap(this.transferKey.toString().getBytes()));
			 * this.s.socket().getOutputStream().flush();
			 * 
			 * FileChannel out = new FileOutputStream(this.localFile).getChannel();
			 * 
			 * log.debug(&quot;retrieving data ...&quot;);
			 * while (!this.s.socket().isClosed() &amp;&amp; out.isOpen() &amp;&amp; this.status != Status.cancelled) {
			 * 	this.amountWritten += out.transferFrom(this.s, this.amountWritten, BLOCKSIZE);
			 * 	log.debug(&quot;already received &quot; + this.amountWritten + &quot; bytes.&quot;);
			 * 	Thread.yield();
			 * 	// TODO: remove (is for debugging)
			 * 	try {
			 * 		Thread.sleep(1000);
			 * 	} catch (InterruptedException e) {
			 * 		// ignore
			 * 	}
			 * }
			 * </pre>
			 **/
			log.debug("retrieving data done: " + amountWritten + " bytes written");
		} catch (IOException e) {
			log.error("transfer failed", e);
			setError(e.getMessage());
		} catch (Exception e) {
			log.error("transfer failed for unexpected reason:", e);
			setError(e.getMessage());
		}
		try {
			this.s.close();
		} catch (IOException e) {
			log.warn("Closing socket failed.", e);
		}
		if (this.status != Status.cancelled) {
			log.debug("setting myself complete");
			this.status = Status.complete;
		}
	}

	@Override
	public void cancel() {
		super.cancel();
		try {
			this.s.close();
		} catch (IOException e) {
			log.error("cancel failed", e);
			setError(e.getMessage());
		}
	}

	@Override
	public long getAmountWritten() {
		return this.amountWritten;
	}

	@Override
	public Boolean isReceiving() {
		return true;
	}
}