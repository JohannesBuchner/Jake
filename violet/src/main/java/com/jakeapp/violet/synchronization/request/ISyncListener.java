package com.jakeapp.violet.synchronization.request;

import com.jakeapp.violet.model.User;

public interface ISyncListener {

	/**
	 * we received a poke. Do a LogSync when you get a chance!
	 * @param user
	 */
	void poke(User user);

	/**
	 * fyi, we started receiving logs from user user
	 * @param user
	 */
	void startReceiving(User user);

	/**
	 * fyi, we finished receiving logs from user user
	 * @param user
	 */
	void finishedReceiving(User user);

}
