package com.doublesignal.sepm.jake.core.domain;

/**
 * The representation of a project member. It consists of a <code>userId</code>, 
 * <code>nickname</code>, and a <code>note</code>(i.e a comment, there is only one
 * note per project member, it is not distributed in the project)
 * @author johannes, domdorn, simon
 */
public class ProjectMember {

	private String userId ="";
	private String notes = "";
	private String nickname = "";

	private boolean active = true;

	/**
	 * Constructs a new <code>ProjectMember</code> with the given <code>userId</code>.
	 * @param userId
	 */
	public ProjectMember(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public String getNotes() {
		return notes;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * Tests if two <code>projectMembers</code>s are equal
	 * @return <code>true</code> iff all fields are equal.
	 */
	public boolean equals(Object obj) {
		if (obj == null || !this.getClass().equals(obj.getClass()))
			return false;

		ProjectMember that = (ProjectMember) obj;
		
		if (nickname == null && that.getNickname() != null)
			return false;
		if (nickname != null && !nickname.equals(that.getNickname()))
			return false;
		
		if (notes == null && that.getNotes() != null)
			return false;
		if (notes != null && !notes.equals(that.getNotes()))
			return false;
		
		if (userId == null && that.getUserId() != null)
			return false;
		if (userId != null && !userId.equals(that.getUserId()))
			return false;

		return true;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Returns a hash of the projectMember. All fields are used in the calculation.
	 */
	public int hashCode() {
		int result;
		result = (userId != null ? userId.hashCode() : 0);
		result = 31 * result + (notes != null ? notes.hashCode() : 0);
		result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
		return result;
	}
}
