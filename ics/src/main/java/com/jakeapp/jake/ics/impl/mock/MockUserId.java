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

	public boolean isOfCorrectUseridFormat() {
		if (userId.contains("@")
				&& userId.lastIndexOf("@") == userId.lastIndexOf("@")
				&& userId.indexOf("@") > 0
				&& userId.indexOf("@") < userId.length() - 1)
			return true;
		return false;
	}
}
