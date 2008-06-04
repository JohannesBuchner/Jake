package com.doublesignal.sepm.jake.core.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author johannes
 */
public class ProjectTest {

	private String baseDir = System.getProperty("java.io.tmpdir","");

	private String validRootPath;
	private String fileRootPath;
	private static final String validProjectName = "My great JakeProject";
	private static final String validOtherProjectName = "jp3820xx";
	
	private Project p1, p2;

	@Before
	public void Setup() throws Exception {
		if(!baseDir.endsWith(File.separator))
			baseDir = baseDir + File.separator;
		validRootPath = baseDir + "validRootPath";
		fileRootPath = baseDir + "somefile";
		new File(validRootPath).mkdir();
		new File(fileRootPath).createNewFile();

		p1 = new Project(new File(validRootPath), validProjectName);
		p2 = new Project(new File(validRootPath), validOtherProjectName);
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
		Project proj = new Project(new File(validRootPath), validProjectName);

		Assert.assertTrue(proj.getName().equals(validProjectName));
		Assert.assertEquals(proj.getRootPath().toString(),validRootPath);
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
