package com.doublesignal.sepm.jake.fss;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FolderWatchTest extends FSTestCase {

	FolderWatcher fw = null;

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testCreateFile() throws Exception {
		System.out.println(" **** testCreateFile **** ");
		File f = new File(mytempdir + File.separator + "just_created");
		try {
			fw = new FolderWatcher(new File(mytempdir), 100);
			fw.initialRun();
			
			final CountDownLatch latch = new CountDownLatch(1);
			fw.addListener(new IModificationListener() {
				public void fileModified(File f, ModifyActions action) {
					assertEquals("just_created", f.getName());
					assertEquals(ModifyActions.CREATED, action);
					latch.countDown();
				}
			});
			fw.run();
			
			f.createNewFile();
			
			System.out.println("We expect CREATED");
			if (!latch.await(3, TimeUnit.SECONDS)) {
				fail("No callback occured");
			}
			f.delete();
			fw.cancel();
		} catch (Exception e) {
			fw.cancel();
			f.delete();
			throw e;
		}
	}
	@Test
	public void testIsWindowsStupid() throws Exception {
		File f = new File(mytempdir + File.separator + "just_stupid");
		writeInFile(f, "foo bar");
		assertTrue(f.exists());
		f.delete();
		System.gc();
		if(f.exists()){
			System.out.println("WARNING: You are on a stupid Windows. ");
			f.delete();
		}
		assertFalse(f.exists());
	}
	
	@Test
	public void testDeleteFile() throws Exception {
		System.out.println(" **** testDeleteFile **** ");
		File f = new File(mytempdir + File.separator + "just_deleted");
		long interval = 1000;
		
		try {
			final Semaphore s = new Semaphore(0);
			
			fw = new FolderWatcher(new File(mytempdir), interval);
			fw.initialRun();
			
			fw.addListener(new IModificationListener() {
				int state = 0;
				public void fileModified(File f, ModifyActions action) {
					System.out.println("testDeleteFile: State: "+state );
					switch(state){
						case 0:
							assertEquals("just_deleted", f.getName());
							assertEquals(ModifyActions.CREATED, action);
							break;
						case 1:
							assertEquals("just_deleted", f.getName());
							assertEquals(ModifyActions.DELETED, action);
							break;
						default:
							fail("Unknown state");
					}
					state++;
					System.out.println("Releasing, we are now in state "+state );
					s.release();
				}
			});
			fw.run();
			f.createNewFile();
			System.out.println("We expect CREATED");
			assertTrue(s.tryAcquire(interval*3, TimeUnit.MILLISECONDS)); 
			f.delete();
			System.gc();
			f.delete();
			assertFalse(f.exists());
			System.out.println("We expect DELETED");
			assertTrue(s.tryAcquire(interval*3, TimeUnit.MILLISECONDS)); 
			
			fw.cancel();
		} catch (Exception e) {
			fw.cancel();
			f.delete();
			throw e;
		}
	}
	
	@Test
	public void testModifyFile() throws Exception {
		
		System.out.println("**** testModifyFile **** ");
		File f = new File(mytempdir + File.separator + "just_edited");
		writeInFile(f, "foo, bar");
		long interval = 700;
		
		try {
			final Semaphore s = new Semaphore(0);
			fw = new FolderWatcher(new File(mytempdir), interval);
			fw.initialRun();
			
			IModificationListener failer = new IModificationListener() {
				public void fileModified(File f, ModifyActions action) {
					fail("Got unwanted event: " + f.getAbsolutePath() + 
							":" + action);
				}
			};
			fw.addListener(failer);
			fw.run();
			
			awaitNextTimeUnit();
			
			System.out.println("We expect no event.");
			
			writeInFile(f, "foo, bar");
			
			Thread.sleep(interval*3);
			
			fw.removeListener(failer);
			fw.addListener(new IModificationListener() {
				public void fileModified(File f, ModifyActions action) {
					System.out.println("got event: " + f.getAbsolutePath() + 
							":" + action);
					assertEquals("just_edited", f.getName());
					assertEquals(ModifyActions.MODIFIED, action);
					s.release();
				}
			});
			
			awaitNextTimeUnit();
			
			System.out.println("We expect no event.");
			
			writeInFile(f, "bar, baz");
			assertTrue("Real content change", 
				s.tryAcquire(interval*3, TimeUnit.MILLISECONDS)); 
			
			fw.cancel();
			f.delete();
			System.gc();
			f.delete();
			assertFalse(f.exists());
			
		} catch (Exception e) {
			fw.cancel();
			f.delete();
			
			throw e;
		}
	}
	
	
	private void awaitNextTimeUnit() throws InterruptedException {
		System.out.print("Waiting a second ... ");
		Thread.sleep(1000); /* modifications are measured in seconds */
		System.out.println("ok.");
	}

	private void writeInFile(File f, String content) throws Exception {
		FileWriter fwriter = new FileWriter(f);
		fwriter.append(content);
		fwriter.close();
		fwriter = null;
		System.gc();
	}
	
	@Test
	public void testWithSmallInterval() throws Exception{
		runSzenarioTest(10);
	}
	@Test
	public void testWithNormalInterval() throws Exception{
		runSzenarioTest(100);
	}
	@Test
	public void testWithBigInterval() throws Exception{
		runSzenarioTest(1000);
	}
	private void runSzenarioTest(long interval) throws Exception {
		System.out.println("runSzenarioTest: with interval " + interval);
		final File f = new File(mytempdir + File.separator + "just_modified");
		final File f2 = new File(mytempdir + File.separator + "just_modified2");
		final Semaphore s = new Semaphore(0);
		
		try {
			fw = new FolderWatcher(new File(mytempdir), interval);
			fw.initialRun();
			
			fw.addListener(new IModificationListener() {
				int state = 0;
				String afile = f.getName();
				String bfile = f2.getName();
				
				public void fileModified(File f, ModifyActions action) {
					System.out.println("State: "+state );
					switch(state){
						case 0:
							assertEquals(afile, f.getName());
							assertEquals(ModifyActions.CREATED, action);
							break;
						case 1:
							assertEquals(afile, f.getName());
							assertEquals(ModifyActions.MODIFIED, action);
							break;
						case 2:
							assertEquals(afile, f.getName());
							assertEquals(ModifyActions.DELETED, action);
							break;
						case 3:
							assertEquals(bfile, f.getName());
							assertEquals(ModifyActions.CREATED, action);
							break;
						case 4:
							assertEquals(afile, f.getName());
							assertEquals(ModifyActions.CREATED, action);
							break;
						case 5:
							assertEquals(bfile, f.getName());
							assertEquals(ModifyActions.DELETED, action);
							break;
						case 6:
							assertEquals(afile, f.getName());
							assertEquals(ModifyActions.MODIFIED, action);
							break;
						case 7:
							assertEquals(afile, f.getName());
							assertEquals(ModifyActions.DELETED, action);
							break;
						default:
							fail("Unknown state");
					}
					state++;
					s.release();
				}
			});
			fw.run();
			f.createNewFile();
			assertTrue(s.tryAcquire(interval*3, TimeUnit.MILLISECONDS)); 
			
			awaitNextTimeUnit();
			
			writeInFile(f, "foo, bar");
			assertTrue(s.tryAcquire(interval*3, TimeUnit.MILLISECONDS)); 
			f.delete();
			System.gc();
			f.delete();
			assertTrue(s.tryAcquire(interval*3, TimeUnit.MILLISECONDS));
			
			writeInFile(f2, "Hello World");
			assertTrue(s.tryAcquire(interval*3, TimeUnit.MILLISECONDS));
			
			writeInFile(f, "bar, baz");
			assertTrue(s.tryAcquire(interval*3, TimeUnit.MILLISECONDS)); 
			
			f2.delete();
			System.gc();
			f2.delete();
			assertTrue(s.tryAcquire(interval*3, TimeUnit.MILLISECONDS));
			
			awaitNextTimeUnit();
			
			writeInFile(f, "bar, bazz");
			assertTrue(s.tryAcquire(interval*3, TimeUnit.MILLISECONDS)); 
			f.delete();
			System.gc();
			f.delete();
			assertTrue(s.tryAcquire(interval*3, TimeUnit.MILLISECONDS)); 
			
			fw.cancel();
		} catch (Exception e) {
			f.delete();
			f2.delete();
			fw.cancel();
			throw e;
		}
	}
	@After
	public void tearDown() throws Exception{
		System.gc();
		super.tearDown();
		System.gc();
	}
}
