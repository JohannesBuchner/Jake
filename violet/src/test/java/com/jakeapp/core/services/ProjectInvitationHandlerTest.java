package com.jakeapp.core.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.UUID;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.invites.IProjectInvitationListener;
import com.jakeapp.violet.protocol.invites.ProjectInvitationHandler;

public class ProjectInvitationHandlerTest {

	private ProjectInvitationHandler projectInvitationHandler;

	private UserId other = DI.getUserId("someone@localhost");

	private IProjectInvitationListener projectInvitationListener;

	private UUID projectid = new UUID(31, 124);

	private String projectname = "myproject";

	@Before
	public void setup() {
		projectInvitationListener = Mockito
				.mock(IProjectInvitationListener.class);

		projectInvitationHandler = new ProjectInvitationHandler();
		projectInvitationHandler
				.registerInvitationListener(projectInvitationListener);
	}

	@Test
	public void testIncomingInviteMessage() throws Exception {
		projectInvitationHandler.receivedMessage(other,
				"<invite/>" + projectid.toString() + projectname);
		verify(projectInvitationListener, times(1)).invited(
				Mockito.eq(new User(other.getUserId())),
				Mockito.eq(projectname), Mockito.eq(projectid));
		Mockito.verifyNoMoreInteractions(projectInvitationListener);
	}

}
