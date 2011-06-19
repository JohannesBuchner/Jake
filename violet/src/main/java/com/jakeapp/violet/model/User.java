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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!userid.equals(obj.toString()))
			return false;
		return true;
	}

}