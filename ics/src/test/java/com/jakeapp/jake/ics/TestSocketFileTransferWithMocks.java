package com.jakeapp.jake.ics;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.UUID;

import local.test.Tracer;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.ics.impl.mock.MockUserId;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.SimpleSocketFileTransfer;

public class TestSocketFileTransferWithMocks {
	private static final int SERVER_PORT = 0;

	private static final Logger log = Logger.getLogger(TestSocketFileTransferWithMocks.class);

	private String filename = "myfile.txt";

	private Tracer t;

	private File testfile;

	private ServerSocket server;

	public String content = "Hello this is nice of you to write such nice things nice, eh";

	@Before
	public void setUp() throws Exception {
		t = new Tracer();
		testfile = new File("testContent.bin");
		if (!testfile.exists()) {
			log.debug(testfile.getAbsolutePath());
			// starting within eclipse:
			testfile = new File("src/test/resources/testContent.bin");
			if (!testfile.exists())
				throw new Exception("Testfile " + testfile + " not found.");
		}
	}


	@After
	public void teardown() throws Exception {
	}

	@Test
	public void testClient() throws Exception {
		FileRequest fr = new FileRequest("myfile.txt", true, new MockUserId("otherpeer"));
		UUID key = UUID.randomUUID();
		InetSocketAddress server = prepareServer(key);
		SimpleSocketFileTransfer client = new SimpleSocketFileTransfer(fr, server, key);
		new Thread(client).start();
		while (!client.isDone()) {
			log.debug("client filetransfer status: " + client.getStatus() + " - "
					+ client.getProgress());
			Thread.sleep(1000);
		}
		Assert.assertFalse(client.getStatus() == Status.error);
		Assert.assertTrue(client.getStatus() == Status.complete);
		Assert.assertNull(client.getError());
		Assert.assertTrue(client.getLocalFile().exists());
		Assert.assertTrue(client.getLocalFile().isFile());
		Assert.assertEquals(content.length(), client.getLocalFile().length());
	}


	private InetSocketAddress prepareServer(UUID key) throws IOException {
		server = new ServerSocket(SERVER_PORT);
		new Thread(new ServeThread(key, this.server)).start();
		InetAddress sa = InetAddress.getLocalHost();
		return new InetSocketAddress(sa, server.getLocalPort());
	}

	public class ServeThread implements Runnable {

		private final Logger log = Logger.getLogger(ServeThread.class);

		public ServeThread(UUID key, ServerSocket server) {
			super();
			this.key = key;
			this.server = server;
		}

		private ServerSocket server;

		private UUID key;

		@Override
		public void run() {
			try {
				Socket client = server.accept();
				log.debug("got a client");
				char[] incontent = new char[1000];
				InputStream input = client.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(input));
				int inlen = br.read(incontent);
				log.debug("got " + inlen + " bytes: " + new String(incontent));
				log.debug("should be: " + key);
				Assert.assertEquals(new String(incontent).trim(), key.toString());
				OutputStream output = client.getOutputStream();
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
				log.debug("writing content ...");
				bw.write(content);
				log.debug("writing content ... done");
				bw.flush();
				output.flush();
				bw.close();
				output.close();
				log.debug("closed");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void justRunServer() throws Exception {
		FileRequest fr = new FileRequest("myfile.txt", true, new MockUserId("otherpeer"));
		UUID key = UUID.randomUUID();
		
		InetSocketAddress server = prepareServer(key);
		log.debug("server ready at: " + server);
		log.debug("send " + key);
		Thread.sleep(100000);
	}
}
