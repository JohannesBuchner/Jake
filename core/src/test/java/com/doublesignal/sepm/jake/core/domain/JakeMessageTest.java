package com.doublesignal.sepm.jake.core.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.doublesignal.sepm.jake.ics.exceptions.InvalidUserIdException;

/**
 * Tests for JakeMessage
 * @author Simon
 *
 */
public class JakeMessageTest {
	
	private JakeMessage m1, m2, m3;
	private ProjectMember pm;
	private static final String content = "content";
	
	/**
	 * setup
	 */
	@Before
	public void setUp() {
		try {
			pm = new ProjectMember("member_1");
		} catch (InvalidUserIdException e) {
			Assert.fail(e.getMessage());
		}
		m1 = new JakeMessage(pm, pm, content);
		m2 = new JakeMessage(pm, pm, "other content");
		m3 = new JakeMessage(pm, pm, content); 
		}

	/**
	 * Test if two different messages are not equal.
	 */
	@Test
	public void m1NotEqualsm2() {
		Assert.assertFalse(m1.equals(m2));
	}
	
	@Test
	public void m1Equalsm3() {
		Assert.assertTrue(m1.equals(m3));
	}
}
