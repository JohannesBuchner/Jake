package com.doublesignal.sepm.jake.gui;

import java.io.File;
import java.sql.SQLException;

import junit.framework.TestCase;

import com.doublesignal.sepm.jake.core.services.JakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.*;

public class JakeGuiAccessTest extends TestCase {
	
	String tmpdir = System.getProperty("java.io.tmpdir","") + File.separator;
	JakeGuiAccess jga = null;
	
	public void setUp() throws Exception{
		File rootPath = new File(tmpdir, "testProject");
		rootPath.mkdir();
		File jakeFile = new File(tmpdir, "testProject" + ".script");
		jakeFile.delete();
		String rootfolder = rootPath.getAbsolutePath();
		if(!(rootPath.exists() && rootPath.isDirectory()))
			return;
		try {
			jga = JakeGuiAccess.openProjectByRootpath(rootfolder);
		} catch (NonExistantDatabaseException e) {
			jga = JakeGuiAccess.createNewProjectByRootpath(rootfolder, "testProject");
		}
	}
	
	public void testA() throws Exception{
		assertEquals(jga.getProject().getName(),"testProject");
	}
	
	public void tearDown(){
		File rootPath = new File(tmpdir, "testProject");
		rootPath.delete();
		File jakeFile = new File(tmpdir, "testProject" + ".script");
		jakeFile.delete();
	}
	
}
