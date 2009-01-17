package com.jakeapp.jake.fss;

import java.io.File;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class FSTestCase  {
	protected String mytempdir = null; 
	
	@Before
	public void setUp() throws Exception {
		clean();
		String systmpdir = System.getProperty("java.io.tmpdir","");
		if(!systmpdir.endsWith(File.separator))
			systmpdir = systmpdir + File.separator;
		// TODO
		//assertTrue(systmpdir.startsWith("C:\\") || systmpdir.startsWith("/tmp"));
		
		File f = new File(systmpdir);
		Assert.assertEquals("tmpdir",systmpdir, f.getAbsolutePath()+File.separator);
		f = new File(systmpdir + File.separator + "fstest");
		if(f.exists()){
			Assert.assertTrue("recursiveDelete",recursiveDelete(f));
		}
		Assert.assertTrue("mkdir",f.mkdir());
		
		Assert.assertTrue("create successful",f.exists() );
		
		mytempdir = f.getAbsolutePath();
		//System.out.println("Using "+systmpdir+" for FSS tests");
		clean();
	}
	
	protected boolean recursiveDelete(File f) {
		clean(); /* windows needs this */
		if(f.isFile()){
			System.out.println("Deleting file: "+f.getAbsoluteFile());
			return f.delete();
		}else{
			System.out.println("Deleting folder: "+f.getAbsoluteFile());
			String[] l = f.list();
			if(l!=null){
				for (int i = 0; i < l.length; i++) {
					if(recursiveDelete(new File(f.getPath(),l[i])) == false){
						System.err.println("deleting " +l[i]+" in "+f.getPath() + " failed!");
						return false;
					}
				}
			}
			clean();
			return f.delete();
		}
	}
	
	@After
	public void tearDown() throws Exception {
		clean();
		File f = new File(mytempdir);
		if(f.exists()){
			Assert.assertTrue("recursiveDelete",recursiveDelete(f));
		}
		Assert.assertFalse("Cleanup done",f.exists());
		clean();
	}

	private void clean() {
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

	@Test
	public void notest() {
		Assert.assertNotNull(mytempdir);
	}
}
