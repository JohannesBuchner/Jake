package com.doublesignal.sepm.jake.core.services;

import java.io.File;

import junit.framework.TestCase;

import com.doublesignal.sepm.jake.core.domain.Project;
import com.doublesignal.sepm.jake.core.services.JakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.*;

public class CreateAndOpenProjectTest extends TestCase {
	
	String tmpdir = System.getProperty("java.io.tmpdir","") + File.separator;
	//String tmpdir = "/home/user/Desktop/foo2/";
	
	public void setUp() throws Exception{
		tearDown();
	}
	
	public void testcreateSchema() throws Exception{
		File rootPath = new File(tmpdir + "test-1");
		rootPath.mkdir();
		JakeGuiAccess.createSchema(rootPath.getAbsolutePath());
		File f = new File(rootPath.getAbsolutePath() + ".script");
		assertTrue(f.exists());
		f.delete();
	}
	public void testcreateNewProjectByRootpath() throws Exception{
		File rootPath = new File(tmpdir + "test-2");
		rootPath.mkdir();
		File f = new File(rootPath.getAbsolutePath() + ".script");
		f.createNewFile();
		assertTrue(f.exists());
		try{
			JakeGuiAccess.createNewProjectByRootpath(rootPath.getAbsolutePath(), 
					"foobar", "bla@foo");
			fail("ExistingProjectException");
		}catch (ExistingProjectException e) {
		}
		assertTrue(f.exists());
		f.delete();
		File f2 = new File(rootPath.getAbsolutePath() + ".properties");
		f2.delete();
		
		JakeGuiAccess jga = JakeGuiAccess.createNewProjectByRootpath(
				rootPath.getAbsolutePath(), "foobaz", "bla@bar");
		assertEquals(jga.getProject().getName(), "foobaz");
		assertEquals(jga.getLoginUserid(), "bla@bar");
		jga.close();
		assertTrue(f.exists());
		f.delete();
		f2.delete();
	}
	
	public void testopenProjectByRootpath() throws Exception
	{
		File rootPath = new File(tmpdir + "test-2");
		rootPath.mkdir();
		JakeGuiAccess.createNewProjectByRootpath(rootPath.getAbsolutePath(), 
				"foooo", "bla@bar");
		File f = new File(rootPath.getAbsolutePath() + ".script");
		assertTrue(f.exists());
		IJakeGuiAccess jga = JakeGuiAccess.openProjectByRootpath(rootPath.getAbsolutePath());
		assertEquals(jga.getProject().getName(), "foooo");
		assertEquals(jga.getLoginUserid(), "bla@bar");
		assertTrue(f.exists());
		f.delete();
	}
	
	public void testaddProjectMember() throws Exception
	{
		File rootPath = new File(tmpdir + "test-2");
		rootPath.mkdir();
		JakeGuiAccess.createNewProjectByRootpath(rootPath.getAbsolutePath(), 
				"foooo", "bla@bar");
		File f = new File(rootPath.getAbsolutePath() + ".script");
		assertTrue(f.exists());
		IJakeGuiAccess jga = JakeGuiAccess.openProjectByRootpath(rootPath.getAbsolutePath());
		assertTrue(jga.getProject().getMembers().size()==0);
		jga.addProjectMember("validusername@domain.com");
		Project p = jga.getProject();
		assertEquals("validusername@domain.com",p.getMembers().get(p.getMembers().size()-1).getUserId());
		
	}
	
	public void tearDown(){
		new File(tmpdir + "test-1").delete();
		new File(tmpdir + "test-1.script").delete();
		new File(tmpdir + "test-2").delete();
		new File(tmpdir + "test-2.script").delete();
	}
	
}
