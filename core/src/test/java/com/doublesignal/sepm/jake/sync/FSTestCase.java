package com.doublesignal.sepm.jake.sync;

import java.io.File;

import junit.framework.TestCase;

public class FSTestCase extends TestCase {
	protected String mytempdir = null; 
	
	@Override
	public void setUp() throws Exception {
		String systmpdir = System.getProperty("java.io.tmpdir","");
		if(!systmpdir.endsWith(File.separator))
			systmpdir = systmpdir + File.separator;
		
		assertTrue(systmpdir.startsWith("C:\\") || systmpdir.startsWith("/tmp"));
		
		File f = new File(systmpdir);
		assertEquals("tmpdir",systmpdir, f.getAbsolutePath()+File.separator);
		f = new File(systmpdir + File.separator + "fstest");
		if(f.exists()){
			assertTrue("recursiveDelete",recursiveDelete(f));
		}
		assertTrue("mkdir",f.mkdir());
		
		assertTrue("create successful",f.exists() );
		
		mytempdir = f.getAbsolutePath();
		//System.out.println("Using "+systmpdir+" for FSS tests");
		super.setUp();
	}
	
	protected boolean recursiveDelete(File f) {
		System.gc(); /* windows needs this */
		if(f.isFile()){
			//System.out.println("Deleting file: "+f.getAbsoluteFile());
			return f.delete();
		}else{
			//System.out.println("Deleting folder: "+f.getAbsoluteFile());
			String[] l = f.list();
			if(l!=null){
				for (int i = 0; i < l.length; i++) {
					if(recursiveDelete(new File(f.getPath(),l[i])) == false){
						System.err.println("deleting " +l[i]+" in "+f.getPath() + " failed!");
						return false;
					}
				}
			}
			return f.delete();
		}
	}
	
	/**
	 * Makes this class executable
	 */
	public void testnotest(){
		
	}
	
	@Override
	public void tearDown() throws Exception {
		File f = new File(mytempdir);
		if(f.exists()){
			assertTrue("recursiveDelete",recursiveDelete(f));
		}
		assertFalse("Cleanup done",f.exists());
		
		super.tearDown();
	}
	
}
