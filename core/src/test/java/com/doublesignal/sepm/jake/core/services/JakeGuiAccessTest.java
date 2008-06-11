package com.doublesignal.sepm.jake.core.services;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchProjectMemberException;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.services.JakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.*;

public class JakeGuiAccessTest extends TestCase {
	
	String tmpdir = System.getProperty("java.io.tmpdir","") + File.separator;
	//String tmpdir = "/home/user/Desktop/foo2/";
	IJakeGuiAccess jga;
	ProjectMember pm;
	
	
	@Before
	public void setup() throws Exception{
		File rootPath = new File(tmpdir, "testProject");
		rootPath.mkdir();
		File jakeFile = new File(tmpdir, "testProject" + ".script");
		jakeFile.delete();
		File jakeFile2 = new File(tmpdir, "testProject" + ".properties");
		jakeFile2.delete();
		String rootfolder = rootPath.getAbsolutePath();
		if(!(rootPath.exists() && rootPath.isDirectory()))
			return;
		try {
			jga = JakeGuiAccess.openProjectByRootpath(rootfolder);
			fail("NonExistantDatabaseException");
		} catch (NonExistantDatabaseException e) {
			jga = JakeGuiAccess.createNewProjectByRootpath(rootfolder, 
					"testProject", "test@host");
		} catch (RuntimeException e){
			e.printStackTrace();
			fail();
		}
		assertNotNull(jga);
	}
	@Test
	public void testA() throws Exception{
		setup();
		assertNotNull(jga);
		assertNotNull(jga.getProject());
		assertEquals(jga.getProject().getName(),"testProject");
		assertEquals(jga.getLoginUserid(), "test@host");
	}
	
	@Test
	public void testEditProjectMemberUserId()throws Exception	{
		
		setup();
		String userNameOne = "testuser@domain.de";
		String userNameTwo = "user@domain.com";
	
		jga.addProjectMember(userNameOne);
		
		jga.editProjectMemberUserId(jga.getProjectMember(userNameOne) , userNameTwo);
		try	{
		jga.getProjectMember(userNameOne);
		fail();
		}	catch (NoSuchProjectMemberException e)	{
			 
		
		}
		
		
	}
		
	
	@Test
	public void testAddProjectMember()throws Exception	{
		setup();
		String userNameOne = "testuser@domain.de";
		jga.addProjectMember(userNameOne);
		assertNotNull(jga.getProjectMember(userNameOne));
		
		
	}
	
	@Test
	public void testGetProjectMember()throws Exception	{
		setup();
		String userNameOne = "testuser@domain.de";
		jga.addProjectMember(userNameOne);
		assertNotNull(jga.getProjectMember(userNameOne));
		
		
	}
	
	@Test
	public void testEditProjectMemberNickName()throws Exception	{
		setup();
		String userNameOne = "testuser@domain.de";
		String nickName = "User Nick Name";
		jga.addProjectMember(userNameOne);
		jga.editProjectMemberNickName(	jga.getProjectMember(userNameOne), nickName);
		assertEquals(jga.getProjectMember(userNameOne).getNickname() , nickName);
		
	}
	
	@Test
	public void testEditProjectMemberNote()throws Exception	{
		setup();
		String userNameOne = "testuser@domain.de";
		String note = "This is a Note!";
	
		jga.addProjectMember(userNameOne);
		jga.editProjectMemberNote(	jga.getProjectMember(userNameOne), note);
		assertEquals(jga.getProjectMember(userNameOne).getNotes() , note);
		
	}
	
	@After
	public void tearDown(){
		File rootPath = new File(tmpdir, "testProject");
		rootPath.delete();
		File jakeFile = new File(tmpdir, "testProject" + ".script");
		jakeFile.delete();
		File jakeFile2 = new File(tmpdir, "testProject" + ".properties");
		jakeFile2.delete();
	}
	
}
