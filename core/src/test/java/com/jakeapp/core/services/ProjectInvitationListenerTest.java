package com.jakeapp.core.services;

import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import junit.framework.Assert;

import java.util.UUID;


public class ProjectInvitationListenerTest {

	/*
	@Mock private IProjectInvitationListener projectInvitationListener;

	@Mock private XMPPMsgService msg;

	@Mock private ICSManager icsManager;

	@Mock private IProjectDao projectDao;

	private ProjectInvitationHandler projectInvitationHandler;
	private ServiceCredentials credentials = new ServiceCredentials("user", "pass", ProtocolType.XMPP);


	UserId user = new UserId(ProtocolType.XMPP, "testuser1@localhost");
	Project project;



	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		when(msg.getIcsManager()).thenReturn(icsManager);
//		when(msg.getServiceCredentials()).thenReturn(credentials);
		
		project = new Project("testproject1", UUID.fromString("8a488840-cbdc-43d2-9c52-3bca07bcead2"), msg, null);

//		projectInvitationHandler = new ProjectInvitationHandler(msg);
//		projectInvitationHandler.setInvitationListener(projectInvitationListener);
	}

	@After
	public void tearDown()
	{

	}

	public ProjectInvitationListenerTest() {

	}

	@Test
	public void testIncomingInviteMessage() throws Exception {
//		ServiceCredentials cred = new ServiceCredentials(id, password, ProtocolType.XMPP);
//		MsgService msg = frontend.addAccount(sessionId, cred);

		when(msg.getIcsManager().getFrontendUserId(null, null)).thenReturn(user);
//		when(msg.getProtocolType()).thenReturn(ProtocolType.XMPP);


		projectInvitationHandler.receivedMessage(new XmppUserId( user.getUserId() +
				"/" + project.getProjectId().toString()),
				"<invite/>"+ project.getProjectId().toString() + project.getName() );
		verify(projectInvitationListener, times(1)).invited(user,project);
	}
	@Test
	public void testIncomingAcceptMessage() throws Exception {

		when(msg.getIcsManager().getFrontendUserId(null, null)).thenReturn(user);
		when(msg.getProtocolType()).thenReturn(ProtocolType.XMPP);


		projectInvitationHandler.receivedMessage(new XmppUserId( user.getUserId() +
				"/" + project.getProjectId().toString()),
				"<accept/>"+ project.getProjectId().toString() + project.getName() );


		verify(projectInvitationListener, times(1)).accepted(user,project);

	}
	@Test
	public void testIncomingRejectMessage() throws Exception {

		when(msg.getIcsManager().getFrontendUserId(null, null)).thenReturn(user);
		when(msg.getProtocolType()).thenReturn(ProtocolType.XMPP);

		projectInvitationHandler.receivedMessage(new XmppUserId(user.getUserId() +
				"/" + project.getProjectId().toString()),
				"<reject/>" + project.getProjectId().toString() + project.getName());


		verify(projectInvitationListener, times(1)).rejected(user,project);
	}

	*/
}
