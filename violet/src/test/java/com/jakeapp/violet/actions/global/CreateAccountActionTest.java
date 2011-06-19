package com.jakeapp.violet.actions.global;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.status.IStatusService;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.User;

public class CreateAccountActionTest {

	private String pw = "secret";

	private User user = new User("me@localhost");

	private CreateAccountAction action;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCreate() throws Exception {
		ICService ics = Mockito.mock(ICService.class);
		IStatusService status = Mockito.mock(IStatusService.class);
		Mockito.when(ics.getStatusService()).thenReturn(status);

		DI.register(ICService.class, ics);
		action = new CreateAccountAction(user, pw);

		AvailableLaterWaiter.await(action);
		Mockito.verify(status)
				.createAccount(DI.getUserId(user.getUserId()), pw);
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

		DI.register(ICService.class, ics);
		action = new CreateAccountAction(user, pw);

		AvailableLaterWaiter.await(action);
		Mockito.verifyNoMoreInteractions(status);
	}
}
