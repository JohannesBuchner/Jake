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
import com.jakeapp.jake.ics.filetransfer.FailoverCapableFileTransferService;
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
import com.jakeapp.jake.ics.impl.sockets.filetransfer.SimpleSocketFileTransferFactory;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;


@RunWith(PrerequisiteAwareClassRunner.class)
public class TestFailoverTransfers {

	private static final Logger log = Logger.getLogger(TestFailoverTransfers.class);

	private static final int SOCKET_TIMEOUT_SECONDS = 5;

	private ICService user1;

	private ICService user2;

	private static XmppUserId testUser1 = new XmppUserId(TestEnvironment
			.getXmppId("testuser1"));

	private static String testUser1Passwd = "testpasswd1";

	private static XmppUserId testUser2 = new XmppUserId(TestEnvironment
			.getXmppId("testuser2"));

	private static String testUser2Passwd = "testpasswd2";

	private static String testnamespace = "mynamespace";

	private static String testgroupname = "mygroupname";

	private IFileTransfer ftclient;

	private IFileTransfer ftserver;

	private FailoverCapableFileTransferService failover1;

	private FailoverCapableFileTransferService failover2;

	private ITransferMethodFactory xmppTransfer2;

	private ITransferMethodFactory xmppTransfer1;

	@Before
	@Prerequisite(checker = TestEnvironment.class)
	public void setUp() throws Exception {
		TestEnvironment.assureUserIdExists(testUser1, testUser1Passwd);
		TestEnvironment.assureUserIdExists(testUser2, testUser2Passwd);

		this.user1 = new XmppICService(testnamespace, testgroupname);
		Assert
				.assertTrue(this.user1.getStatusService().login(testUser1,
						testUser1Passwd));

		this.xmppTransfer1 = this.user1.getTransferMethodFactory();
		Assert.assertNotNull(this.xmppTransfer1);
		Assert.assertTrue(this.user1.getStatusService().isLoggedIn());
		this.failover1 = new FailoverCapableFileTransferService();

		this.user2 = new XmppICService(testnamespace, testgroupname);
		Assert
				.assertTrue(this.user2.getStatusService().login(testUser2,
						testUser2Passwd));
		this.xmppTransfer2 = this.user2.getTransferMethodFactory();
		Assert.assertNotNull(this.xmppTransfer2);
		Assert.assertTrue(this.user2.getStatusService().isLoggedIn());
		this.failover2 = new FailoverCapableFileTransferService();

		Assert.assertTrue(this.user2.getStatusService().isLoggedIn());
		Assert.assertTrue(this.user1.getStatusService().isLoggedIn());
		Assert.assertTrue(this.user2.getStatusService().isLoggedIn());

		this.ftclient = null;
	}

	@After
	@Prerequisite(checker = TestEnvironment.class)
	public void teardown() throws Exception {
		this.failover1.stopServing();
		this.failover2.stopServing();
		if (this.user1 != null)
			this.user1.getStatusService().logout();
		if (this.user2 != null)
			this.user2.getStatusService().logout();
		TestEnvironment.assureUserDeleted(testUser1, testUser1Passwd);
		TestEnvironment.assureUserDeleted(testUser2, testUser2Passwd);
	}

	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testNothing() throws Exception {

	}

	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testReceiveSend_BothSocketsOk() throws Exception {
		testReceiveSend(true, true);
	}

	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testReceiveSend_ServerSocketsOk() throws Exception {
		testReceiveSend(false, true);
	}

	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testReceiveSend_ClientSocketsOk() throws Exception {
		testReceiveSend(true, false);
	}

	@Test
	@Prerequisite(checker = TestEnvironment.class)
	public void testReceiveSend_NoSocketsOk() throws Exception {
		testReceiveSend(false, false);
	}

	public void testReceiveSend(boolean enableSockets, boolean enableServerSockets)
			throws Exception {
		if (enableSockets)
			this.failover2.addTransferMethod(new SimpleSocketFileTransferFactory(
					SOCKET_TIMEOUT_SECONDS), this.user2.getMsgService(), testUser2);
		this.failover2.addTransferMethod(this.xmppTransfer2, this.user2.getMsgService(),
				testUser2);

		final String filename = "foo.txt";
		final Tracer t = new Tracer();
		String content = "Hello dummy!\n\naren't you a cute boy ...\n";
		final File file = getTempFile(content);
		final FileRequest request = new FileRequest(filename, false, testUser1);

		setupServer(filename, t, file, enableServerSockets);

		this.failover2.request(request, new INegotiationSuccessListener() {

			@Override
			public void failed(Throwable reason) {
				Assert.fail();
				t.step("negotiation failed");
			}

			@Override
			public void succeeded(IFileTransfer ft) {
				ftclient = ft;
				log.debug("client fail-over reported success: " + ft);
				log.debug("receiving? " + ftclient.isReceiving());
				Assert.assertTrue(ftclient.isReceiving());
				t.step("negotiation succeeded");
			}

		});

		Assert.assertTrue(this.user1.getStatusService().isLoggedIn());
		Assert.assertTrue(this.user2.getStatusService().isLoggedIn());
		int additionalTime = 2000;
		if (!enableServerSockets || !enableSockets)
			additionalTime = SOCKET_TIMEOUT_SECONDS * 1000 * 2;

		Assert.assertTrue(t.await("accept, serving started", 3000 + additionalTime,
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
		Assert.assertTrue(t.await("negotiation succeeded", 2000 + additionalTime,
				TimeUnit.MILLISECONDS));
		while (!ftclient.isDone() || !ftserver.isDone()) {
			log.debug("server filetransfer status: " + ftserver.getStatus() + " - "
					+ ftserver.getProgress());
			log.debug("client filetransfer status: " + ftclient.getStatus() + " - "
					+ ftserver.getProgress());
			Thread.sleep(100);
		}
		Assert.assertEquals(file.length(), ftserver.getAmountWritten());
		Assert.assertEquals(file.length(), ftclient.getAmountWritten());

		Assert.assertEquals(file.length(), ftserver.getLocalFile().length());
		Assert.assertEquals(file.length(), ftclient.getLocalFile().length());
		Assert.assertEquals(content.trim(), getFileContent(ftclient.getLocalFile())
				.trim());
	}

	private void setupServer(final String filename, final Tracer t, final File file,
			boolean enableSockets) throws NotLoggedInException {

		if (enableSockets)
			this.failover1.addTransferMethod(new SimpleSocketFileTransferFactory(
					SOCKET_TIMEOUT_SECONDS), this.user1.getMsgService(), testUser1);
		this.failover1.addTransferMethod(xmppTransfer1, this.user1.getMsgService(),
				testUser1);
		failover1.startServing(new IncomingTransferListener() {

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
