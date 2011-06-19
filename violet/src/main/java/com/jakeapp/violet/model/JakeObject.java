package com.jakeapp.violet.model;

public class JakeObject {

	private String relpath;

	public JakeObject(String relpath) {
		if (relpath == null)
			throw new NullPointerException();
		this.relpath = relpath;
	}

	public String getRelPath() {
		return relpath;
	}

	@Override
	public int hashCode() {
		return relpath.hashCode();
	}

	@Override
	public String toString() {
		return relpath.toString();
	}
}
