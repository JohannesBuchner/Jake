package com.jakeapp.core.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.StringBufferInputStream;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import local.test.Tracer;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.jake.fss.FSService;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.AdditionalFileTransferData;
import com.jakeapp.jake.ics.filetransfer.FailoverCapableFileTransferService;
import com.jakeapp.jake.ics.filetransfer.FileRequestFileMapper;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.ics.impl.mock.MockICService;
import com.jakeapp.jake.ics.impl.mock.MockUserId;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.FileTransfer;
import com.jakeapp.jake.ics.msgservice.IMsgService;
import com.jakeapp.jake.test.SimpleFakeMessageExchanger;
import com.jakeapp.jake.test.TmpdirEnabledTestCase;
import com.jakeapp.violet.actions.global.serve.ProjectRequestListener;
import com.jakeapp.violet.actions.project.interact.SimpleUserOrderStrategy;
import com.jakeapp.violet.actions.project.interact.pull.PullAction;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.msg.IMessageMarshaller;
import com.jakeapp.violet.protocol.msg.impl.MessageMarshaller;

public class PullTest extends TmpdirEnabledTestCase {

	private static final Logger log = Logger.getLogger(PullTest.class);

	private IMessageMarshaller messageMarshaller = new MessageMarshaller();

	private JakeObject fo = new JakeObject("/my/path.txt");

	private User member = new User("a@b");

	private LogEntry le = new LogEntry(null, null, member, fo, "blabla", "bla",
			true);

	@Mock
	private IFileTransferService fileTransferService;

	@Mock
	private ITransferMethod mockTransferMethod;

	private Tracer tracer;

	@Mock
	private ProjectModel model;

	ProjectRequestListener prl;

	@Before
	public void setup() throws Exception {
		super.setup();
		model = mock(ProjectModel.class);
		FSService fss = new FSService();
		fss.setRootPath(new ProjectDir(tmpdir));
		when(model.getFss()).thenReturn(fss);
		ICService icservice = new MockICService();
		icservice.getStatusService().login(DI.getUserId(member.getUserId()),
				member.toString(), "foo", 0L);
		when(model.getIcs()).thenReturn(icservice);
		Log log = mock(Log.class);
		when(model.getLog()).thenReturn(log);

		prl = new ProjectRequestListener(model, null);

		when(log.getLastOfJakeObject(fo, true)).thenReturn(le);
		when(log.getLastOfJakeObject(fo, false)).thenReturn(le);
		when(log.getLastOfJakeObject(fo, true)).thenReturn(le);
		when(log.getLastOfJakeObject(fo, false)).thenReturn(le);

		tracer = new Tracer();
	}

	private void responderSetup(final File file) throws Exception {
		mockTransferMethod = new ITransferMethod() {

			private final Logger log = Logger
					.getLogger("Mocked ITransferMethod");

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
			public ITransferMethod getTransferMethod(
					IMsgService negotiationService, UserId user) {
				return mockTransferMethod;
			}

		};
		SimpleFakeMessageExchanger sfme = new SimpleFakeMessageExchanger();
		MockUserId backendUser = new MockUserId(member.getUserId());
		model.getIcs().getStatusService()
				.login(backendUser, backendUser.getUserId(), null, 0);
		IMsgService msg = sfme.addUser(backendUser);
		fileTransferService = new FailoverCapableFileTransferService();
		fileTransferService.addTransferMethod(transferMethodFactory, msg,
				backendUser);

		when(model.getTransfer()).thenReturn(fileTransferService);
	}

	@Test
	public void pull_noIncomingFile() throws Exception {
		responderSetup(new File(PullTest.this.tmpdir, "fileDoesntExist"));

		Assert.assertNotNull(model.getLog().getLastOfJakeObject(fo, false));

		PullAction action = new PullAction(model, fo,
				new SimpleUserOrderStrategy());
		AvailableLaterWaiter.await(action);

		Assert.assertTrue(tracer.await("pullNegotiationDone", 10,
				TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());
		Assert.assertFalse(model.getFss().fileExists(fo.getRelPath()));
	}

	@Test
	public void pull() throws Exception {
		File tmpfile = new File(PullTest.this.tmpdir, "myOutputFile");

		@SuppressWarnings("deprecation")
		StringBufferInputStream sbis = new StringBufferInputStream("Foo bar");

		FSService.writeFileStreamAbs(tmpfile, sbis);
		Assert.assertTrue(tmpfile.length() > 6);

		responderSetup(tmpfile);

		Assert.assertNotNull(model.getLog().getLastOfJakeObject(fo, true));

		PullAction action = new PullAction(model, fo,
				new SimpleUserOrderStrategy());

		AvailableLaterWaiter.await(action);
		Assert.assertTrue(tracer.await("pullNegotiationDone", 10,
				TimeUnit.MILLISECONDS));
		Assert.assertTrue(tracer.isDone());

		Assert.assertTrue(model.getFss().fileExists(fo.getRelPath()));
		Assert.assertEquals(tmpfile.length(),
				model.getFss().getFileSize(fo.getRelPath()));

	}

}
