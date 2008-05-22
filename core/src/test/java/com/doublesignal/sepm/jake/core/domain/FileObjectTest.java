package com.doublesignal.sepm.jake.core.domain;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Testclass for FileObject.
 * @author Simon
 *
 */
public class FileObjectTest {
	
	private static final String name = "name";
	private FileObject f1, f2, f3, fnull;
	
	/**
	 * Setup
	 */
	@Before
	public void setUp() {
		f1 = new FileObject(name);
		f2 = new FileObject("file2 name");
		f3 = new FileObject(name);
		fnull = new FileObject(null);

	}
	
	/**
	 * tear down
	 */
	@After
	public void tearDown() {
		f1 = null;
		f2 = null;
	}
	
	/**
	 * tests if to different file obs are not equal.
	 */
	@Test
	public void testNotEqual() {
		Assert.assertFalse(f1.equals(f2));
	}
	
	/**
	 * test if f1 equals f1
	 */
	@Test
	public void testEqual() {
		Assert.assertTrue(f1.equals(f1));
	}
	
	/**
	 * test if f1 equals a file ob with a null value. Should not throw exception.
	 */
	@Test
	public void testWithNullValue() {
		Assert.assertFalse(f1.equals(fnull));
	}
	
	/**
	 *  tests if f1 equals f3.
	 */
	@Test
	public void testf1Equalf3() {
		Assert.assertTrue(f1.equals(f3));
	}
}
