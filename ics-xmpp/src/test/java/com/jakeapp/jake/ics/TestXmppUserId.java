package com.jakeapp.jake.ics;

import junit.framework.Assert;

import org.junit.Test;

import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;


public class TestXmppUserId {

	private XmppUserId shortUserId = new XmppUserId("foo@bar");

	private XmppUserId longUserId = new XmppUserId(
			"johannes.buchner@my.favorite.host");

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
}
