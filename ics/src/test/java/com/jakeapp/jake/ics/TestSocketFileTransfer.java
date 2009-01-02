package com.jakeapp.jake.ics;


import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import local.test.Tracer;

import org.apache.log4j.Logger;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.impl.mock.MockUserId;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.FileRequestFileMapper;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.STUNEnabledFileTransferFactory;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.SimpleSocketFileTransfer;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.SimpleSocketFileTransferFactory;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;

//@RunWith(JMock.class)
public class TestSocketFileTransfer {

	private static Logger log = Logger.getLogger(TestSocketFileTransfer.class);

	private Mockery context = new JUnit4Mockery();

	private UserId userid2 = new MockUserId("foo@bar.com");

	private UserId userid1 = new MockUserId("baz@bar.com");

	private SimpleSocketFileTransferFactory sftf = new SimpleSocketFileTransferFactory();

	private String filename = "myfile.txt";

	private FileRequest outrequest = new FileRequest(this.filename, false,
			this.userid2);

	private FileRequest inrequest = new FileRequest(this.filename, true,
			this.userid1);

	private SimpleFakeMessageExchanger msgX = new SimpleFakeMessageExchanger();

	private Tracer t = new Tracer();

	private String content = "\u4323Test<xml>>fdspf<ycs<YY>or not...";

	@Before
	public void setUp() throws Exception {
		t = new Tracer();
	}

	private IFileTransfer fileTransfer;

	@Test
	public void testSocketMethod_accepting() {
		IMsgService msg = msgX.addUser(userid1);
		createFriendlyPeer(t, userid2);

		ITransferMethod tfm;
		try {
			tfm = sftf.getTransferMethod(msg, userid1);
		} catch (NotLoggedInException e1) {
			Assert.fail();
			return;
		}
		tfm.request(this.outrequest, new INegotiationSuccessListener() {

			@Override
			public void failed() {
				t.step("clientside negotiation failed");
			}

			@Override
			public void succeeded(IFileTransfer ft) {
				t.step("clientside negotiation succeeded");
				Assert.assertEquals(filename, ft.getFileName());
				TestSocketFileTransfer.this.fileTransfer = ft;
			}
		});

		Assert.assertTrue(this.t.awaitStep("clientside negotiation succeeded",
				1, TimeUnit.SECONDS));

		while (!this.fileTransfer.isDone()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

		Assert.assertTrue(this.t.isDone(100, TimeUnit.MILLISECONDS));
	}

	private void createFriendlyPeer(final Tracer t, UserId userid2) {
		IMsgService msg = msgX.addUser(userid2);
		try {
			msg.registerReceiveMessageListener(new IMessageReceiveListener() {

				@Override
				public void receivedMessage(UserId from_userid, String content) {
					log.info(from_userid + ": " + content);
				}
			});
		} catch (NotLoggedInException e) {
			Assert.fail();
		}
		ITransferMethod tfm;
		try {
			tfm = sftf.getTransferMethod(msg, userid2);
		} catch (NotLoggedInException e1) {
			Assert.fail();
			return;
		}
		try {
			tfm.startServing(new IncomingTransferListener() {

				@Override
				public boolean accept(FileRequest req) {
					Assert.assertEquals(inrequest, req);
					t.step("accepted request");
					return true;
				}

				@Override
				public void started(IFileTransfer ft) {
					t.step("started on server");
					Assert.assertEquals(inrequest, ft.getFileRequest());
				}
			}, new FileRequestFileMapper() {

				@Override
				public File getFileForRequest(FileRequest r) {
					Assert.assertEquals(filename, r.getFileName());
					t.step("getting content from fs");
					try {
						File tmp = File.createTempFile("my", "tempfile");
						tmp.deleteOnExit();
						FileOutputStream fos;
						fos = new FileOutputStream(tmp);
						new BufferedOutputStream(fos).write(content.getBytes());
						fos.close();
						return tmp;
					} catch (FileNotFoundException e) {
						log.debug(e);
						return null;
					} catch (IOException e) {
						log.debug(e);
						return null;
					}
				}

			});
		} catch (NotLoggedInException e) {
			Assert.fail();
		}
	}

}
