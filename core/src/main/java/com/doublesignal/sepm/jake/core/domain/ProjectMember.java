package com.doublesignal.sepm.jake.core.domain;

import com.doublesignal.sepm.jake.core.domain.exceptions.InputLenghtException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidCharactersException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidNicknameException;


/**
 * @author johannes, domdorn
 */
public class ProjectMember
{

	private String UserId;
	private String Notes;
	private String Nickname;


	public ProjectMember(String userId)
	{
		UserId = userId;
	}

	public String getUserId()
	{
		return UserId;
	}

	public String getNotes()
	{
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
		Nickname = nickname;
	}

	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ProjectMember that = (ProjectMember) o;

		if (Nickname.equals(that.Nickname) && 
			Notes.equals(that.Notes) && 
			UserId.equals(that.UserId)
		){
			return true;
		}
		
		return false;
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
