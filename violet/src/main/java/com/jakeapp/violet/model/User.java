package com.jakeapp.violet.model;

public class User {
	private String userid;

	public User(String userid) {
		if (userid == null)
			throw new NullPointerException();
		this.userid = userid;
	}

	public String getUserId() {
		return userid;
	}

	@Override
	public int hashCode() {
		return userid.hashCode();
	}

	@Override
	public String toString() {
		return userid.toString();
	}
}