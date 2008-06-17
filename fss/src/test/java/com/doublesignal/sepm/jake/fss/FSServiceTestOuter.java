package com.doublesignal.sepm.jake.fss;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;


public class FSServiceTestOuter extends FSServiceTestCase {
	public void testGetFullpath() throws Exception{
		String sep = File.separator;
		String root = mytempdir;
		
		assertEquals("root", root, fss.getFullpath("/"));
		assertEquals(fss.getFullpath("testfile.xml"), 
				root + sep + "testfile.xml");
		assertEquals(fss.getFullpath("folder/to/testfile.xml"), 
				root + sep + "folder"+sep+"to"+sep+"testfile.xml");
		
		
		assertEquals(fss.joinPath("foldera", "folderb"), 
			"foldera"+sep+"folderb");
		assertEquals(fss.joinPath("foldera"+sep+"to"+sep, "folderb"), 
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
			boolean setupWorked = true;
			try{
				Runtime.getRuntime().exec(
					"chmod 000 " + mytempdir + File.separator + filename);
				Thread.sleep(100);
			}catch(Exception e){
				setupWorked = false;
			}
			if(setupWorked){
				try{
					fss.readFile(filename);
					fail("NotAReadableFileException");
				}catch(NotAReadableFileException e){
				}
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
		
		List<String> s = fss.listFolder(folder);
		String sep = "/";
		
		for (int j = 0; j < content.length; j++) {
			boolean found = false;
			for (int i = 0; i < s.size(); i++) {
				if(s.get(i).equals(folder + sep + content[j])){
					found = true;
					break;
				}
			}
			assertTrue(found);
			if(j<4){
				assertTrue("folder: " + content[j],fss.folderExists(folder + sep + content[j]));
			}else{
				assertTrue("file: " + content[j],  fss.fileExists(folder + sep + content[j]));
			}
		}
		
		assertTrue(s.size() == content.length);
	}
	
	public void testRecursiveListFolder() throws Exception{
		wipeRoot();
		recursiveDelete(new File(fss.getRootPath()));
		
		String[] content = { "B", "C", "B/foo", "D", "F", "G", "H", "J", "B/foo/bar", "C/foo" };
		for (int i = 0; i < content.length; i++) {
			File f = new File(fss.getRootPath() + File.separator + content[i]);
			if ( i < 4 ) {
				f.mkdirs();
				assertTrue(f.isDirectory());
			} else {
				f.createNewFile();
				assertTrue(f.isFile());
			}
		}
		
		List<String> s = fss.recursiveListFiles();

		for (int j = 0; j < content.length; j++) {
			boolean found = false;
			for (int i = 0; i < s.size(); i++) {
				if(s.get(i).equals(content[j])){
					found = true;
					break;
				}
			}
			if(j<4){
				assertFalse("We don't expect directory " + content[j],found);
			}else{
				assertTrue(found);
				assertTrue("file: " + content[j],  fss.fileExists(content[j]));
			}
		}
		
		assertTrue(s.size() == content.length - 4);
		wipeRoot();
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
	
	public void testHashFile() throws Exception{
		fss.writeFile("bar/baz/random", "Foobar".getBytes());
		
		assertEquals(fss.calculateHashOverFile("bar/baz/random"), 
			"cead1f59a9a0d22e46a28f943a662338dd758d6dce38f7ea6ab13b6615c312b69fffff049781c169b597577cb5566d5d1354364ac032a9d4d5bd8ef833340061");
		
	}
	
	/** 
	 * Tests that no exceptions are thrown.
	 * No application is launched, since awt/swing is not started
	 **/
	public void launchTest() throws Exception {
		fss.writeFile("launch1.txt", "Foobar".getBytes());
		fss.writeFile("launch2.html", "<html><body><h1>Woot!</h1></body></html>"
				.getBytes());
		fss.launchFile("launch1.txt");
		fss.launchFile("launch2.html");
	}
	
	public void testFileSize() throws Exception {
		fss.writeFile("launch1.txt", "Foobar".getBytes());
		fss.writeFile("launch2.html", "<html><body><h1>Woot!</h1></body></html>"
				.getBytes());
		assertEquals(fss.getFileSize("launch2.html"),
				fss.readFile("launch2.html").length);
		assertEquals(fss.getFileSize("launch1.txt"),
				fss.readFile("launch1.txt").length);
		try{
			fss.getFileSize("does/not/exist.txt");
			fail();
		}catch(FileNotFoundException e){
		}
		fss.writeFile("foo/bar.txt", "Foobar".getBytes());
		try{
			fss.getFileSize("foo");
			fail();
		}catch(NotAFileException e){
		}

	}
	
	/* TODO: launchFile registerModificationListener */
}
