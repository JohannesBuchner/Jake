package com.jakeapp.violet.actions.global;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.impl.mock.MockUserId;
import com.jakeapp.violet.di.IUserIdFactory;


public class MockUserIdFactory implements IUserIdFactory {

	@Override
	public UserId get(String user) {
		return new MockUserId(user);
	}

}
