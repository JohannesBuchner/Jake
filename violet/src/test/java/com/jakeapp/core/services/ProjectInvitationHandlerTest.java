package com.jakeapp.core.services;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.violet.actions.global.MockUserIdFactory;
import com.jakeapp.violet.di.IUserIdFactory;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.invites.IProjectInvitationListener;
import com.jakeapp.violet.protocol.invites.ProjectInvitationHandler;

public class ProjectInvitationHandlerTest {

	private ProjectInvitationHandler projectInvitationHandler;


	private IUserIdFactory userids = new MockUserIdFactory();

	private UserId other = userids.get("someone@localhost");

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
