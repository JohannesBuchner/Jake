package com.jakeapp.jake.fss;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.junit.ext.Prerequisite;
import com.googlecode.junit.ext.PrerequisiteAwareClassRunner;
import com.jakeapp.jake.fss.exceptions.CreatingSubDirectoriesFailedException;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAFileException;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;

@RunWith(PrerequisiteAwareClassRunner.class)
public class FSServiceOuterTest extends FSServiceTestCase {

	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testGetFullpath() throws Exception {
		String sep = File.separator;
		String root = mytempdir;

		Assert.assertEquals("root", root, fss.getFullpath("/"));
		Assert.assertEquals(fss.getFullpath("testfile.xml"), root + sep
				+ "testfile.xml");
		Assert.assertEquals(fss.getFullpath("folder/to/testfile.xml"), root + sep
				+ "folder" + sep + "to" + sep + "testfile.xml");


		Assert.assertEquals(fss.joinPath("foldera", "folderb"), "foldera" + sep
				+ "folderb");
		Assert.assertEquals(fss.joinPath("foldera" + sep + "to" + sep, "folderb"),
				"foldera" + sep + "to" + sep + "folderb");

	}

	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testReadFile() throws Exception {
		wipeRoot();
		Assert.assertFalse("got a tempdir", mytempdir == null
				&& mytempdir.length() > 0);

		try {
			fss.readFile(":");
			Assert.fail("InvalidFilenameException");
		} catch (InvalidFilenameException e) {
		}
		try {
			fss.readFile("/../../test.xml");
			Assert.fail("InvalidFilenameException");
		} catch (InvalidFilenameException e) {
		}
		try {
			fss.readFile("/folder/../../../test.xml");
			Assert.fail("InvalidFilenameException");
		} catch (InvalidFilenameException e) {
		}
		try {
			fss.readFile("/doesntexist");
			Assert.fail("FileDoesNotExistException");
		} catch (FileNotFoundException e) {
		}
		try {
			fss.readFile("/");
			Assert.fail("NotAFileException");
		} catch (NotAFileException e) {
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
			Assert.assertEquals("content read correctly", new String(r), content);
		}
		{
			byte[] c = { 32, 12, 61, 72, 245 - 256, 11, 100, 0, 23, 1, 4, 21,
					254 - 256, 21, 1, 2 };

			FileOutputStream bw = new FileOutputStream(mytempdir
					+ File.separator + filename);
			bw.write(c);
			bw.close();

			byte[] r = fss.readFile(filename);
			Assert.assertEquals("content read correctly", new String(r), new String(c));
			Assert.assertEquals("content read correctly", r.length, c.length);
		}
		{
			boolean setupWorked = true;
			try {
				Runtime.getRuntime().exec(
						"chmod 000 " + mytempdir + File.separator + filename);
				Thread.sleep(100);
			} catch (Exception e) {
				setupWorked = false;
			}
			if (setupWorked) {
				try {
					fss.readFile(filename);
					Assert.fail("NotAReadableFileException");
				} catch (NotAReadableFileException e) {
				}
			}
		}
	}

	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testFolderExists() throws Exception {
		wipeRoot();
		Assert.assertTrue(fss.folderExists("/"));
		Assert.assertFalse(fss.folderExists("/folderDoesNotExist"));
		String[] dirnames = { "foobar", "/foo/bar/", "ran/dom/stuff" };
		for (int i = 0; i < dirnames.length; i++) {
			File f = new File(fss.getRootPath() + File.separator + dirnames[i]
					+ File.separator);
			f.mkdirs();
			Assert.assertTrue(fss.folderExists(dirnames[i]));
		}
		try {
			fss.folderExists("/fol:derDoesNotExist");
			Assert.fail("InvalidFilenameException");
		} catch (InvalidFilenameException e) {
		}
		File f = new File(fss.getRootPath() + File.separator + "test.out");
		f.createNewFile();
		Assert.assertFalse(fss.folderExists("test.out"));
	}

	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testFileExists() throws Exception {
		wipeRoot();
		Assert.assertFalse(fss.fileExists("/fileDoesNotExist"));
		Assert.assertFalse(fss.fileExists("fileDoesNotExist"));

		String[] dirnames = { "foobar", "/foo/bar", "ran/dom/stuff" };
		for (int i = 0; i < dirnames.length; i++) {
			File f = new File(fss.getRootPath() + File.separator + dirnames[i]
					+ File.separator);
			f.getParentFile().mkdirs();
			f.createNewFile();
			Assert.assertTrue(fss.fileExists(dirnames[i]));
		}
		try {
			fss.fileExists("/fil:eDoesNotExist");
			Assert.fail("InvalidFilenameException");
		} catch (InvalidFilenameException e) {
		}

		File f = new File(fss.getRootPath() + File.separator + "foobar");
		f.delete();
		f.mkdirs();
		Assert.assertFalse(fss.fileExists("foobar"));

	}

	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testListFolder() throws Exception {
		wipeRoot();
		String folder = "jakeAtestFolder";
		recursiveDelete(new File(fss.getRootPath() + File.separator + folder));

		String[] content = { "B", "C", "E", "D", "F", "G", "H", "J" };
		for (int i = 0; i < content.length; i++) {
			File f = new File(fss.getRootPath() + File.separator + folder
					+ File.separator + content[i]);
			if (i < 4) {
				f.mkdirs();
				Assert.assertTrue(f.isDirectory());
			} else {
				f.createNewFile();
				Assert.assertTrue(f.isFile());
			}
		}

		List<String> s = fss.listFolder(folder);
		String sep = "/";

		for (int j = 0; j < content.length; j++) {
			boolean found = false;
			for (int i = 0; i < s.size(); i++) {
				if (s.get(i).equals(folder + sep + content[j])) {
					found = true;
					break;
				}
			}
			Assert.assertTrue(found);
			if (j < 4) {
				Assert.assertTrue("folder: " + content[j], fss.folderExists(folder
						+ sep + content[j]));
			} else {
				Assert.assertTrue("file: " + content[j], fss.fileExists(folder + sep
						+ content[j]));
			}
		}

		Assert.assertTrue(s.size() == content.length);
	}

	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testRecursiveListFolder() throws Exception {
		wipeRoot();
		recursiveDelete(new File(fss.getRootPath()));

		String[] content = { "B", "C", "B/foo", "D", "F", "G", "H", "J",
				"B/foo/bar", "C/foo" };
		for (int i = 0; i < content.length; i++) {
			File f = new File(fss.getRootPath() + File.separator + content[i]);
			if (i < 4) {
				f.mkdirs();
				Assert.assertTrue(f.isDirectory());
			} else {
				f.createNewFile();
				Assert.assertTrue(f.isFile());
			}
		}

		List<String> s = fss.recursiveListFiles();

		for (int j = 0; j < content.length; j++) {
			boolean found = false;
			for (int i = 0; i < s.size(); i++) {
				if (s.get(i).equals(content[j])) {
					found = true;
					break;
				}
			}
			if (j < 4) {
				Assert.assertFalse("We don't expect directory " + content[j], found);
			} else {
				Assert.assertTrue(found);
				Assert.assertTrue("file: " + content[j], fss.fileExists(content[j]));
			}
		}

		Assert.assertTrue(s.size() == content.length - 4);
		wipeRoot();
	}

	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testWriteFile() throws Exception {
		wipeRoot();
		try {
			fss.writeFile("ran:dom", null);
			Assert.fail("InvalidFilenameException");
		} catch (InvalidFilenameException e) {
		}
		{
			fss.writeFile("random", new byte[] {});
			Assert.assertEquals(fss.readFile("random").length, 0);
		}
		{
			String content = "Foo bar\nbaz";
			fss.writeFile("random", content.getBytes());
			Assert.assertEquals(new String(fss.readFile("random")), content);
		}
		{
			String content = "Foo\u00c3\u00a4\u00c3\u00b6p\u00c3\u00a4\u00c3\u00bc\u00c3\u00b6 "
					+ "bar\nbau\u00e2\u0082\u00ac@\u00c4\u00b1\u00ce\u00b4\u00c3\u00be\u00c3"
					+ "\u00a6'\u00c5\u0093\u00c5\u0093\u00c3\u00a6'\u00c5\u0093\u00c3\u00b8"
					+ "\u00e2\u0082\u00ac@\u00c4\u00b1z";
			fss.writeFile("random", content.getBytes("utf8"));
			Assert.assertEquals(new String(fss.readFile("random"), "utf8"), content);
		}
		fss.writeFile("foo", new byte[] { 12 });
		try {
			fss.writeFile("foo/random", new byte[] { 12, 23 });
			Assert.fail("CreatingSubDirectoriesFailedException");
		} catch (CreatingSubDirectoriesFailedException e) {
		}

		{
			fss.writeFile("bar/baz/random", "Foobar".getBytes());
			Assert.assertTrue("recursice create", fss.folderExists("bar")
					&& fss.folderExists("bar/baz"));
			Assert.assertTrue("recursice create", fss.fileExists("bar/baz/random"));
			Assert.assertEquals("recursice create", "Foobar", new String(fss
					.readFile("bar/baz/random")));
		}

	}

	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testDeleteFile() throws Exception {
		wipeRoot();
		{
			Assert.assertFalse(fss.fileExists("bar/baz/random")
					&& fss.folderExists("bar/baz") && fss.folderExists("bar"));

			fss.writeFile("bar/baz/random", "Foobar".getBytes());

			Assert.assertTrue(fss.fileExists("bar/baz/random")
					&& fss.folderExists("bar/baz") && fss.folderExists("bar"));

			Assert.assertTrue(fss.deleteFile("bar/baz/random"));

			Assert.assertFalse(fss.fileExists("bar/baz/random")
					&& fss.folderExists("bar/baz") && fss.folderExists("bar"));
		}
		wipeRoot();
		{
			fss.writeFile("bar/baz/random", "Foobar".getBytes());
			try {
				fss.deleteFile("bar/baz/random2");
				Assert.fail("FileNotFoundException");
			} catch (FileNotFoundException e) {
			}
			try {
				fss.deleteFile("bar/baz/");
				Assert.fail("NotAFileException");
			} catch (NotAFileException e) {
			}
			Assert.assertTrue(fss.fileExists("bar/baz/random"));
			fss.writeFile("bar/baz/random2", "Foobar".getBytes());
			Assert.assertTrue(fss.deleteFile("bar/baz/random"));
			Assert.assertFalse(fss.fileExists("bar/baz/random"));
			Assert.assertTrue(fss.folderExists("bar/baz") && fss.folderExists("bar"));
			Assert.assertTrue(fss.fileExists("bar/baz/random2"));
		}

	}

	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testHashFile() throws Exception {
		fss.writeFile("bar/baz/random", "Foobar".getBytes());

		Assert.assertEquals(
				fss.calculateHashOverFile("bar/baz/random"),
				"cead1f59a9a0d22e46a28f943a662338dd758d6dce38f7ea6ab13b6615c312b69fffff049781c169b597577cb5566d5d1354364ac032a9d4d5bd8ef833340061");

	}

	/**
	 * Tests that no exceptions are thrown. No application is launched, since
	 * awt/swing is not started
	 **/
	@Test
	@Prerequisite(checker = ThrowStuffInMyFaceChecker.class)
	public void launchTest() throws Exception {
		fss.writeFile("launch1.txt", "Foobar".getBytes());
		fss.writeFile("launch2.html",
				"<html><body><h1>Woot!</h1></body></html>".getBytes());
		Thread.yield();
		fss.launchFile("launch1.txt");
		fss.launchFile("launch2.html");
	}

	@Test
	@Prerequisite(checker = DesktopSupportedChecker.class)
	public void testFileSize() throws Exception {
		fss.writeFile("launch1.txt", "Foobar".getBytes());
		fss.writeFile("launch2.html",
				"<html><body><h1>Woot!</h1></body></html>".getBytes());
		Assert.assertEquals(fss.getFileSize("launch2.html"), fss
				.readFile("launch2.html").length);
		Assert.assertEquals(fss.getFileSize("launch1.txt"), fss
				.readFile("launch1.txt").length);
		try {
			fss.getFileSize("does/not/exist.txt");
			Assert.fail();
		} catch (FileNotFoundException e) {
		}
		fss.writeFile("foo/bar.txt", "Foobar".getBytes());
		try {
			fss.getFileSize("foo");
			Assert.fail();
		} catch (NotAFileException e) {
		}

	}

	/* TODO: launchFile registerModificationListener */
}
