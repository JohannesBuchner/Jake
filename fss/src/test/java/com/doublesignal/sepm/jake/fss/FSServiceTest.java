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
	
	public void testGetFullpath() throws Exception{
		String sep = File.separator;
		String root = mytempdir;
		
		assertEquals("separator", "/", File.separator);
		
		assertEquals("/", root, fss.getFullpath(sep));
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
		wipeRoot();
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
	
	public void testFolderExists() throws Exception {
		wipeRoot();
		assertTrue(fss.folderExists("/"));
		assertFalse(fss.folderExists("/folderDoesNotExist"));
		String[] dirnames = {"foobar", 
				"/foo/bar/", "ran/dom/stuff"};
		for (int i = 0; i < dirnames.length; i++) {
			File f = new File(fss.getRootPath() + File.separator + dirnames[i] + 
					File.separator);
			f.mkdirs();
			assertTrue(fss.folderExists(dirnames[i]));
		}
		try{
			fss.folderExists("/fol:derDoesNotExist");
			fail("InvalidFilenameException");
		}catch(InvalidFilenameException e){
		}
		File f = new File(fss.getRootPath() + File.separator + "test.out");
		f.createNewFile();
		assertFalse(fss.folderExists("test.out"));
	}
	public void testFileExists() throws Exception {
		wipeRoot();
		assertFalse(fss.fileExists("/fileDoesNotExist"));
		assertFalse(fss.fileExists("fileDoesNotExist"));
		
		String[] dirnames = {"foobar", 
				"/foo/bar", "ran/dom/stuff"};
		for (int i = 0; i < dirnames.length; i++) {
			File f = new File(fss.getRootPath() + File.separator + dirnames[i] + 
					File.separator);
			f.getParentFile().mkdirs();
			f.createNewFile();
			assertTrue(fss.fileExists(dirnames[i]));
		}
		try{
			fss.fileExists("/fil:eDoesNotExist");
			fail("InvalidFilenameException");
		}catch(InvalidFilenameException e){
		}
		
		File f = new File(fss.getRootPath() + File.separator + "foobar");
		f.delete();
		f.mkdirs();
		assertFalse(fss.fileExists("foobar"));
		
	}
	
	public void testGetTempDir() throws Exception{
		if(File.separatorChar == '/'){
			assertEquals("/tmp", fss.getTempDir());
		}else{
			fail("Not implemented for Windows (yet)");
			assertEquals("C:\\Windows\\Temp", fss.getTempDir());
		}
		
	}
	
	public void testGetTempFile() throws Exception{
		assertTrue("in Temp dir",fss.getTempFile().startsWith(fss.getTempDir() + 
			File.separator));
	}
	
	public void testListFolder() throws Exception{
		wipeRoot();
		String folder = "jakeAtestFolder";
		recursiveDelete(new File(fss.getRootPath() + File.separator + folder));
		
		String[] content = { "B", "C", "E", "D", "F", "G", "H", "J" };
		for (int i = 0; i < content.length; i++) {
			File f = new File(fss.getRootPath() + File.separator + folder + 
				File.separator + content[i]);
			if ( i < 4 ) {
				f.mkdirs();
				assertTrue(f.isDirectory());
			} else {
				f.createNewFile();
				assertTrue(f.isFile());
			}
		}
		
		String[] s = fss.listFolder(folder);		
		for (int j = 0; j < content.length; j++) {
			boolean found = false;
			for (int i = 0; i < s.length; i++) {
				if(s[i].equals(content[j])){
					found = true;
					break;
				}
			}
			assertTrue(found);
			if(j<4){
				assertTrue("folder: " + content[j],fss.folderExists(folder + File.separator + content[j]));
			}else{
				assertTrue("file: " + content[j],  fss.fileExists(folder + File.separator + content[j]));
			}
		}
		
		assertTrue(s.length == content.length);
	}
	
	public void testWriteFile() throws Exception{
		wipeRoot();
		try{
			fss.writeFile("ran:dom", null);
			fail("InvalidFilenameException");
		}catch(InvalidFilenameException e){
		}
		{
			fss.writeFile("random", new byte[]{});
			assertEquals(fss.readFile("random").length,0);
		}
		{
			String content = "Foo bar\nbaz"; 
			fss.writeFile("random", content.getBytes());
			assertEquals(new String(fss.readFile("random")),content);
		}
		{
			String content = "Fooäöpäüö bar\nbau€@ıδþæ'œœæ'œø€@ız"; 
			fss.writeFile("random", content.getBytes("utf8"));
			assertEquals(new String(fss.readFile("random"), "utf8"),content);
		}
		fss.writeFile("foo", new byte[]{12});
		try{
			fss.writeFile("foo/random", new byte[]{12,23});
			fail("CreatingSubDirectoriesFailedException");
		}catch(CreatingSubDirectoriesFailedException e){
		}
		
		{
			fss.writeFile("bar/baz/random", "Foobar".getBytes());
			assertTrue("recursice create", fss.folderExists("bar") && fss.folderExists("bar/baz"));
			assertTrue("recursice create", fss.fileExists("bar/baz/random"));
			assertEquals("recursice create","Foobar",new String(fss.readFile("bar/baz/random")));
		}
		
	}
	public void testDeleteFile() throws Exception{
		wipeRoot();
		{
			assertFalse(fss.fileExists("bar/baz/random") && 
				fss.folderExists("bar/baz") && fss.folderExists("bar"));
			
			fss.writeFile("bar/baz/random", "Foobar".getBytes());
			
			assertTrue(fss.fileExists("bar/baz/random") && 
				fss.folderExists("bar/baz") && fss.folderExists("bar"));
			
			assertTrue(fss.deleteFile("bar/baz/random"));
			
			assertFalse(fss.fileExists("bar/baz/random") && 
				fss.folderExists("bar/baz") && fss.folderExists("bar"));
		}
		wipeRoot();
		{
			fss.writeFile("bar/baz/random", "Foobar".getBytes());
			try{
				fss.deleteFile("bar/baz/random2");
				fail("FileNotFoundException");
			}catch(FileNotFoundException e){
			}
			try{
				fss.deleteFile("bar/baz/");
				fail("NotAFileException");
			}catch(NotAFileException e){
			}
			assertTrue(fss.fileExists("bar/baz/random"));
			fss.writeFile("bar/baz/random2", "Foobar".getBytes());
			assertTrue(fss.deleteFile("bar/baz/random"));
			assertFalse(fss.fileExists("bar/baz/random"));
			assertTrue(fss.folderExists("bar/baz") && fss.folderExists("bar"));
			assertTrue(fss.fileExists("bar/baz/random2"));
		}
		
	}
	
	
	private void wipeRoot() {
		File f = new File(fss.getRootPath());
		assertTrue(f.exists() && f.isDirectory());
		assertTrue(recursiveDelete(f));
		f.mkdirs();
		assertTrue(f.exists() && f.isDirectory() && f.list().length == 0);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		fss = new FSService();
		assertFalse(fss == null);
		fss.setRootPath(mytempdir);
		assertEquals("rootpath",mytempdir,fss.getRootPath());
		assertFalse(fss.getRootPath().startsWith("/home"));
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* TODO: calculateHash launchFile registerModificationListener */
}
