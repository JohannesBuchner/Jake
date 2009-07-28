package com.jakeapp.core.services;

import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.logentries.JakeObjectNewVersionLogEntry;
import com.jakeapp.core.synchronization.ISyncService;
import com.jakeapp.core.synchronization.SyncServiceImpl;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.core.synchronization.helpers.MessageMarshaller;
import com.jakeapp.core.synchronization.request.TrustAllRequestHandlePolicy;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.util.UnprocessedBlindLogEntryDaoProxy;
import com.jakeapp.jake.fss.FSService;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.*;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.ics.impl.mock.MockICService;
import com.jakeapp.jake.ics.impl.mock.MockUserId;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.FileTransfer;
import com.jakeapp.jake.ics.msgservice.IMsgService;
import com.jakeapp.jake.test.FSTestCommons;
import com.jakeapp.jake.test.SimpleFakeMessageExchanger;
import junit.framework.Assert;
import local.test.Tracer;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.StringBufferInputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class PullTest {


		protected File tmpdir;






	private final class TracingChangeListener implements ChangeListener {

		@Override
		public INegotiationSuccessListener beganRequest(JakeObject jo) {
			tracer.step("beganRequest");
			return null;
		}

		@Override
		public void pullDone(JakeObject jo) {
			tracer.step("pullDone");
		}

		@Override
		public void pullFailed(JakeObject jo, Throwable reason) {
			tracer.step("pullFailed");
		}

		@Override
		public void pullNegotiationDone(JakeObject jo) {
			tracer.step("pullNegotiationDone");
		}

		@Override
		public void pullProgressUpdate(JakeObject jo, Status status, double progress) {
			tracer.step("pullProgressUpdate");
		}

		@Override public void onlineStatusChanged(Project p) {
			tracer.step("onlineStatusChanged");
		}

		@Override public void syncStateChanged(Project p, SyncState state) {
			tracer.step("syncStateChanged");
		}
	}

	private static final Logger log = Logger.getLogger(PullTest.class);

	private ISyncService sync;

	@Mock
	private ICSManager icsmanager;

	@Mock
	private MessageMarshaller messageMarshaller;

	@Mock
	private ProjectApplicationContextFactory projectApplicationContextFactory;

	@Mock
	private IProjectsFileServices projectsFileServices;

	@Mock
	private MsgService msgService;

	private Project project = new Project("myProject", new UUID(4, 2), null, tmpdir);

	private FileObject fo = new FileObject(new UUID(2, 4), project, "/my/path.txt");

	@Mock
	private ILogEntryDao logEntryDao;

	@Mock
	private IFileObjectDao fileObjectDao;

	private UnprocessedBlindLogEntryDaoProxy ublogEntryDao;

	private User member = new User(ProtocolType.XMPP, "a@b");

	private JakeObjectNewVersionLogEntry le = new JakeObjectNewVersionLogEntry(fo,
			member, "blabla", "bla", true);

	private ICService icservice = new MockICService();

	private IFileTransferService fileTransferService;

	private ITransferMethod mockTransferMethod;

	private Tracer tracer;

	private IFSService fss;



	@After
	public void teardown() throws Exception {
		if (tmpdir.exists())
			Assert.assertTrue(FSTestCommons.recursiveDelete(tmpdir));
		Assert.assertFalse("Cleanup done", tmpdir.exists());
	}




	@Before
	public void setup() throws Exception {
		tmpdir = FSTestCommons.provideTempDir();

		MockitoAnnotations.initMocks(this);
		SyncServiceImpl sync = new SyncServiceImpl();
		sync.setICSManager(icsmanager);
		sync.setMessageMarshaller(new MessageMarshaller(new LogEntrySerializer(
				projectApplicationContextFactory)));
		sync.setApplicationContextFactory(projectApplicationContextFactory);
		sync.setProjectsFileServices(projectsFileServices);
		sync.setRequestHandlePolicy(new TrustAllRequestHandlePolicy(
				projectApplicationContextFactory, projectsFileServices));

		this.sync = sync;
		when(msgService.getIcsManager()).thenReturn(icsmanager);
		when(msgService.getVisibilityStatus()).thenReturn(VisibilityStatus.ONLINE);

		ublogEntryDao = new UnprocessedBlindLogEntryDaoProxy(logEntryDao);
		le.setUuid(new UUID(432, 3214));
		project.setMessageService(msgService);
		fo.setProject(project);
		
		// unfortunately, this doesn't work this way.
		// we would need a whenSimilarTo instead of when(). TODO .
		FileObject strippedFo = new FileObject(fo.getProject(), fo.getRelPath());

		when(projectApplicationContextFactory.getLogEntryDao(fo)).thenReturn(
				ublogEntryDao);
		when(projectApplicationContextFactory.getLogEntryDao(strippedFo)).thenReturn(
				ublogEntryDao);
		when(projectApplicationContextFactory.getLogEntryDao(project)).thenReturn(
				ublogEntryDao);
		when(projectApplicationContextFactory.getUnprocessedAwareLogEntryDao(fo))
				.thenReturn(logEntryDao);
		when(projectApplicationContextFactory.getUnprocessedAwareLogEntryDao(strippedFo))
		.thenReturn(logEntryDao);
		when(projectApplicationContextFactory.getUnprocessedAwareLogEntryDao(project))
				.thenReturn(logEntryDao);
		when(projectApplicationContextFactory.getFileObjectDao(project)).thenReturn(
				fileObjectDao);


		fss = new FSService();
		fss.setRootPath(tmpdir.getAbsolutePath());
		when(projectsFileServices.getProjectFSService(project)).thenReturn(fss);

		when(logEntryDao.getLastVersion(fo, true)).thenReturn(le);
		when(logEntryDao.getLastVersion(fo, false)).thenReturn(le);
		when(logEntryDao.getLastOfJakeObject(fo, true)).thenReturn(le);
		when(logEntryDao.getLastOfJakeObject(fo, false)).thenReturn(le);
		
		when(logEntryDao.getLastVersion(strippedFo, true)).thenReturn(le);
		when(logEntryDao.getLastVersion(strippedFo, false)).thenReturn(le);
		when(logEntryDao.getLastOfJakeObject(strippedFo, true)).thenReturn(le);
		when(logEntryDao.getLastOfJakeObject(strippedFo, false)).thenReturn(le);

		when(icsmanager.getICService(project)).thenReturn(icservice);
		tracer = new Tracer();

	}

	private void responderSetup(final File file) throws Exception {
		mockTransferMethod = new ITransferMethod() {

			private final Logger log = Logger.getLogger("Mocked ITransferMethod");

			@Override
			public void request(final FileRequest inrequest,
					INegotiationSuccessListener nsl) {
				log.debug("request: " + inrequest);

				log.debug("declaring success");
				FileTransfer ft = new FileTransfer() {

					{
						this.request = inrequest;
						this.localFile = file;
						this.request.setData(new AdditionalFileTransferData(
								this.localFile));
						this.status = Status.complete;
						this.amountWritten = inrequest.getFileSize();
					}

					@Override
					public Boolean isReceiving() {
						return true;
					}

				};
				log.debug("created transfer: " + ft);
				nsl.succeeded(ft);
				log.debug("declared success");
			}

			@Override
			public void startServing(IncomingTransferListener l,
					FileRequestFileMapper mapper) throws NotLoggedInException {

			}

			@Override
			public void stopServing() {

			}

		};

		ITransferMethodFactory transferMethodFactory = new ITransferMethodFactory() {

			@Override
			public ITransferMethod getTransferMethod(IMsgService negotiationService,
					UserId user) throws NotLoggedInException {
				return mockTransferMethod;
			}

		};
		SimpleFakeMessageExchanger sfme = new SimpleFakeMessageExchanger();
		MockUserId backendUser = new MockUserId(member.getUserId());
		icservice.getStatusService().login(backendUser, backendUser.getUserId(), null, 0);
		IMsgService msg = sfme.addUser(backendUser);
		fileTransferService = new FailoverCapableFileTransferService();
		fileTransferService.addTransferMethod(transferMethodFactory, msg, backendUser);

		when(icsmanager.getTransferService(project)).thenReturn(fileTransferService);
	}

	@Test
	public void pull_noIncomingFile() throws Exception {
		responderSetup(new File(PullTest.this.tmpdir, "fileDoesntExist"));


		Assert.assertNotNull(projectApplicationContextFactory.getLogEntryDao(fo)
				.getLastVersion(new FileObject(fo.getProject(), fo.getRelPath())));
		Assert.assertTrue(projectApplicationContextFactory.getLogEntryDao(fo)
				.getLastVersion(fo).getBelongsTo() instanceof FileObject);

		sync.startServing(project, new TracingChangeListener());
		Assert.assertNull(sync.pullObject(fo));
		Assert.assertTrue(tracer.await("pullNegotiationDone", 10, TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());
		Assert.assertFalse(fss.fileExists(fo.getRelPath()));
	}

	@Test
	public void pull() throws Exception {
		File tmpfile = new File(PullTest.this.tmpdir, "myOutputFile");

		@SuppressWarnings("deprecation")
		StringBufferInputStream sbis = new StringBufferInputStream("Foo bar");

		FSService.writeFileStreamAbs(tmpfile.getAbsolutePath(), sbis);
		Assert.assertTrue(tmpfile.length() > 6);

		responderSetup(tmpfile);

		Assert.assertNotNull(projectApplicationContextFactory.getLogEntryDao(fo)
				.getLastVersion(fo));
		Assert.assertTrue(projectApplicationContextFactory.getLogEntryDao(fo)
				.getLastVersion(fo).getBelongsTo() instanceof FileObject);

		sync.startServing(project, new TracingChangeListener());
		Assert.assertEquals(fo, sync.pullObject(fo));
		Assert.assertTrue(tracer.await("pullNegotiationDone", 10, TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());

		Assert.assertTrue(fss.fileExists(fo.getRelPath()));
		Assert.assertEquals(tmpfile.length(), fss.getFileSize(fo.getRelPath()));

	}

}
