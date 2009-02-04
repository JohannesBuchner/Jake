package com.jakeapp.jake.test;

import java.io.File;

import junit.framework.Assert;


public abstract class FSTestCommons {

	/**
	 * gets a new temporary directory available for tests
	 */
	public static File provideTempDir() {
		clean();
		String systmpdir = System.getProperty("java.io.tmpdir", "");
		if (!systmpdir.endsWith(File.separator))
			systmpdir = systmpdir + File.separator;

		File f = new File(systmpdir);
		Assert.assertEquals("tmpdir", systmpdir, f.getAbsolutePath()
				+ File.separator);

		f = new File(systmpdir + File.separator + "fstest");
		if (f.exists()) {
			Assert.assertTrue("recursiveDelete", recursiveDelete(f));
		}
		Assert.assertTrue("mkdir", f.mkdir());

		Assert.assertTrue("create successful", f.exists());

		clean();
		return f;
	}

	/*
	 * unfortunately, Windows tests do not succeed otherwise TODO for future
	 * generations
	 */
	private static void clean() {
		System.gc();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
		System.gc();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
		System.gc();
	}

	/**
	 * fully delete a folder
	 * 
	 * @param folder
	 * @return whether deletion was successful
	 */
	public static boolean recursiveDelete(File folder) {
		clean(); /* windows needs this */
		if (folder.isFile()) {
			return folder.delete();
		} else {
			String[] l = folder.list();
			if (l != null) {
				for (int i = 0; i < l.length; i++) {
					if (recursiveDelete(new File(folder.getPath(), l[i])) == false) {
						System.err.println("deleting " + l[i] + " in "
								+ folder.getPath() + " failed!");
						return false;
					}
				}
			}
			clean();
			return folder.delete();
		}
	}
}
