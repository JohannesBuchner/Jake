package com.jakeapp.violet.actions.global;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.violet.gui.Projects;

public class CreateDeleteProjectActionTest {

	private CreateDeleteProjectAction action;

	ProjectDir dir = new ProjectDir("this/is/mydir");

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCreate() throws Exception {
		Projects projects = Mockito.mock(Projects.class);

		action = new CreateDeleteProjectAction(dir, false);
		action.setProjects(projects);

		AvailableLaterWaiter.await(action);
		Mockito.verify(projects).add(dir);
		Mockito.verifyNoMoreInteractions(projects);
	}

	@Test
	public void testDelete() throws Exception {
		Projects projects = Mockito.mock(Projects.class);

		action = new CreateDeleteProjectAction(dir, true);
		action.setProjects(projects);

		AvailableLaterWaiter.await(action);
		Mockito.verify(projects).remove(dir);
		Mockito.verifyNoMoreInteractions(projects);
	}

}
