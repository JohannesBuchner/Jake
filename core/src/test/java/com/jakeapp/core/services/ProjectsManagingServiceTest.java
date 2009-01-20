package com.jakeapp.core.services;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jakeapp.core.util.ApplicationContextFactory;

public class ProjectsManagingServiceTest {
	IProjectsManagingService service;

	@Before
	public void setUp() throws Exception {
		ApplicationContextFactory appc;
		service = new ProjectsManagingServiceImpl();
		//TODO add dao
		//((ProjectsManagingServiceImpl) service).setProjectDao(null); 
	}

	@After
	public void tearDown() throws Exception {
	}


    @Test
    public void doNothing()
    {
        // empty test to prevent initializationError. // TODO CHRISTOPHER: Please do this yourself!!! NOT ALWAYS ME! :)
    }

	/*
	@Test(timeout = TestingConstants.UNITTESTTIME,expected = IllegalArgumentException.class)
	public void createProject_shouldFailWithNullName() throws FileNotFoundException, IllegalArgumentException  {
		this.service.createProject(null, "~/jakeproject", null);
	}
	
	@Test(timeout = TestingConstants.UNITTESTTIME,expected = IllegalArgumentException.class)
	public void createProject_shouldFailWithNullPath() throws FileNotFoundException, IllegalArgumentException  {
		this.service.createProject("neuesProjekt", null, null);
	}
	*/
}
