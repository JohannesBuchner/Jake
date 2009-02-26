package com.jakeapp.jake.ics;

import junit.framework.Assert;

import org.junit.Test;

import com.jakeapp.jake.ics.impl.mock.MockUserId;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;


public class TestXmppUserId {

	private XmppUserId shortUserId = new XmppUserId("foo@bar");

	private XmppUserId longUserId = new XmppUserId("johannes.buchner@my.favorite.host");

	@Test
	public void testShortHost() throws Exception {
		Assert.assertEquals("bar", shortUserId.getHost());
	}

	@Test
	public void testLongHost() throws Exception {
		Assert.assertEquals("my.favorite.host", longUserId.getHost());
	}

	@Test
	public void testShortUserName() throws Exception {
		Assert.assertEquals("foo", shortUserId.getUsername());
	}

	@Test
	public void testLongUserName() throws Exception {
		Assert.assertEquals("johannes.buchner", longUserId.getUsername());
	}

	@Test
	public void testSameUserDifferentRessource() throws Exception {
		Assert.assertTrue(XmppUserId.isSameUser(new XmppUserId("foo@bar/Pidgin"),
				new XmppUserId("foo@bar/MIRC")));
	}

	@Test
	public void testEqualsDifferentRessource1() {
		Assert.assertEquals(new XmppUserId("foo@bar/Pidgin"), new XmppUserId(
				"foo@bar/MIRC"));
	}

	@Test
	public void testEqualsDifferentRessource2() {
		Assert.assertEquals(new XmppUserId("foo@bar/MIRC"), new MockUserId(
				"foo@bar/Pidgin"));
	}

	@Test
	public void testEqualsDifferentRessource2a() {
		Assert.assertFalse(new MockUserId("foo@bar/Pidgin").equals(new XmppUserId(
				"foo@bar/MIRC")));
	}

	@Test
	public void testEqualsDifferentRessource3() {
		Assert.assertFalse(new MockUserId("foo@baz/Pidgin")
				.equals(new XmppUserId("foo@bar/MIRC")));
	}

	@Test
	public void testEqualsDifferentRessource4() {
		Assert.assertFalse(new XmppUserId("foo@bar/MIRC").equals(new MockUserId(
				"foo@baz/Pidgin")));
	}

	@Test
	public void testEqualsUsesToString() {
		Assert.assertEquals(longUserId, longUserId.getUserId());
	}

	@Test
	public void testToString() throws Exception {
		Assert.assertEquals(longUserId.getUserId(), longUserId.toString());
	}
}
