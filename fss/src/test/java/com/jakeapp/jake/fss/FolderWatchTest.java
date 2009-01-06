package com.jakeapp.jake.fss;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.junit.ext.Prerequisite;
import com.googlecode.junit.ext.PrerequisiteAwareClassRunner;

@RunWith(PrerequisiteAwareClassRunner.class)
public class FolderWatchTest extends FSTestCase {

	private static final Logger log = Logger.getLogger(FolderWatchTest.class);

	FolderWatcher fw = null;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testCreateFile() throws Exception {
		log.debug(" **** testCreateFile **** ");
		File f = new File(mytempdir + File.separator + "just_created");
		try {
			fw = new FolderWatcher(new File(mytempdir), 100);
			fw.initialRun();

			final CountDownLatch latch = new CountDownLatch(1);
			fw.addListener(new IModificationListener() {

				public void fileModified(File f, ModifyActions action) {
					Assert.assertEquals("just_created", f.getName());
					Assert.assertEquals(ModifyActions.CREATED, action);
					latch.countDown();
				}
			});
			fw.run();

			f.createNewFile();

			log.debug("We expect CREATED");
			if (!latch.await(3, TimeUnit.SECONDS)) {
				Assert.fail("No callback occured");
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
		Assert.assertTrue(f.exists());
		f.delete();
		System.gc();
		if (f.exists()) {
			log.info("WARNING: You are on a stupid Windows. ");
			f.delete();
		}
		Assert.assertFalse(f.exists());
	}

	@Test
	public void testDeleteFile() throws Exception {
		log.debug(" **** testDeleteFile **** ");
		File f = new File(mytempdir + File.separator + "just_deleted");
		long interval = 1000;

		try {
			final Semaphore s = new Semaphore(0);

			fw = new FolderWatcher(new File(mytempdir), interval);
			fw.initialRun();

			fw.addListener(new IModificationListener() {

				int state = 0;

				public void fileModified(File f, ModifyActions action) {
					log.debug("testDeleteFile: State: " + state);
					switch (state) {
						case 0:
							Assert.assertEquals("just_deleted", f.getName());
							Assert.assertEquals(ModifyActions.CREATED, action);
							break;
						case 1:
							Assert.assertEquals("just_deleted", f.getName());
							Assert.assertEquals(ModifyActions.DELETED, action);
							break;
						default:
							Assert.fail("Unknown state");
					}
					state++;
					log.debug("Releasing, we are now in state " + state);
					s.release();
				}
			});
			fw.run();
			f.createNewFile();
			log.debug("We expect CREATED");
			Assert.assertTrue(s.tryAcquire(interval * 3, TimeUnit.MILLISECONDS));
			f.delete();
			System.gc();
			f.delete();
			Assert.assertFalse(f.exists());
			log.debug("We expect DELETED");
			Assert.assertTrue(s.tryAcquire(interval * 3, TimeUnit.MILLISECONDS));

			fw.cancel();
		} catch (Exception e) {
			fw.cancel();
			f.delete();
			throw e;
		}
	}

	@Test
	public void testModifyFile() throws Exception {

		log.debug("**** testModifyFile **** ");
		File f = new File(mytempdir + File.separator + "just_edited");
		writeInFile(f, "foo, bar");
		long interval = 700;

		try {
			final Semaphore s = new Semaphore(0);
			fw = new FolderWatcher(new File(mytempdir), interval);
			fw.initialRun();

			IModificationListener failer = new IModificationListener() {

				public void fileModified(File f, ModifyActions action) {
					Assert.fail("Got unwanted event: " + f.getAbsolutePath() + ":"
							+ action);
				}
			};
			fw.addListener(failer);
			fw.run();

			awaitNextTimeUnit();

			log.debug("We expect no event.");

			writeInFile(f, "foo, bar");

			Thread.sleep(interval * 3);

			fw.removeListener(failer);
			fw.addListener(new IModificationListener() {

				public void fileModified(File f, ModifyActions action) {
					log.debug("got event: " + f.getAbsolutePath() + ":" + action);
					Assert.assertEquals("just_edited", f.getName());
					Assert.assertEquals(ModifyActions.MODIFIED, action);
					s.release();
				}
			});

			awaitNextTimeUnit();

			log.debug("We expect no event.");

			writeInFile(f, "bar, baz");
			Assert.assertTrue("Real content change", s.tryAcquire(interval * 3,
					TimeUnit.MILLISECONDS));

			fw.cancel();
			f.delete();
			System.gc();
			f.delete();
			Assert.assertFalse(f.exists());

		} catch (Exception e) {
			fw.cancel();
			f.delete();

			throw e;
		}
	}


	private void awaitNextTimeUnit() throws InterruptedException {
		log.debug("Waiting a second ... ");
		Thread.sleep(1000); /* modifications are measured in seconds */
		log.debug("ok.");
	}

	private void writeInFile(File f, String content) throws Exception {
		FileWriter fwriter = new FileWriter(f);
		fwriter.append(content);
		fwriter.close();
		fwriter = null;
		System.gc();
	}

	@Test
	public void testWithSmallInterval() throws Exception {
		runSzenarioTest(10);
	}

	@Test
	public void testWithNormalInterval() throws Exception {
		runSzenarioTest(100);
	}

	@Test
	@Prerequisite(checker = AllowSlowChecker.class)
	public void testWithBigInterval() throws Exception {
		runSzenarioTest(1000);
	}

	private void runSzenarioTest(long interval) throws Exception {
		log.debug("runSzenarioTest: with interval " + interval);
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
					log.debug("State: " + state);
					switch (state) {
						case 0:
							Assert.assertEquals(afile, f.getName());
							Assert.assertEquals(ModifyActions.CREATED, action);
							break;
						case 1:
							Assert.assertEquals(afile, f.getName());
							Assert.assertEquals(ModifyActions.MODIFIED, action);
							break;
						case 2:
							Assert.assertEquals(afile, f.getName());
							Assert.assertEquals(ModifyActions.DELETED, action);
							break;
						case 3:
							Assert.assertEquals(bfile, f.getName());
							Assert.assertEquals(ModifyActions.CREATED, action);
							break;
						case 4:
							Assert.assertEquals(afile, f.getName());
							Assert.assertEquals(ModifyActions.CREATED, action);
							break;
						case 5:
							Assert.assertEquals(bfile, f.getName());
							Assert.assertEquals(ModifyActions.DELETED, action);
							break;
						case 6:
							Assert.assertEquals(afile, f.getName());
							Assert.assertEquals(ModifyActions.MODIFIED, action);
							break;
						case 7:
							Assert.assertEquals(afile, f.getName());
							Assert.assertEquals(ModifyActions.DELETED, action);
							break;
						default:
							Assert.fail("Unknown state");
					}
					state++;
					s.release();
				}
			});
			fw.run();
			f.createNewFile();
			Assert.assertTrue(s.tryAcquire(interval * 3, TimeUnit.MILLISECONDS));

			awaitNextTimeUnit();

			writeInFile(f, "foo, bar");
			Assert.assertTrue(s.tryAcquire(interval * 3, TimeUnit.MILLISECONDS));
			f.delete();
			System.gc();
			f.delete();
			Assert.assertTrue(s.tryAcquire(interval * 3, TimeUnit.MILLISECONDS));

			writeInFile(f2, "Hello World");
			Assert.assertTrue(s.tryAcquire(interval * 3, TimeUnit.MILLISECONDS));

			writeInFile(f, "bar, baz");
			Assert.assertTrue(s.tryAcquire(interval * 3, TimeUnit.MILLISECONDS));

			f2.delete();
			System.gc();
			f2.delete();
			Assert.assertTrue(s.tryAcquire(interval * 3, TimeUnit.MILLISECONDS));

			awaitNextTimeUnit();

			writeInFile(f, "bar, bazz");
			Assert.assertTrue(s.tryAcquire(interval * 3, TimeUnit.MILLISECONDS));
			f.delete();
			System.gc();
			f.delete();
			Assert.assertTrue(s.tryAcquire(interval * 3, TimeUnit.MILLISECONDS));

			fw.cancel();
		} catch (Exception e) {
			f.delete();
			f2.delete();
			fw.cancel();
			throw e;
		}
	}

	@After
	public void tearDown() throws Exception {
		System.gc();
		super.tearDown();
		System.gc();
	}
}
