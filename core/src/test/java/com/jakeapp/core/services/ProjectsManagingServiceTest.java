package com.jakeapp.core.services;


import java.io.FileNotFoundException;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jakeapp.TestingConstants;
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
	
	@Test(timeout = TestingConstants.UNITTESTTIME,expected = IllegalArgumentException.class)
	public void createProject_shouldFailWithNullName() throws FileNotFoundException, IllegalArgumentException  {
		this.service.createProject(null, "~/jakeproject", null);
	}
	
	@Test(timeout = TestingConstants.UNITTESTTIME,expected = IllegalArgumentException.class)
	public void createProject_shouldFailWithNullPath() throws FileNotFoundException, IllegalArgumentException  {
		this.service.createProject("neuesProjekt", null, null);
	}
}
