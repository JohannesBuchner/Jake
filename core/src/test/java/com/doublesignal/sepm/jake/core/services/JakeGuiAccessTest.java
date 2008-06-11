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
	public void editProjectMemberUserIdTest()throws Exception	{
		String userNameOne = "testuser@domain.de";
		String userNameTwo = "valid@domain.de";
		
		jga.addProjectMember(userNameOne);
		jga.addProjectMember(userNameTwo);
		try {
			jga.editProjectMemberUserId(jga.getProjectMember(userNameOne), userNameTwo);
		} catch (NoSuchProjectMemberException e) {
			fail("Can'tAddUser");
		}
		
		try {
			assertEquals(jga.getProjectMember(userNameOne),jga.getProjectMember(userNameTwo));
		} catch (NoSuchProjectMemberException e) {
			fail("Can'tAddUser");
		}
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
