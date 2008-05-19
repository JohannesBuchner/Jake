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
		if(userId == null) throw new InvalidUserIdException("UserId may not be null");
		if(userId.length() == 0) throw new InvalidUserIdException("UserId may not be empty");
		UserId = userId;
    }

    public String getUserId() {
        return UserId;
    }

    public String getNotes() {
        return Notes;
    }

    public String getNickname()
    {
        return Nickname;
    }



    public void setNotes(String notes) throws InputLenghtException, InvalidCharactersException
    {
        Notes = notes;
    }

    public void setNickname(String nickname) throws InvalidNicknameException
	{
		if(nickname == null) throw new InvalidNicknameException("Nickname must not be null");
		if(nickname.length() > 50) throw new InvalidNicknameException("Nickname must be shorter than 50 chars.");

		Nickname = nickname;
    }

	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		ProjectMember that = (ProjectMember) o;

		if (Nickname != null ? !Nickname.equals(that.Nickname) : that.Nickname != null)
		{
			return false;
		}
		if (Notes != null ? !Notes.equals(that.Notes) : that.Notes != null)
		{
			return false;
		}
		if (UserId != null ? !UserId.equals(that.UserId) : that.UserId != null)
		{
			return false;
		}

		return true;
	}

	public int hashCode()
	{
		int result;
		result = (UserId != null ? UserId.hashCode() : 0);
		result = 31 * result + (Notes != null ? Notes.hashCode() : 0);
		result = 31 * result + (Nickname != null ? Nickname.hashCode() : 0);
		return result;
	}
}
