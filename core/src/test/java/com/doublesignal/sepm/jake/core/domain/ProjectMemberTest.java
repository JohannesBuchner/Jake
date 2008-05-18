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
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 18, 2008
 * Time: 4:21:19 AM
 */
public class ProjectMemberTest {
    String validUsername = "validusername@domain.com";
    String tooLongUsername = "thisisawaytoolongusernamethisisawaytoolongusernamethisisawaytoolongusername" +
            "thisisawaytoolongusernamethisisawaytoolongusernamethisisawaytoolongusernamethisisawaytoolongusername";
    String usernameWithWhitespaces = "Username with whitespaces";

    String notesInput = "Das ist ein Notes Input Test";

    String validNickname = "Peter";

    String tooLongNickname = "HansChristianAndersonDerMitDenGänsenFliegtUndSowieso";


    @Before
    public void Setup() throws Exception {

    }

    @After
    public void TearDown() throws Exception {

    }


    @Test
    public void createValidMemberTest() {
        try {
            ProjectMember member = new ProjectMember(validUsername);
        } catch (InvalidUserIdException e) {
            Assert.fail();
        }
    }

    @Test(expected = InvalidUserIdException.class)
    void emptyUsernameTest() throws InvalidUserIdException {
        String Username = "";

        ProjectMember test = new ProjectMember(Username);

    }

    @Test(expected = InvalidUserIdException.class)
    public void UsernameNullTest() throws InvalidUserIdException
    {
        ProjectMember test = new ProjectMember(null);
    }


    @Test(expected = InvalidUserIdException.class)
    void whiteSpaceUsernameTest() throws InvalidUserIdException {
        ProjectMember test = new ProjectMember(usernameWithWhitespaces);
    }

    @Test(expected = InvalidUserIdException.class)
    void tooLongusernameTest() throws InvalidUserIdException {
        ProjectMember test = new ProjectMember(tooLongUsername);
    }


    @Test
    void setGetNotesTest() {
        try {
            ProjectMember member = new ProjectMember(validUsername);
            member.setNotes(notesInput);

            Assert.assertTrue(member.getNotes().equals(notesInput));
        }
        catch (InvalidCharactersException e) {
            Assert.fail();

        }
        catch (InvalidUserIdException
                e) {
            Assert.fail();

        } catch (InputLenghtException
                e) {
            Assert.fail();
        }
    }

    @Test
    void setValidNicknameTest()
    {
        try {
            ProjectMember member = new ProjectMember(validUsername);
            member.setNickname(validNickname);

            Assert.assertTrue(member.getNickname().equals(validUsername));
        } catch (InvalidUserIdException e) {
            Assert.fail();
        } catch (InvalidNicknameException e) {
            Assert.fail();
        }

    }

    @Test(expected = InvalidNicknameException.class)
    void NicknameNotNullTest() throws InvalidNicknameException
    {
        try {
            ProjectMember member = new ProjectMember(validUsername);
            member.setNickname(null);
        } catch (InvalidUserIdException e) {
            Assert.fail();
        }


    }


    @Test(expected = InvalidNicknameException.class)
    void setTooLongNicknameTest() throws InvalidNicknameException
    {
        try {
            ProjectMember member = new ProjectMember(validUsername);
            member.setNickname(tooLongNickname);
        } catch (InvalidUserIdException e) {
            Assert.fail();
        }
    }

    @Test
    void checkEqualsImplemented()
    {

        ProjectMember mema = null;
        ProjectMember memb = null;
        try {
            mema = new ProjectMember(validUsername);
            memb = new ProjectMember(validUsername);

            mema.setNickname(validNickname);
            memb.setNickname(validNickname);

            mema.setNotes(notesInput);
            memb.setNotes(notesInput);
        } catch (InvalidUserIdException e) {
            Assert.fail();
        } catch (InputLenghtException e) {
            Assert.fail();
        } catch (InvalidCharactersException e) {
            Assert.fail();
        }

        Assert.assertEquals(mema, memb);      
    }

}
