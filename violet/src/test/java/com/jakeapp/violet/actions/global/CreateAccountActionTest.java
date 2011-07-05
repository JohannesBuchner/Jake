package com.jakeapp.violet.actions.global;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.status.IStatusService;
import com.jakeapp.violet.model.User;

public class CreateAccountActionTest {

	private String pw = "secret";

	private User user = new User("me@localhost");

	private CreateAccountAction action;

	private MockUserIdFactory userids = new MockUserIdFactory();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCreate() throws Exception {
		ICService ics = Mockito.mock(ICService.class);
		IStatusService status = Mockito.mock(IStatusService.class);
		Mockito.when(ics.getStatusService()).thenReturn(status);

		action = new CreateAccountAction(user, pw);
		action.setIcs(ics);
		action.setUserids(userids);

		AvailableLaterWaiter.await(action);
		Mockito.verify(status).createAccount(userids.get(user.getUserId()), pw);
		Mockito.verifyNoMoreInteractions(status);
	}

	@Test(expected = NetworkException.class)
	public void testCreateFails() throws Exception {
		ICService ics = Mockito.mock(ICService.class);
		IStatusService status = Mockito.mock(IStatusService.class);
		Mockito.doThrow(new NetworkException("I don't like your name"))
				.when(status)
				.createAccount((UserId) Mockito.any(), Mockito.eq(pw));
		Mockito.when(ics.getStatusService()).thenReturn(status);

		action = new CreateAccountAction(user, pw);
		action.setIcs(ics);
		action.setUserids(userids);

		AvailableLaterWaiter.await(action);
		Mockito.verifyNoMoreInteractions(status);
	}
}
