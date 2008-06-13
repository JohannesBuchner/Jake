package com.doublesignal.sepm.jake.core.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class JakeObjectTest {
	
	private JakeObject j1, j2, jnull;
	private class Sub extends JakeObject {
		public Sub (String name) {
			super(name);
		}
	}
	
	/**
	 * set up
	 */
	@Before
	public void setUp() {
		
		j1 = new Sub("foo");
		j2 = new Sub("bar");
		jnull = new Sub(null);
	}
	
	/**
	 * Test if j1 euqals j1
	 */
	@Test
	public void j1Equalsj1() {
		Assert.assertTrue(j1.equals(j1));
	}
	
	@Test
	public void j1Equalsj2() {
		Assert.assertFalse(j1.equals(j2));
	}
	
	@Test
	public void ObWithNullValueEualsJ1() {
		Assert.assertFalse(jnull.equals(j1));
	}
	
	@Test
	public void j1EqualsOtherObject() {
		Assert.assertFalse(j1.equals(new String("foobar")));
	}
}
