package com.jakeapp.violet.gui;

import java.io.IOException;
import java.util.Observable;

import com.jakeapp.violet.model.Model;

/**
 * Password storage -- implementations must not keep passwords in memory.
 * 
 * This is for the GUI, the core should not use it, it should only receive
 * passwords.
 */
public abstract class Passwords extends Observable implements Model {

	/**
	 * load password, if available
	 * 
	 * @param user
	 *            User id
	 * @return null if not found
	 */
	public abstract String loadForUser(String user);

	/**
	 * save password
	 * 
	 * @param user
	 *            User id
	 * @param pw
	 *            Password
	 * @throws IOException
	 */
	public abstract void storeForUser(String user, String pw)
			throws IOException;

	/**
	 * remove password if stored.
	 * 
	 * @param user
	 *            User id
	 * @throws IOException
	 */
	public abstract void forgetForUser(String user) throws IOException;

}
