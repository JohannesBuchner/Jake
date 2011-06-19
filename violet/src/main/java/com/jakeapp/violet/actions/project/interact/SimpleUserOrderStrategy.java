package com.jakeapp.violet.actions.project.interact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jakeapp.violet.model.User;

public class SimpleUserOrderStrategy implements UserOrderStrategy {

	@Override
	public Collection<User> selectUsers(User origin, Collection<User> users) {
		List<User> s = new ArrayList<User>();
		s.add(origin);
		s.addAll(users);
		return s;
	}
}
