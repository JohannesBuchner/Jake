package com.jakeapp.core.services;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Ignore;
import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.domain.*;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

import java.util.UUID;


public class ProjectInvitationHandlerTest {


	@Mock
	private IProjectInvitationListener projectInvitationListener;

	@Mock
	private XMPPMsgService msg;

	@Mock
	private ICSManager icsManager;

	@Mock
	private IProjectDao projectDao;

	private ProjectInvitationHandler projectInvitationHandler;
	private ServiceCredentials credentials = new ServiceCredentials("user", "pass", ProtocolType.XMPP);


	UserId user = new UserId(ProtocolType.XMPP, "testuser1@localhost");
	Project project;


	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		project = new Project("testproject1", UUID.fromString("8a488840-cbdc-43d2-9c52-3bca07bcead2"), msg, null);

		projectInvitationHandler = new ProjectInvitationHandler(msg);
		projectInvitationHandler.registerInvitationListener(projectInvitationListener);
	}

	@After
	public void tearDown() {

	}

	public ProjectInvitationHandlerTest() {

	}

	@Test
	public void testIncomingInviteMessage() throws Exception {
		XmppUserId xmppUserId = new XmppUserId(
				user.getUserId() +
						"/" + project.getProjectId().toString());

		when(msg.getIcsManager()).thenReturn(icsManager);
		when(icsManager.getFrontendUserId(project, xmppUserId)).thenReturn(user);

		projectInvitationHandler.receivedMessage(
				xmppUserId,
				"<invite/>" + project.getProjectId().toString() + project.getName());
		verify(projectInvitationListener, times(1)).invited(user, project);
	}


	@Ignore
	public void testIncomingAcceptMessage() throws Exception {
		XmppUserId xmppUserId = new XmppUserId(
				user.getUserId() +
						"/" + project.getProjectId().toString());

		when(msg.getIcsManager()).thenReturn(icsManager);
		when(icsManager.getFrontendUserId(project, xmppUserId)).thenReturn(user);

		projectInvitationHandler.receivedMessage(
				xmppUserId,
				"<accept/>" + project.getProjectId().toString() + project.getName());
		verify(projectInvitationListener, times(1)).accepted(user, project);


	}

	@Test
	public void testIncomingRejectMessage() throws Exception {
		XmppUserId xmppUserId = new XmppUserId(
				user.getUserId() +
						"/" + project.getProjectId().toString());
		                            
		when(msg.getIcsManager()).thenReturn(icsManager);
		when(icsManager.getFrontendUserId(project, xmppUserId)).thenReturn(user);

		projectInvitationHandler.receivedMessage(
				xmppUserId,
				"<reject/>" + project.getProjectId().toString() + project.getName());
		verify(projectInvitationListener, times(1)).rejected(user, project);
	}


}
