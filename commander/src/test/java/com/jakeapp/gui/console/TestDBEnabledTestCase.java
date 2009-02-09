package com.jakeapp.gui.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.jakeapp.jake.test.FSTestCommons;


abstract public class TestDBEnabledTestCase extends TmpdirEnabledTestCase {

	private static final Logger log = Logger.getLogger(TestDBEnabledTestCase.class);

	/**
	 * From http://www.dreamincode.net/code/snippet1443.htm. No Copyright
	 * attached. Because of triviality of this function, no license assumed
	 * (Public Domain). Please contact us if this is not the case and we will
	 * remove this function.
	 * 
	 * This function will copy files or directories from one location to
	 * another. note that the source and the destination must be mutually
	 * exclusive. This function can not be used to copy a directory to a sub
	 * directory of itself. The function will also have problems if the
	 * destination files already exist.
	 * 
	 * @param src
	 *            -- A File object that represents the source for the copy
	 * @param dest
	 *            -- A File object that represents the destination for the copy.
	 * @throws IOException
	 *             if unable to copy.
	 */
	public static void copyFiles(File src, File dest, String ignoredDirectory) throws IOException {
		if (src.getName().equals(ignoredDirectory)) {
			return;
		}
		log.debug(src.getAbsolutePath() + " -> " + dest.getAbsolutePath());
		// Check to ensure that the source is valid...
		if (!src.exists()) {
			throw new IOException("copyFiles: Can not find source: " + src.getAbsolutePath() + ".");
		} else if (!src.canRead()) { // check to ensure we have rights to the
			// source...
			throw new IOException("copyFiles: No right to source: " + src.getAbsolutePath() + ".");
		}
		// is this a directory copy?
		if (src.isDirectory()) {
			if (!dest.exists()) { // does the destination already exist?
				// if not we need to make it exist if possible (note this is
				// mkdirs not mkdir)
				if (!dest.mkdirs()) {
					throw new IOException("copyFiles: Could not create direcotry: "
							+ dest.getAbsolutePath() + ".");
				}
			}
			// get a listing of files...
			String list[] = src.list();
			// copy all the files in the list.
			for (int i = 0; i < list.length; i++) {
				File dest1 = new File(dest, list[i]);
				File src1 = new File(src, list[i]);
				copyFiles(src1, dest1, ignoredDirectory);
			}
		} else {
			// This was not a directory, so lets just copy the file
			FileInputStream fin = null;
			FileOutputStream fout = null;
			byte[] buffer = new byte[4096]; // Buffer 4K at a time (you can
			// change this).
			int bytesRead;
			try {
				// open the files for input and output
				fin = new FileInputStream(src);
				fout = new FileOutputStream(dest);
				// while bytesRead indicates a successful read, lets write...
				while ((bytesRead = fin.read(buffer)) >= 0) {
					fout.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) { // Error copying file...
				IOException wrapper = new IOException("copyFiles: Unable to copy file: "
						+ src.getAbsolutePath() + "to" + dest.getAbsolutePath() + ".");
				wrapper.initCause(e);
				wrapper.setStackTrace(e.getStackTrace());
				throw wrapper;
			} finally { // Ensure that the files are closed (if they were open).
				if (fin != null) {
					fin.close();
				}
				if (fout != null) {
					fout.close();
				}
			}
		}
	}

	@Before
	public void setup() throws Exception {
		super.setup();

		String dbtemplate = this.getDbTemplateName();


		File templatedir = new File("src/test/resources/testdata", dbtemplate);
		System.out.println(templatedir.getAbsolutePath());
		Assert.assertTrue("template " + templatedir.getAbsolutePath() + " doesn't exist.",
				folderExists(templatedir));

		//File workdir = new File(tmpdir, "workdir");
		//Assert.assertTrue(workdir.mkdir());
		//copyFiles(templatedir, workdir, ".svn");

		//String cwd = System.getProperty("user.dir");
		//System.setProperty("user.dir", workdir.getAbsolutePath());
		/*
		// unfortunately, this doesn't work
		//System.setProperty("user.dir", workdir.getAbsolutePath());
		// so we are brutal (until bug 21 is resolved)
		FSTestCommons.recursiveDelete(new File(".jake"));
		File workdir = new File(".");
		copyFiles(templatedir, workdir, ".svn");
		 */
		
		// The brutal way
		FSTestCommons.recursiveDelete(new File(".jake"));
		File workdir = new File(".");
		copyFiles(templatedir, workdir, ".svn");
		
		File dbdir = new File(".jake");
		dbdir.mkdir();		
		Assert.assertTrue(folderExists(".jake"));
		log.info("Directory prepared with database template. ");
	}

	public boolean folderExists(String name) {
		return folderExists(new File(name));
	}

	public boolean folderExists(File f) {
		return f.exists() && f.isDirectory();
	}

	abstract protected String getDbTemplateName();

	@After
	public void teardown() throws Exception {
		super.teardown();
	}
}
