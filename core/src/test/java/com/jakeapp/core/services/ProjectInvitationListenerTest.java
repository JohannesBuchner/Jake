package com.jakeapp.core.services;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.logentries.ProjectJoinedLogEntry;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import junit.framework.Assert;

import java.util.UUID;


public class ProjectInvitationListenerTest {


	private IProjectInvitationListener projectInvitationListener;


	@Mock
	private IProjectDao projectDao;

	@Mock
	private ProjectApplicationContextFactory contextFactory;

	@Mock
	private ILogEntryDao logEntryDao;

	private ProjectInvitationHandler projectInvitationHandler;
	private ServiceCredentials credentials = new ServiceCredentials("user", "pass", ProtocolType.XMPP);


	UserId user = new UserId(ProtocolType.XMPP, "testuser1@localhost");
	Project project;


	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		projectInvitationListener = new ProjectInvitationListener(projectDao, contextFactory);

		project = new Project("testproject1", UUID.fromString("8a488840-cbdc-43d2-9c52-3bca07bcead2"), null, null);

//		projectInvitationHandler = new ProjectInvitationHandler(msg);
//		projectInvitationHandler.registerInvitationListener(projectInvitationListener);
	}

	@After
	public void tearDown() {

	}

	public ProjectInvitationListenerTest() {

	}

	@Test
	public void testIncomingInviteMessage() throws Exception {


		projectInvitationListener.invited(user, project);
		verify(projectDao, times(1)).create(project);
	}

	ProjectJoinedLogEntry resultLogEntry;

	@Test
	public void testIncomingAcceptMessage() throws Exception {

		ProjectJoinedLogEntry logEntry = new ProjectJoinedLogEntry(project, user);


		when(contextFactory.getUnprocessedAwareLogEntryDao(project)).thenReturn(logEntryDao);


//		stubVoid(logEntryDao).toAnswer(new Answer(){
//			@Override
//			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
//				System.out.println("ANWERING!");
//				ProjectInvitationListenerTest.resultLogEntry = (ProjectJoinedLogEntry) invocationOnMock.getArguments()[0];
//				for(Object o : invocationOnMock.getArguments())
//				{
//					System.out.println("o = " + o);
//				}
//				return null;
//			}))
//
//
//			;

		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
				resultLogEntry = (ProjectJoinedLogEntry) invocationOnMock.getArguments()[0];
				return null;
			}
		}).when(logEntryDao).create((LogEntry) anyObject());
		projectInvitationListener.accepted(user, project);

		System.out.println("resultLogEntry = " + resultLogEntry);

		verify(logEntryDao, times(1)).create(resultLogEntry);

		Assert.assertEquals(logEntry.getProject(), resultLogEntry.getProject());
		Assert.assertEquals(logEntry.getLogAction(), resultLogEntry.getLogAction());
		Assert.assertEquals(logEntry.getBelongsTo(), resultLogEntry.getBelongsTo());
		Assert.assertEquals(logEntry.getChecksum(), resultLogEntry.getChecksum());
		Assert.assertEquals(logEntry.getMember(), resultLogEntry.getMember());
		Assert.assertEquals(logEntry.getObjectuuid(), resultLogEntry.getObjectuuid());
		Assert.assertEquals(logEntry.isProcessed(), resultLogEntry.isProcessed());

		// TODO domdorn: check other logentries as well


// this cannot be predicted!
//		Assert.assertEquals(logEntry.getUuid(), resultLogEntry.getUuid());
//		Assert.assertEquals(logEntry.getTimestamp(), resultLogEntry.getTimestamp());
	}

	@Test
	public void testIncomingRejectMessage() throws Exception {


		projectInvitationListener.rejected(user, project);
		// TODO

//		when(msg.getIcsManager().getFrontendUserId(null, null)).thenReturn(user);
//		when(msg.getProtocolType()).thenReturn(ProtocolType.XMPP);
//
//		projectInvitationHandler.receivedMessage(new XmppUserId(user.getUserId() +
//				"/" + project.getProjectId().toString()),
//				"<reject/>" + project.getProjectId().toString() + project.getName());
//
//
//		verify(projectInvitationListener, times(1)).rejected(user,project);
	}

}
