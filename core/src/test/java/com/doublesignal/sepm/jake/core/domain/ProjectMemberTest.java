package com.doublesignal.sepm.jake.core.domain;

import org.junit.Test;
import org.junit.Assert;

/**
 * @author domdorn, johannes
 */
public class ProjectMemberTest
{
	String validUsername = "validusername@domain.com";
	String notesInput = "Das ist ein Notes Input Test";
	String validNickname = "Peter";
	
	@Test
	public void createValidMemberTest()
	{
		try
		{
			new ProjectMember(validUsername);
		}
		catch (Exception e)
		{
			Assert.fail("cought general exception");
		}
	}
	@Test
	public void setGetNotesTest()
	{
		ProjectMember member = new ProjectMember(validUsername);
		member.setNotes(notesInput);

		Assert.assertTrue(member.getNotes().equals(notesInput));
	}
	
	@Test
	public void setValidNicknameTest()
	{
		ProjectMember member = new ProjectMember(validUsername);
		member.setNickname(validNickname);

		Assert.assertTrue(member.getNickname().equals(validNickname));
	}
	
	@Test
	public void checkEqualsImplemented()
	{

		ProjectMember mema = null;
		ProjectMember memb = null;
		try
		{
			mema = new ProjectMember(validUsername);
			memb = new ProjectMember(validUsername);

			mema.setNickname(validNickname);
			memb.setNickname(validNickname);

			mema.setNotes(notesInput);
			memb.setNotes(notesInput);
		}
		catch (Exception e)
		{
			Assert.fail();
		}

		Assert.assertEquals(mema, memb);
	}
	
	@Test
	public void notEqualWithString() {
		ProjectMember p = new ProjectMember("user");
		Assert.assertFalse(p.equals(new String()));
	}

}
