package com.doublesignal.sepm.jake.fss;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;

public class FSServiceTest extends FSTestCase {
	FSService fss = null;
	public void testIsValidRelpath() throws Exception{
		
		assertTrue(fss.isValidRelpath("/"));
		
		String[] valids = {"/", "foo", "foo.bar", "rANdoM.xls", "fold/er.txt", 
			"crazy/named/file/that.has.more.ext.s", "cool+-035.x_chars", 
			"a", "ac (also cool).file"
		};
		for (int i = 0; i < valids.length; i++) {
			assertTrue("valid name: " + valids[i], fss.isValidRelpath(valids[i]));
		}
		assertFalse("up and out", fss.isValidRelpath("foo/../../bar.xls"));
		assertFalse("up",fss.isValidRelpath("foo/../bar.xls"));
		
		String[] s = {"~","*","..","#","=","}","{","$","\"",
				"'","!","%","&","<",">","|","","@","","^","\\", ":" };
		
		for (int i = 0; i < s.length; i++) {
			assertFalse("Invalid character " + s[i], fss.isValidRelpath("~"));
		}
		assertFalse("backslash",fss.isValidRelpath("windows\\path"));
		assertFalse("notestyle",fss.isValidRelpath("note:random"));
		
	}
	public void testSetRootPath() throws Exception{
		String oldrootpath = fss.getRootPath();
		
		try{
			fss.setRootPath("/root/file/that/doesnt/exist");
			fail();
		}catch(FileNotFoundException e){
		}
		{
			String filename = mytempdir + "/test.out";
			File f = new File(filename);
			f.createNewFile();
			try{
				fss.setRootPath(filename);
				fail();
			}catch(NotADirectoryException e){
			}
		}
		String[] dirnames = {mytempdir + "/..", 
				mytempdir + "/!", mytempdir + "/#"};
		for (int i = 0; i < dirnames.length; i++) {
			File f = new File(dirnames[i]);
			f.mkdirs();
			assertTrue("Creating dir successful", f.exists() && f.isDirectory());
			try{
				fss.setRootPath(dirnames[i]);
			}catch(NotADirectoryException e){
			}
		}
				
		fss.setRootPath(oldrootpath);
	}
	
	public void testgetFullpath() throws Exception{
		String sep = File.separator;
		String root = mytempdir;
		
		assertEquals("separator", "/", File.separator);
		
		assertEquals("/", root + sep, fss.getFullpath(sep));
		assertEquals(fss.getFullpath("testfile.xml"), 
				root + sep + "testfile.xml");
		assertEquals(fss.getFullpath("folder/to/testfile.xml"), 
				root + sep + "folder"+sep+"to"+sep+"testfile.xml");
		
		
		assertEquals(fss.joinPath("foldera", "folderb"), 
			"foldera"+sep+"folderb");
		assertEquals(fss.joinPath("foldera/to/", "folderb"), 
			"foldera"+sep+"to"+sep+"folderb");
		
	}
	
	public void testReadFile() throws Exception{
		assertFalse("got a tempdir", mytempdir==null && mytempdir.length()>0);
		
		try{
			fss.readFile(":");
			fail("InvalidFilenameException");
		}catch(InvalidFilenameException e){
		}
		try{
			fss.readFile("/../../test.xml");
			fail("InvalidFilenameException");
		}catch(InvalidFilenameException e){
		}
		try{
			fss.readFile("/folder/../../../test.xml");
			fail("InvalidFilenameException");
		}catch(InvalidFilenameException e){
		}
		try{
			fss.readFile("/doesntexist");
			fail("FileDoesNotExistException");
		}catch(FileNotFoundException e){
		}
		try{
			fss.readFile("/");
			fail("NotAFileException");
		}catch(NotAFileException e){
		}
		
		String filename = "/test.out";
		String content = "This is interesting\nstuff\n\nyou know...\n\n";
		{
			FileWriter w = new FileWriter(mytempdir + File.separator + filename);
			BufferedWriter bw = new BufferedWriter(w);
			bw.write(content);
			bw.close();
			w.close();
			
			byte[] r = fss.readFile(filename);
			assertEquals("content read correctly", new String(r), content);
		}
		{
			byte[] c = {32,12,61,72,245-256,11,100,0,23,1,4,21,254-256,21,1,2 };
			
			FileOutputStream bw = new FileOutputStream(mytempdir + File.separator + filename);
			bw.write(c);
			bw.close();
			
			byte[] r = fss.readFile(filename);
			assertEquals("content read correctly", new String(r), new String(c));
			assertEquals("content read correctly", r.length, c.length);
		}
		{
			Runtime.getRuntime().exec(
				"chmod 000 " + mytempdir + File.separator + filename);
			
			Thread.sleep(100);
			try{
				fss.readFile(filename);
				fail("NotAReadableFileException");
			}catch(NotAReadableFileException e){
			}
		}
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		fss = new FSService();
		assertFalse(fss == null);
		fss.setRootPath(mytempdir);
		assertEquals("rootpath",mytempdir,fss.getRootPath());
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	
}
