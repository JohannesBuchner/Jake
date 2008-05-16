package com.doublesignal.sepm.jake.fss;

import java.io.File;

import junit.framework.TestCase;

public class FSTestCase extends TestCase {
	String mytempdir = null; 
	

	@Override
	public void setUp() throws Exception {
		String systmpdir = "/tmp";
		if(File.separatorChar != '/'){
			throw new Exception("Test only developed for Unix-like operating systems");
		}
		
		File f = new File(systmpdir);
		assertEquals("tmpdir",systmpdir, f.getAbsolutePath());
		f = new File(systmpdir + File.separator + "fstest");
		if(f.exists()){
			assertTrue("recursiveDelete",recursiveDelete(f));
		}
		assertTrue("mkdir",f.mkdir());
		
		assertTrue("create successful",f.exists() );
		
		mytempdir = f.getAbsolutePath();
		
		super.setUp();
	}
	
	private boolean recursiveDelete(File f) {
		if(f.isFile()){
			//System.out.println("Deleting file: "+f.getAbsoluteFile());
			return f.delete();
		}else{
			//System.out.println("Deleting folder: "+f.getAbsoluteFile());
			String[] l = f.list();
			if(l!=null){
				for (int i = 0; i < l.length; i++) {
					if(recursiveDelete(new File(f.getPath() + File.separator + l[i])) == false)
						return false;
				}
			}
			f.delete();
			return true;
		}
	}

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
