package com.doublesignal.sepm.jake.core.domain;

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


	public void setNotes(String notes)
	{
		Notes = notes;
	}

	public void setNickname(String nickname)
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
