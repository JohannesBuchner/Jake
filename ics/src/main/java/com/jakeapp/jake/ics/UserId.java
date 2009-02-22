package com.jakeapp.jake.ics;

/**
 * Identifies a user within the ICS implementation
 * 
 * @author johannes
 */
abstract public class UserId {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass()) {
			// added string compare magic:
			if (obj.getClass().equals(String.class) && this.userId.equals(obj))
				return true;
			return false;
		}
		UserId other = (UserId) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	public UserId(String userId) {
		super();
		this.userId = userId;
	}


	public String getUserId() {
		return this.userId;
	}

	protected String userId;

	abstract public boolean isOfCorrectUseridFormat();

	@Override
	public String toString() {
		return getUserId();
	}
}
