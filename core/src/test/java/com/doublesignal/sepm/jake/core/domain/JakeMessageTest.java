package com.doublesignal.sepm.jake.core.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for JakeMessage
 * @author Simon, johannes
 *
 */
public class JakeMessageTest {
	
	private JakeMessage m1, m2, m3, mNull;
	private ProjectMember pm;
	private static final String content = "content";
	
	/**
	 * setup
	 */
	@Before
	public void setUp() {
		pm = new ProjectMember("member_1");
		m1 = new JakeMessage(pm, pm, content);
		m2 = new JakeMessage(pm, pm, "other content");
		m3 = new JakeMessage(pm, pm, content); 
		mNull = new JakeMessage(null, null, null);
	}

	/**
	 * Test if two different messages are not equal.
	 */
	@Test
	public void m1NotEqualsm2() {
		Assert.assertFalse(m1.equals(m2));
	}
	
	/**
	 * Test if two equal messages are equal
	 */
	@Test
	public void m1Equalsm3() {
		Assert.assertTrue(m1.equals(m3));
	}
	
	/**
	 * Test if a Message with null values is equal to another message. Should
	 * not throw any exceptions.
	 */
	@Test
	public void nullEqualsMessage() {
		Assert.assertFalse(mNull.equals(m1));
	}
	
	@Test
	public void m1NotEqualsString() {
		Assert.assertFalse(m1.equals(new String("asdf")));
	}
}
