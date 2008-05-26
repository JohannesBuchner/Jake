package com.doublesignal.sepm.jake.core.domain;


import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for LogEntry
 * @author Simon
 *
 */
public class LogEntryTest {
	
	private LogEntry l1, l2, lNull;

	@Before
	public void setUp() throws Exception {
		l1 = new LogEntry(LogAction.NEW_VERSION, new Date(), "objectid", "foobar", "hash", "comment");
		l2 = new LogEntry(LogAction.TAG_REMOVE, new Date(), "otherobjectid", "foobar", "hash", "comment");
		lNull = new LogEntry(null, null, null, null, null, null);
	}
	
	@Test
	public void l1Equalsl1() {
		Assert.assertTrue(l1.equals(l1));
	}
	
	@Test
	public void l1Equalsl2() {
		Assert.assertFalse(l1.equals(l2));
	}
	
	@Test
	public void lNullEqualsl1() {
		Assert.assertFalse(lNull.equals(l1));
	}

}
