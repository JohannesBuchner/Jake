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
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 18, 2008
 * Time: 1:05:06 PM
 */
public class ProjectTest
{

	private String baseDir;

	private String validRootPath;
	private String fileRootPath;
	private String RootPathDoesNotExist;

	private String validProjectName;
	private String onecharProjectName;
	private String tooLongProjectName;
	private String maximumLengthProjectName;
	private String emptyProjectName;
	private String nullProjectName;

	private String validProjectId = "jp3820xx";
	private String emptyProjectId = "";
	private String nullProjectId = null;
	private String tooShortProjectId = "1234567";
	private String tooLongProjectId = "123456789";


	public ProjectTest()
	{

		baseDir = "/tmp";
		validRootPath = baseDir + "/validRootPath";
		fileRootPath = baseDir + "/somefile";
		RootPathDoesNotExist = baseDir + "/somenotexistendthing";


		validProjectName = "My great JakeProject";
		tooLongProjectName = "dasisteintestdasisteintestdasisteintestdasisteintes"; // 51 chars
		emptyProjectName = "";
		nullProjectName = null;
		onecharProjectName = "a";
		maximumLengthProjectName = "dasisteintestdasisteintestdasisteintestdasisteinte";
	}


	@Before
	public void Setup() throws Exception
	{
		new File(validRootPath).mkdir();
		new File(fileRootPath).createNewFile();
	}

	@After
	public void TearDown() throws Exception
	{
		new File(fileRootPath).delete();
		new File(validRootPath).delete();
	}


	@Test
	public void createEmptyProjectTest()
	{
		Project project = new Project();
	}

	@Test(expected = ProjectNotConfiguredException.class)
	public void accessUnconfiguredProjectFolder() throws ProjectNotConfiguredException
	{
		Project proj = new Project();
		proj.getRootPath();
	}

	@Test(expected = ProjectNotConfiguredException.class)
	public void accessUnconfiguredProjectName() throws ProjectNotConfiguredException
	{
		Project proj = new Project();
		proj.getName();
	}


	@Test(expected = ProjectNotConfiguredException.class)
	public void accessUnconfiguredProjectId() throws ProjectNotConfiguredException
	{
		Project proj = new Project();
		proj.getProjectId();
	}


	@Test()
	public void createProjectWithValidPath()
	{
		Project proj = new Project();

		try
		{
			proj.setRootPath(new File(validRootPath));
		}
		catch (InvalidRootPathException e)
		{
			Assert.fail("couldn't set a valid rootpath");
		}

	}

	@Test(expected = InvalidRootPathException.class)
	public void createProjectWithFileRootPath() throws InvalidRootPathException
	{
		Project proj = new Project();
		proj.setRootPath(new File(fileRootPath));
	}

	@Test(expected = InvalidRootPathException.class)
	public void createProjectWithNonExistendRootPath() throws InvalidRootPathException
	{
		Project proj = new Project();
		proj.setRootPath(new File(RootPathDoesNotExist));
	}

	@Test
	public void createProjectWithValidName()
	{
		Project proj = new Project();
		try
		{
			proj.setName(validProjectName);
		}
		catch (InvalidProjectNameException e)
		{
			Assert.fail("Thrown exception but ProjectName is valid");
		}
	}

	@Test(expected = InvalidProjectNameException.class)
	public void createOnecharProjectNameTest() throws InvalidProjectNameException
	{
		Project proj = new Project();
		proj.setName(onecharProjectName);
	}

	@Test(expected = InvalidProjectNameException.class)
	public void createTooLongProjectNameTest() throws InvalidProjectNameException
	{
		Project proj = new Project();
		proj.setName(tooLongProjectName);
	}

	@Test
	public void createMaximumLengthProjectNameTest()
	{
		Project proj = new Project();

		try
		{
			proj.setName(maximumLengthProjectName);
		}
		catch (InvalidProjectNameException e)
		{
			Assert.fail("failed but projectname has maximum allowed with");
		}
	}

	@Test(expected = InvalidProjectNameException.class)
	public void emptyProjectNameTest() throws InvalidProjectNameException
	{
		Project proj = new Project();
		proj.setName(emptyProjectName);
	}


	@Test(expected = InvalidProjectNameException.class)
	public void nullProjectNameTest() throws InvalidProjectNameException
	{
		Project proj = new Project();
		proj.setName(nullProjectName);
	}


	@Test
	public void createProjectWithValidProjectId()
	{
		Project proj = new Project();
		try
		{
			proj.setProjectId(validProjectId);
		}
		catch (InvalidProjectIdException e)
		{
			Assert.fail("failed but projectId was correct");
		}
	}

	@Test(expected = InvalidProjectIdException.class)
	public void emptyProjectIdTest() throws InvalidProjectIdException
	{
		Project proj = new Project();
		proj.setProjectId(emptyProjectId);
	}

	@Test(expected = InvalidProjectIdException.class)
	public void nullProjectIdTest() throws InvalidProjectIdException
	{
		Project proj = new Project();
		proj.setProjectId(nullProjectId);
	}

	@Test(expected = InvalidProjectIdException.class)
	public void tooShortProjectIdTest() throws InvalidProjectIdException
	{
		Project proj = new Project();
		proj.setProjectId(tooShortProjectId);
	}

	@Test(expected = InvalidProjectIdException.class)
	public void tooLongProjectIdTest() throws InvalidProjectIdException
	{
		Project proj = new Project();
		proj.setProjectId(tooLongProjectId);
	}

	@Test
	public void createCorrectProjectTest()
	{
		try
		{
			Project proj = new Project(new File(validRootPath), validProjectName, validProjectId);


			Assert.assertTrue(proj.getName().equals(validProjectName));
			Assert.assertTrue(proj.getProjectId().equals(validProjectId));
			Assert.assertTrue(proj.getRootPath().toString().equals(validRootPath));


		}
		catch (InvalidProjectNameException e)
		{
			Assert.fail("Failed but ProjectName was correct");
		}
		catch (InvalidProjectIdException e)
		{
			Assert.fail("Failed but ProjectId was correct");
		}
		catch (InvalidRootPathException e)
		{
			Assert.fail("Failed but ProjectRootPath was correct");
		}
		catch (ProjectNotConfiguredException e)
		{
			Assert.fail("Failed but project was configured");
		}
	}

	@Test
	public void equalsTest()
	{
		Project proja = null;
		Project projb = null;
		try
		{
			proja = new Project(new File(validRootPath), validProjectName, validProjectId);
			projb = new Project();
			projb.setProjectId(validProjectId);
			projb.setName(validProjectName);
			projb.setRootPath(new File(validRootPath));
		}
		catch (InvalidProjectNameException e)
		{
			Assert.fail();
		}
		catch (InvalidProjectIdException e)
		{
			Assert.fail();
		}
		catch (InvalidRootPathException e)
		{
			Assert.fail();
		}

		Assert.assertEquals(proja,projb);

	}

		public void hashCodeTest()
	{
		Project proja = null;
		Project projb = null;
		try
		{
			proja = new Project(new File(validRootPath), validProjectName, validProjectId);
			projb = new Project();
			projb.setProjectId(validProjectId);
			projb.setName(validProjectName);
			projb.setRootPath(new File(validRootPath));
		}
		catch (InvalidProjectNameException e)
		{
			Assert.fail();
		}
		catch (InvalidProjectIdException e)
		{
			Assert.fail();
		}
		catch (InvalidRootPathException e)
		{
			Assert.fail();
		}


		Assert.assertTrue(proja.hashCode() == projb.hashCode());

	}

}
