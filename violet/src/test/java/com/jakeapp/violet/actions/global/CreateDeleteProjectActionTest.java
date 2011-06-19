package com.jakeapp.violet.actions.global;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.status.IStatusService;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.gui.Projects;
import com.jakeapp.violet.model.User;

public class CreateDeleteProjectActionTest {

	private CreateDeleteProjectAction action;

	ProjectDir dir = new ProjectDir("this/is/mydir");

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCreate() throws Exception {
		Projects projects = Mockito.mock(Projects.class);

		DI.register(Projects.class, projects);
		action = new CreateDeleteProjectAction(dir, false);

		AvailableLaterWaiter.await(action);
		Mockito.verify(projects).add(dir);
		Mockito.verifyNoMoreInteractions(projects);
	}

	@Test
	public void testDelete() throws Exception {
		Projects projects = Mockito.mock(Projects.class);

		DI.register(Projects.class, projects);
		action = new CreateDeleteProjectAction(dir, true);

		AvailableLaterWaiter.await(action);
		Mockito.verify(projects).remove(dir);
		Mockito.verifyNoMoreInteractions(projects);
	}

}
