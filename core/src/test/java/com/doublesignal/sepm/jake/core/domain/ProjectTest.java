package com.doublesignal.sepm.jake.core.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Tests for Project.
 */
public class ProjectTest {

	private static final String baseDir = "/tmp";

	private String validRootPath;
	private String fileRootPath;
	private static final String validProjectName = "My great JakeProject";
	private static final String validProjectId = "jp3820xx";
	
	private Project p1, p2;

	@Before
	public void Setup() throws Exception {
		validRootPath = baseDir + "/validRootPath";
		fileRootPath = baseDir + "/somefile";
		new File(validRootPath).mkdir();
		new File(fileRootPath).createNewFile();

		p1 = new Project(new File(validRootPath), validProjectName, validProjectId);
		p2 = new Project(new File(validRootPath), validProjectName, "otherPID");
	}

	@After
	public void TearDown() throws Exception {
		new File(fileRootPath).delete();
		new File(validRootPath).delete();
	}

	@Test()
	public void createProjectWithValidPath() {
		p1.setRootPath(new File(validRootPath));
	}

	@Test()
	public void createProjectWithNullRootPath() {
		p1.setRootPath(null);
	}
	
	@Test
	public void createProjectWithValidName() {
		p1.setName(validProjectName);
	}
	
	@Test
	public void createCorrectProjectTest() {
		Project proj = new Project(new File(validRootPath), validProjectName, validProjectId);

		Assert.assertTrue(proj.getName().equals(validProjectName));
		Assert.assertTrue(proj.getRootPath().toString().equals(validRootPath));
	}

	@Test
	public void p1Equalsp2() {
		Assert.assertFalse(p1.equals(p2));
	}

	@Test
	public void p1Equalsp1() {
		Assert.assertTrue(p1.equals(p1));
	}

	public void hashCodeTest() {
		Assert.assertTrue(p1.hashCode() == p1.hashCode());

	}

}
