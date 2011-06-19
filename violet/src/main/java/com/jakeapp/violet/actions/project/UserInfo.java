package com.jakeapp.violet.actions.project;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.violet.model.User;

/**
 * Detailed information about the user
 * 
 * @see User
 */
public class UserInfo {

	private Boolean online;

	private String nickName;

	private String firstName;

	private String lastName;

	private UserId userid;

	public Boolean getOnline() {
		return online;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public UserId getUserid() {
		return userid;
	}

	public void setUserid(UserId userid) {
		this.userid = userid;
	}

}
