package com.jakeapp.jake.fss;

import java.io.File;
import java.io.FileNotFoundException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.junit.ext.Prerequisite;
import com.googlecode.junit.ext.PrerequisiteAwareClassRunner;
import com.jakeapp.jake.fss.exceptions.NotADirectoryException;


@RunWith(PrerequisiteAwareClassRunner.class)
public class FSServiceInnerTest extends FSTestCase {
	FSService fss = null;
	
	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testIsValidRelpath() throws Exception{
		
		Assert.assertTrue(fss.isValidRelpath("/"));
		Assert.assertFalse("upexploit",fss.isValidRelpath(".."));
		Assert.assertFalse("upexploit",fss.isValidRelpath("foo/../bar"));
		Assert.assertFalse("upexploit",fss.isValidRelpath("foo/.."));
		Assert.assertFalse("upexploit",fss.isValidRelpath("foo/../"));
		Assert.assertFalse("upexploit",fss.isValidRelpath("foo/../../bar"));
		
		String[] valids = {
			"/", "foo", "foo.bar", "rANdoM.xls", "fold/er.txt", 
			"crazy/named/file/that.has.more.ext.s", "cool+-035.x_chars", 
			"a", "ac (also cool).file", "...", "foo....bar", "foo/bar../abz"
		};
		for (String valid : valids) {
			Assert.assertTrue("valid name: " + valid, fss.isValidRelpath(valid));
		}
		Assert.assertFalse("up and out", fss.isValidRelpath("foo/../../bar.xls"));
		Assert.assertFalse("up",fss.isValidRelpath("foo/../bar.xls"));
		
		String[] s = {"~","*","..","#","=","}","{","$","\"",
				"'","!","%","&","<",">","|","","@","","^","\\", ":" };

		for (String value : s) {
			Assert.assertFalse("Invalid character " + value, fss.isValidRelpath("~"));
		}
		
		Assert.assertFalse("backslash",fss.isValidRelpath("windows\\path"));
		Assert.assertFalse("notestyle",fss.isValidRelpath("note:random"));
	}
	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testSetRootPath() throws Exception{
		String oldrootpath = fss.getRootPath();
		
		try{
			fss.setRootPath("/root/file/that/doesnt/exist");
			Assert.fail();
		}catch(FileNotFoundException e){
		}
		{
			String filename = mytempdir + "/test.out";
			File f = new File(filename);
			f.createNewFile();
			try{
				fss.setRootPath(filename);
				Assert.fail();
			}catch(NotADirectoryException e){
			}
		}
		new File(mytempdir, "bla").mkdirs();
		String[] dirnames = {mytempdir + "/bla/..", 
				mytempdir + "/!", mytempdir + "/#"};
		for (String dirname : dirnames) {
			File f = new File(dirname);
			f.mkdirs();
			Assert.assertTrue("Creating dir successful", f.exists() && f.isDirectory());
			try {
				fss.setRootPath(dirname);
			} catch (NotADirectoryException e) {
			}
		}
				
		fss.setRootPath(oldrootpath);
	}
	
	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testGetTempDir() throws Exception{
		if(File.separatorChar == '/'){
			Assert.assertEquals("/tmp", fss.getTempDir());
		}else{
			Assert.assertEquals(System.getProperty("java.io.tmpdir",""), fss.getTempDir() + 
				File.separator);
		}
		
	}
	
	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testGetTempFile() throws Exception{
		String filename = fss.getTempFile();
		Assert.assertTrue("in Temp dir",filename.startsWith(fss.getTempDir() + 
			File.separator));
		File f = new File(filename);
		f.delete();
	}
	
	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testHashLength() throws Exception{
		Assert.assertEquals(fss.getHashLength(), 128); 
	}
	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testHash() throws Exception{
		Assert.assertEquals(fss.calculateHash("foobar".getBytes()), 
			"0a50261ebd1a390fed2bf326f2673c145582a6342d523204973d0219337f81616a8069b012587cf5635f6925f1b56c360230c19b273500ee013e030601bf2425");
		Assert.assertEquals(fss.calculateHash("".getBytes()), 
			"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e");
	}	
	
	@Override
	@Before
	//@Prerequisite(checker = DesktopSupportedChecker.class)  - does not work
	public void setUp() throws Exception {
		super.setUp();
		
		if ((new DesktopSupportedChecker()).satisfy()) {
			fss = new FSService();
			fss.setRootPath(mytempdir);
			Assert.assertEquals("rootpath",mytempdir,fss.getRootPath());
			Assert.assertFalse(fss.getRootPath().startsWith("/home"));
		}
	}
	
}
