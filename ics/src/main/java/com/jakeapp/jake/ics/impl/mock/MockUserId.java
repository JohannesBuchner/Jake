package com.jakeapp.jake.ics.impl.mock;

import com.jakeapp.jake.ics.UserId;

/**
 * Identifies a user within the ICS implementation
 * 
 * @author johannes
 */
public class MockUserId extends UserId {

	public MockUserId(String userId) {
		super(userId);
	}

	public MockUserId(UserId userId) {
		super(userId.getUserId());
	}

	@Override
	public boolean isOfCorrectUseridFormat() {
		if (this.userId.contains("@")
				&& this.userId.lastIndexOf("@") == this.userId.lastIndexOf("@")
				&& this.userId.indexOf("@") > 0
				&& this.userId.indexOf("@") < this.userId.length() - 1)
			return true;
		return false;
	}
}
