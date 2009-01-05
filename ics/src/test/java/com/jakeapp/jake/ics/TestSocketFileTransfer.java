package com.jakeapp.jake.ics;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import local.test.Tracer;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.impl.mock.MockUserId;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.FileRequestFileMapper;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.SimpleSocketFileTransferFactory;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;

//@RunWith(JMock.class)
public class TestSocketFileTransfer {

	private static Logger log = Logger.getLogger(TestSocketFileTransfer.class);

	private UserId userid2 = new MockUserId("provider@testhost");

	private UserId userid1 = new MockUserId("requester@testhost");

	private SimpleSocketFileTransferFactory sftf = new SimpleSocketFileTransferFactory();

	private String filename = "myfile.txt";

	private FileRequest outrequest = new FileRequest(this.filename, false, this.userid2);

	private FileRequest inrequest = new FileRequest(this.filename, true, this.userid1);

	private SimpleFakeMessageExchanger msgX = new SimpleFakeMessageExchanger();

	private Tracer t;

	private File testfile;

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

	private IFileTransfer fileTransfer;

	@Test
	public void testSocketMethod_accepting() throws Exception {
		IMsgService msg = msgX.addUser(userid1);
		createFriendlyPeer(t, userid2);

		ITransferMethod tfm;
		tfm = sftf.getTransferMethod(msg, userid1);

		tfm.request(this.outrequest, new INegotiationSuccessListener() {

			private Logger log = Logger.getLogger("Main:INegotiationSuccessListener");

			@Override
			public void failed(Throwable reason) {
				this.log.debug("clientside negotiation failed: " + reason);
				t.step("clientside negotiation failed");
			}

			@Override
			public void succeeded(IFileTransfer ft) {
				this.log.debug("clientside negotiation succeeded" + ft);
				t.step("clientside negotiation succeeded");
				Assert.assertEquals(filename, ft.getFileName());
				TestSocketFileTransfer.this.fileTransfer = ft;
			}
		});

		Assert.assertTrue(this.t.awaitStep("clientside negotiation succeeded", 1,
				TimeUnit.SECONDS));

		this.log.debug("main: waiting for filetransfer to finish: " + this.fileTransfer);

		this.t.await("getting content from fs,accepted request,started on server", 100,
				TimeUnit.MILLISECONDS);

		while (!this.fileTransfer.isDone()) {
			try {
				log.debug(this.t.toString());
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// best effort, doesn't matter
			}
		}
		log.debug(this.t.toString());
		Assert.assertTrue("timeout", this.t.isDone(1000, TimeUnit.MILLISECONDS));
	}

	private void createFriendlyPeer(final Tracer t, UserId userid2)
			throws NotLoggedInException {
		log.debug("createFriendlyPeer: creating MsgService");
		IMsgService msg = msgX.addUser(userid2);
		msg.registerReceiveMessageListener(new IMessageReceiveListener() {

			private Logger log = Logger.getLogger("Peer:IMessageReceiveListener");

			@Override
			public void receivedMessage(UserId from_userid, String content) {
				this.log.info("receivedMessage: " + from_userid + ": " + content);
				this.log.info("don't care, transfer should handle it");
			}
		});
		ITransferMethod tfm = sftf.getTransferMethod(msg, userid2);

		tfm.startServing(new IncomingTransferListener() {

			private Logger log = Logger.getLogger("Peer:IncomingTransferListener");

			@Override
			public boolean accept(FileRequest req) {
				log.debug("accept(): " + req);
				Assert.assertEquals(inrequest, req);
				Assert.assertEquals(inrequest.getFileName(), req.getFileName());
				t.step("accepted request");
				return true;
			}

			@Override
			public void started(IFileTransfer ft) {
				log.debug("started(): " + ft);
				t.step("started on server");
				Assert.assertEquals(inrequest, ft.getFileRequest());
			}
		}, new FileRequestFileMapper() {

			private Logger log = Logger.getLogger("Peer:FileRequestFileMapper");

			@Override
			public File getFileForRequest(FileRequest r) {
				this.log.debug("getFileForRequest(): " + r);
				Assert
						.assertEquals(TestSocketFileTransfer.this.filename, r
								.getFileName());
				t.step("getting content from fs");
				try {
					return getTestContentFile();
				} catch (FileNotFoundException e) {
					this.log.debug(e);
					return null;
				} catch (IOException e) {
					this.log.debug(e);
					return null;
				}
			}
		});
	}

	@Test
	@Ignore
	public void testUnavailableFile() {
		// TODO
	}

	@Test
	@Ignore
	public void testCancellingServer() {
		// TODO
	}

	public File getTestContentFile() throws IOException {
		File tmp = File.createTempFile("my_cute_", "tempfile");
		tmp.deleteOnExit();
		FileChannel fic = new FileInputStream(testfile).getChannel();
		FileChannel foc = new FileOutputStream(tmp).getChannel();
		fic.transferTo(0, testfile.length(), foc);
		fic.close();
		foc.close();
		return tmp;
	}

	@Test
	public void testStream() throws IOException {
		char[] cbuforig = new char[(int) (this.testfile.length() + 1)];
		char[] cbuftmp = new char[(int) (this.testfile.length() + 1)];

		FileInputStream fis = new FileInputStream(getTestContentFile());
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		int len = (br.read(cbuftmp));

		FileInputStream fisorig = new FileInputStream(this.testfile);
		BufferedReader brorig = new BufferedReader(new InputStreamReader(fisorig));
		int lenorig = (brorig.read(cbuforig));

		Assert.assertEquals(lenorig, len);
		Assert.assertEquals(new String(cbuforig), new String(cbuftmp));
	}
}
