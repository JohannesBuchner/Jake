package com.doublesignal.sepm.jake.core.domain;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>NoteObject</code>
 * @author Simon, johannes
 *
 */
public class NoteObjectTest {
	
	private NoteObject n1, n2, nNull;

	@Before
	public void setUp() throws Exception {
		n1 = new NoteObject("foo", "foobar");
		n2 = new NoteObject("bar", "barfoo");
		nNull = new NoteObject(null, null);
	}
	
	@Test
	public void n1Equalsn1() {
		Assert.assertTrue(n1.equals(n1));
	}
	
	@Test
	public void n1Equalsn2() {
		Assert.assertFalse(n1.equals(n2));
	}
	
	@Test
	public void nNullEqualsn1() {
		Assert.assertFalse(nNull.equals(n1));
	}
	@Test
	public void testCreateNoteObject() throws InterruptedException{
		NoteObject n = NoteObject.createNoteObject("myuserid@host", null);
		Assert.assertTrue(n.getName().startsWith("note:myuserid@host"));
		Assert.assertNull(n.getContent());
		
		n = NoteObject.createNoteObject("myuserid@host", "random");
		Assert.assertTrue(n.getName().startsWith("note:myuserid@host"));
		Assert.assertEquals(n.getContent(), "random");
		
		Thread.sleep(1000);
		
		n2 = NoteObject.createNoteObject("myuserid@host", "random");
		Assert.assertFalse(n2.equals(n));
	}
}
