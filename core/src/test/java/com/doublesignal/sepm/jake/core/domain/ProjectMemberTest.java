package com.doublesignal.sepm.jake.core.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import com.doublesignal.sepm.jake.ics.exceptions.InvalidUserIdException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidCharactersException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InputLenghtException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidNicknameException;

/**
 * @author domdorn
 */
public class ProjectMemberTest
{
	String validUsername;
	String tooLongUsername;
	String usernameWithWhitespaces;
	String notesInput;
	String validNickname;
	String tooLongNickname;

	{
		validUsername = "validusername@domain.com";
		tooLongUsername = "thisisawaytoolongusernamethisisawaytoolongusernamethisisawaytoolongusername" +
				"thisisawaytoolongusernamethisisawaytoolongusernamethisisawaytoolongusernamethisisawaytoolongusername";
		usernameWithWhitespaces = "Username with whitespaces";
		notesInput = "Das ist ein Notes Input Test";
		validNickname = "Peter";
		tooLongNickname = "HansChristianAndersonDerMitDenGänsenFliegtUndSowieso";
	}


	@Before
	public void Setup() throws Exception
	{
		// nothing to setup here
	}


	@After
	public void TearDown() throws Exception
	{
		// nopthint to tear down here
	}


	@Test
	public void createValidMemberTest()
	{
		try
		{
			ProjectMember member = new ProjectMember(validUsername);
		}
		catch (InvalidUserIdException e)
		{
			Assert.fail("cought InvalidUserException although member should be valid");
		}
		catch (Exception e)
		{
			Assert.fail("cought general exception");
		}
	}

	@Test(expected = InvalidUserIdException.class)
	public void emptyUsernameTest() throws InvalidUserIdException
	{
		String Username = "";

		ProjectMember test = new ProjectMember(Username);

	}

	@Test(expected = InvalidUserIdException.class)
	public void UsernameNullTest() throws InvalidUserIdException
	{
		ProjectMember test = new ProjectMember(null);
	}


	@Test(expected = InvalidUserIdException.class)
	public void whiteSpaceUsernameTest() throws InvalidUserIdException
	{
		ProjectMember test = new ProjectMember(usernameWithWhitespaces);
	}

	@Test(expected = InvalidUserIdException.class)
	public void tooLongusernameTest() throws InvalidUserIdException
	{
		ProjectMember test = new ProjectMember(tooLongUsername);
	}


	@Test
	public void setGetNotesTest()
	{
		try
		{
			ProjectMember member = new ProjectMember(validUsername);
			member.setNotes(notesInput);

			Assert.assertTrue(member.getNotes().equals(notesInput));
		}
		catch (InvalidCharactersException e)
		{
			Assert.fail();

		}
		catch (InvalidUserIdException
				e)
		{
			Assert.fail();

		}
		catch (InputLenghtException
				e)
		{
			Assert.fail();
		}
	}







	@Test
	public void setValidNicknameTest()
	{
		try
		{
			ProjectMember member = new ProjectMember(validUsername);
			member.setNickname(validNickname);

			Assert.assertTrue(member.getNickname().equals(validNickname));
		}
		catch (InvalidUserIdException e)
		{
			Assert.fail("didn't got a valid username");
		}
		catch (InvalidNicknameException e)
		{
			Assert.fail("didn't get a valid nickname");
		}

	}

	@Test(expected = InvalidNicknameException.class)
	public void NicknameNotNullTest() throws InvalidNicknameException
	{
		try
		{
			ProjectMember member = new ProjectMember(validUsername);
			member.setNickname(null);
		}
		catch (InvalidUserIdException e)
		{
			Assert.fail();
		}


	}


	@Test(expected = InvalidNicknameException.class)
	public void setTooLongNicknameTest() throws InvalidNicknameException
	{
		try
		{
			ProjectMember member = new ProjectMember(validUsername);
			member.setNickname(tooLongNickname);
		}
		catch (InvalidUserIdException e)
		{
			Assert.fail();
		}
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
		catch (InvalidUserIdException e)
		{
			Assert.fail();
		}
		catch (InputLenghtException e)
		{
			Assert.fail();
		}
		catch (InvalidCharactersException e)
		{
			Assert.fail();
		}
		catch (InvalidNicknameException e)
		{
			Assert.fail();
		}

		Assert.assertEquals(mema, memb);
	}

}
