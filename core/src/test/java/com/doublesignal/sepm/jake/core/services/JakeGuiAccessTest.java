package com.doublesignal.sepm.jake.core.services;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;

import com.doublesignal.sepm.jake.core.services.JakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.*;

public class JakeGuiAccessTest extends TestCase {
	
	//String tmpdir = System.getProperty("java.io.tmpdir","") + File.separator;
	String tmpdir = "/home/user/Desktop/foo2/";
	JakeGuiAccess jga;
	
	@Before
	public void setup() throws Exception{
		File rootPath = new File(tmpdir, "testProject");
		rootPath.mkdir();
		File jakeFile = new File(tmpdir, "testProject" + ".script");
		jakeFile.delete();
		String rootfolder = rootPath.getAbsolutePath();
		if(!(rootPath.exists() && rootPath.isDirectory()))
			return;
		try {
			jga = JakeGuiAccess.openProjectByRootpath(rootfolder);
			fail("NonExistantDatabaseException");
		} catch (NonExistantDatabaseException e) {
			jga = JakeGuiAccess.createNewProjectByRootpath(rootfolder, "testProject");
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
	}
	
	@After
	public void tearDown(){
		File rootPath = new File(tmpdir, "testProject");
		rootPath.delete();
		File jakeFile = new File(tmpdir, "testProject" + ".script");
		jakeFile.delete();
	}
	
}
