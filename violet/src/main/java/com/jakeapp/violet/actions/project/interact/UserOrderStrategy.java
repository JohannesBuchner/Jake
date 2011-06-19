package com.jakeapp.violet.actions.project.interact;

import java.util.Collection;

import com.jakeapp.violet.model.User;

public interface UserOrderStrategy {

	/**
	 * select and order the users
	 * 
	 * @param origin
	 *            the user that made the commit
	 * @param users
	 *            all other users
	 * @return a order in which to try pulling files
	 */
	public Collection<User> selectUsers(User origin, Collection<User> users);

}
