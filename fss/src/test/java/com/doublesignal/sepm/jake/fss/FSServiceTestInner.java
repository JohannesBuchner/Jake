package com.doublesignal.sepm.jake.fss;

import com.doublesignal.sepm.jake.fss.exceptions.NotADirectoryException;

import java.io.File;
import java.io.FileNotFoundException;


public class FSServiceTestInner extends FSTestCase {
	FSService fss = null;
	public void testIsValidRelpath() throws Exception{
		
		assertTrue(fss.isValidRelpath("/"));
		assertFalse("upexploit",fss.isValidRelpath(".."));
		assertFalse("upexploit",fss.isValidRelpath("foo/../bar"));
		assertFalse("upexploit",fss.isValidRelpath("foo/.."));
		assertFalse("upexploit",fss.isValidRelpath("foo/../"));
		assertFalse("upexploit",fss.isValidRelpath("foo/../../bar"));
		
		String[] valids = {
			"/", "foo", "foo.bar", "rANdoM.xls", "fold/er.txt", 
			"crazy/named/file/that.has.more.ext.s", "cool+-035.x_chars", 
			"a", "ac (also cool).file", "...", "foo....bar", "foo/bar../abz"
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
	
	public void testGetTempDir() throws Exception{
		if(File.separatorChar == '/'){
			assertEquals("/tmp", fss.getTempDir());
		}else{
			assertEquals(System.getProperty("java.io.tmpdir",""), fss.getTempDir() + 
				File.separator);
		}
		
	}
	
	public void testGetTempFile() throws Exception{
		String filename = fss.getTempFile();
		assertTrue("in Temp dir",filename.startsWith(fss.getTempDir() + 
			File.separator));
		File f = new File(filename);
		f.delete();
	}
	
	public void testHashLength() throws Exception{
		assertEquals(fss.getHashLength(), 128); 
	}
	public void testHash() throws Exception{
		assertEquals(fss.calculateHash("foobar".getBytes()), 
			"0a50261ebd1a390fed2bf326f2673c145582a6342d523204973d0219337f81616a8069b012587cf5635f6925f1b56c360230c19b273500ee013e030601bf2425");
		assertEquals(fss.calculateHash("".getBytes()), 
			"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e");
	}	
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		fss = new FSService();
		fss.setRootPath(mytempdir);
		assertEquals("rootpath",mytempdir,fss.getRootPath());
		assertFalse(fss.getRootPath().startsWith("/home"));
	}
}
