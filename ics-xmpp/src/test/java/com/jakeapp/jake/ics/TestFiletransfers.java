package com.jakeapp.jake.ics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import local.test.Tracer;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.junit.ext.Prerequisite;
import com.googlecode.junit.ext.PrerequisiteAwareClassRunner;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.AdditionalFileTransferData;
import com.jakeapp.jake.ics.filetransfer.FileRequestFileMapper;
import com.jakeapp.jake.ics.filetransfer.ITransferListener;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.TransferWatcherThread;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.test.XmppTestEnvironment;


@RunWith(PrerequisiteAwareClassRunner.class)
public class TestFiletransfers {

	private static final Logger log = Logger.getLogger(TestFiletransfers.class);

	private ICService user1;

	private ICService user2;

	private ITransferMethod transfers1;

	private ITransferMethod transfers2;

	private static XmppUserId testUser1 = new XmppUserId(XmppTestEnvironment
			.getXmppId("testuser1"));

	private static String testUser1Passwd = "testpasswd1";

	private static XmppUserId testUser2 = new XmppUserId(XmppTestEnvironment
			.getXmppId("testuser2"));

	private static String testUser2Passwd = "testpasswd2";

	private static String testnamespace = "mynamespace";

	private static String testgroupname = "mygroupname";

	private IFileTransfer ftclient;

	private IFileTransfer ftserver;

	@Before
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void setUp() throws Exception {
		XmppTestEnvironment.assureUserIdExists(testUser1, testUser1Passwd);
		XmppTestEnvironment.assureUserIdExists(testUser2, testUser2Passwd);

		this.user1 = new XmppICService(testnamespace, testgroupname);
		Assert
				.assertTrue(this.user1.getStatusService().login(testUser1,
						testUser1Passwd));

		ITransferMethodFactory t1 = this.user1.getTransferMethodFactory();
		Assert.assertNotNull(t1);
		Assert.assertTrue(this.user1.getStatusService().isLoggedIn());
		this.transfers1 = t1.getTransferMethod(this.user1.getMsgService(), testUser1);

		this.user2 = new XmppICService(testnamespace, testgroupname);
		Assert
				.assertTrue(this.user2.getStatusService().login(testUser2,
						testUser2Passwd));
		ITransferMethodFactory t2 = this.user2.getTransferMethodFactory();
		Assert.assertNotNull(t2);
		Assert.assertTrue(this.user2.getStatusService().isLoggedIn());
		this.transfers2 = t2.getTransferMethod(this.user2.getMsgService(), testUser2);

		Assert.assertTrue(this.user2.getStatusService().isLoggedIn());
		Assert.assertTrue(this.user1.getStatusService().isLoggedIn());
		Assert.assertTrue(this.user2.getStatusService().isLoggedIn());

		this.ftclient = null;
	}

	@After
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void teardown() throws Exception {
		if (this.user1 != null)
			this.user1.getStatusService().logout();
		if (this.user2 != null)
			this.user2.getStatusService().logout();
		XmppTestEnvironment.assureUserDeleted(testUser1, testUser1Passwd);
		XmppTestEnvironment.assureUserDeleted(testUser2, testUser2Passwd);
	}

	@Test
	@Prerequisite(checker = XmppTestEnvironment.class)
	public void testReceiveSend() throws Exception {
		final String filename = "foo.txt";
		final Tracer t = new Tracer();
		String content = "Hello dummy!\n\naren't you a cute boy ...\n";
		final File file = getTempFile(content);
		final FileRequest request = new FileRequest(filename, false, testUser1);

		setupServer(filename, t, file);

		transfers2.request(request, new INegotiationSuccessListener() {

			@Override
			public void failed(Throwable reason) {
				Assert.fail();
				t.step("negotiation failed");
			}

			@Override
			public void succeeded(IFileTransfer ft) {
				ftclient = ft;
				Assert.assertTrue(ftclient.isReceiving());
				t.step("negotiation succeeded");
			}

		});

		Assert.assertTrue(this.user1.getStatusService().isLoggedIn());
		Assert.assertTrue(this.user2.getStatusService().isLoggedIn());

		Assert
				.assertTrue(t.await("accept, serving started", 3000,
						TimeUnit.MILLISECONDS));
		Assert.assertFalse(ftserver.isReceiving());
		Assert.assertEquals(ftserver.getFileName(), filename);
		Assert.assertEquals(ftserver.getLocalFile(), file);

		new Thread(new TransferWatcherThread(ftserver, new ITransferListener() {

			@Override
			public void onFailure(AdditionalFileTransferData transfer, String error) {
				t.step("server transmission failed");
				Assert.fail(error);
			}

			@Override
			public void onSuccess(AdditionalFileTransferData transfer) {
				log.info("success");
				t.step("server transmission done");
			}

			@Override
			public void onUpdate(AdditionalFileTransferData transfer, Status status,
					double progress) {
				log.info("update: " + status + " - " + progress);
			}

		})).start();

		Assert.assertTrue(t.await("mapper", 1000, TimeUnit.MILLISECONDS));
		Assert.assertTrue(t.await("negotiation succeeded", 1000, TimeUnit.MILLISECONDS));
		while (!ftclient.isDone() || !ftserver.isDone()) {
			log.debug("server filetransfer status: " + ftserver.getStatus() + " - "
					+ ftserver.getProgress());
			log.debug("client filetransfer status: " + ftclient.getStatus() + " - "
					+ ftserver.getProgress());
			Thread.sleep(100);
		}
		Assert.assertEquals(ftserver.getAmountWritten(), file.length());
		Assert.assertEquals(ftclient.getAmountWritten(), file.length());

		Assert.assertEquals(file.length(), ftserver.getLocalFile().length());
		Assert.assertEquals(file.length(), ftclient.getLocalFile().length());
		Assert.assertEquals(content.trim(), getFileContent(ftclient.getLocalFile())
				.trim());
	}

	private void setupServer(final String filename, final Tracer t, final File file)
			throws NotLoggedInException {
		transfers1.startServing(new IncomingTransferListener() {

			@Override
			public boolean accept(FileRequest req) {
				t.step("accept");
				return true;
			}

			@Override
			public void started(IFileTransfer ft) {
				ftserver = ft;
				t.step("serving started");
			}

		}, new FileRequestFileMapper() {

			@Override
			public File getFileForRequest(FileRequest r) {
				Assert.assertEquals(filename, r.getFileName());
				Assert.assertEquals(testUser2, r.getPeer());
				t.step("mapper");
				return file;
			}

		});
	}

	private File getTempFile(String content) throws IOException {
		File f = File.createTempFile("foobar", "baz");
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.append(content);
		bw.flush();
		Assert.assertEquals(f.length(), content.length());
		return f;
	}

	private String getFileContent(File f) throws IOException {
		char[] content = new char[1000];
		new BufferedReader(new FileReader(f)).read(content);
		return new String(content);
	}
}
