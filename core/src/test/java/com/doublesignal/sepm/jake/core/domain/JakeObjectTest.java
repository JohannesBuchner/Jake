package com.doublesignal.sepm.jake.core.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class JakeObjectTest {
	
	private JakeObject j1, j2, jnull;
	
	/**
	 * set up
	 */
	@Before
	public void setUp() {
		j1 = new JakeObject("foo");
		j2 = new JakeObject("bar");
		jnull = new JakeObject(null);
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
}
