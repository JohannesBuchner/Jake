package com.doublesignal.sepm.jake.core.domain;

import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidRootPathException;
import com.doublesignal.sepm.jake.core.domain.exceptions.ProjectNotConfiguredException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidProjectNameException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidProjectIdException;
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
	private String RootPathDoesNotExist;

	private static final String validProjectName = "My great JakeProject";
	private static final String onecharProjectName = "a";
	private static final String tooLongProjectName = "dasisteintestdasisteintestdasisteintestdasisteintes"; // 51 chars
	private static final String maximumLengthProjectName = "dasisteintestdasisteintestdasisteintestdasisteinte";
	private static final String emptyProjectName = "";
	private static final String nullProjectName = null;

	private static final String validProjectId = "jp3820xx";
	private static final String emptyProjectId = "";
	private static final String nullProjectId = null;
	private static final String tooShortProjectId = "1234567";
	private static final String tooLongProjectId = "123456789";

	private Project p1, p2;

	@Before
	public void Setup() throws Exception {
		validRootPath = baseDir + "/validRootPath";
		fileRootPath = baseDir + "/somefile";
		RootPathDoesNotExist = baseDir + "/somenotexistendthing";

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
		try {
			p1.setRootPath(new File(validRootPath));
		} catch (InvalidRootPathException e) {
			Assert.fail("couldn't set a valid rootpath");
		}

	}

	@Test(expected = NullPointerException.class)
	public void createProjectWithNullRootPath() throws InvalidRootPathException {
		p1.setRootPath(null);
	}

	@Test(expected = InvalidRootPathException.class)
	public void createProjectWithFileRootPath() throws InvalidRootPathException {
		p1.setRootPath(new File(fileRootPath));
	}

	@Test(expected = InvalidRootPathException.class)
	public void createProjectWithNonExistendRootPath()
			throws InvalidRootPathException {
		p1.setRootPath(new File(RootPathDoesNotExist));
	}

	@Test
	public void createProjectWithValidName() {
		try {
			p1.setName(validProjectName);
		} catch (InvalidProjectNameException e) {
			Assert.fail("Thrown exception but ProjectName is valid");
		}
	}

	@Test(expected = InvalidProjectNameException.class)
	public void createOnecharProjectNameTest()
			throws InvalidProjectNameException {
		p1.setName(onecharProjectName);
	}

	@Test(expected = InvalidProjectNameException.class)
	public void createTooLongProjectNameTest()
			throws InvalidProjectNameException {
		p1.setName(tooLongProjectName);
	}

	@Test
	public void createMaximumLengthProjectNameTest() {
		try {
			p1.setName(maximumLengthProjectName);
		} catch (InvalidProjectNameException e) {
			Assert.fail("failed but projectname has maximum allowed with");
		}
	}

	@Test(expected = InvalidProjectNameException.class)
	public void emptyProjectNameTest() throws InvalidProjectNameException {
		p1.setName(emptyProjectName);
	}

	@Test(expected = NullPointerException.class)
	public void nullProjectNameTest() throws InvalidProjectNameException {
		p1.setName(nullProjectName);
	}

	@Test
	public void setValidProjectId() {
		try {
			p1.setProjectId(validProjectId);
		} catch (InvalidProjectIdException e) {
			Assert.fail("failed but projectId was correct");
		}
	}

	@Test(expected = InvalidProjectIdException.class)
	public void emptyProjectIdTest() throws InvalidProjectIdException {
		p1.setProjectId(emptyProjectId);
	}

	@Test(expected = NullPointerException.class)
	public void nullProjectIdTest() throws InvalidProjectIdException {
		p1.setProjectId(nullProjectId);
	}

	@Test(expected = InvalidProjectIdException.class)
	public void tooShortProjectIdTest() throws InvalidProjectIdException {
		p1.setProjectId(tooShortProjectId);
	}

	@Test(expected = InvalidProjectIdException.class)
	public void tooLongProjectIdTest() throws InvalidProjectIdException {
		p1.setProjectId(tooLongProjectId);
	}

	@Test
	public void createCorrectProjectTest() {
		try {
			Project proj = new Project(new File(validRootPath), validProjectName, validProjectId);

			Assert.assertTrue(proj.getName().equals(validProjectName));
			Assert.assertTrue(proj.getProjectId().equals(validProjectId));
			Assert.assertTrue(proj.getRootPath().toString().equals(validRootPath));
		} catch (InvalidProjectNameException e) {
			Assert.fail("Failed but ProjectName was correct");
		} catch (InvalidProjectIdException e) {
			Assert.fail("Failed but ProjectId was correct");
		} catch (InvalidRootPathException e) {
			Assert.fail("Failed but ProjectRootPath was correct");
		}

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
