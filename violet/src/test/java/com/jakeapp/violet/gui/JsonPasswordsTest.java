package com.jakeapp.violet.gui;


import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.test.TmpdirEnabledTestCase;

public class JsonPasswordsTest extends TmpdirEnabledTestCase {

	private File f;

	private JsonPasswords pw;

	private String user = "foobar.this.is.a.long.username+\" weird \\\t characters ";

	private String userpw = "foobar.this.is.a.long.password </html\\\\~>";

	@Before
	public void setUp() throws Exception {
		super.setup();
		f = new File(this.tmpdir, "mypasswords");
		pw = new JsonPasswords(f);
	}

	@Test
	public void run() throws IOException {
		Assert.assertFalse(f.exists());
		Assert.assertNull(pw.loadForUser(user));
		Assert.assertTrue(f.length() < user.length());
		pw.forgetForUser(user);
		Assert.assertTrue(f.length() < user.length());
		pw.storeForUser(user, userpw);
		Assert.assertTrue(f.length() > user.length());
		Assert.assertEquals(userpw, pw.loadForUser(user));
		pw.forgetForUser(user);
		Assert.assertTrue(f.length() < user.length());
		Assert.assertTrue(f.exists());
	}
}
