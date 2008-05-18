package com.doublesignal.sepm.jake.core.domain;

import com.doublesignal.sepm.jake.ics.exceptions.InvalidUserIdException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidNicknameException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidCharactersException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InputLenghtException;


/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 8, 2008
 * Time: 11:04:48 PM
 */
public class ProjectMember
{

    private String UserId;
    private String Notes;
    private String Nickname;


    public ProjectMember(String userId) throws InvalidUserIdException
    {
        UserId = userId;
    }

    public String getUserId() {
        return UserId;
    }

    public String getNotes() {
        return Notes;
    }

    public String getNickname() throws InvalidNicknameException
    {
        return Nickname;
    }



    public void setNotes(String notes) throws InputLenghtException, InvalidCharactersException
    {
        Notes = notes;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public boolean equals(Object o) {
        /* TODO */
        return super.equals(o);
    }
}
