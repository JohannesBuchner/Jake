package com.jakeapp.violet.actions.global.serve;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.User;

public interface ISyncListener {

	/**
	 * we received a poke. Do a LogSync when you get a chance!
	 * 
	 * @param user
	 */
	void poke(User user);

	/**
	 * fyi, we started receiving logs from user user
	 * 
	 * @param user
	 */
	void startReceiving(User user);

	/**
	 * fyi, we finished receiving logs from user user
	 * 
	 * @param user
	 */
	void finishedReceiving(User user);

	/**
	 * Should we accept a filerequest for this jakeObject by this user?
	 * 
	 * @param user
	 * @param jakeObject
	 */
	boolean acceptSending(UserId user, JakeObject jakeObject);

	/**
	 * Sending a file to the user user failed. There is no reason to be
	 * concerned though, it happens.
	 * 
	 * @param user
	 * @param fileName
	 * @param error
	 */
	void sendingFailed(User user, String fileName, String error);

	/**
	 * We successfully sent a file to the user user.
	 * 
	 * @param user
	 * @param fileName
	 */
	void sendingSucceeded(User user, String fileName);

	/**
	 * Just a update on the progress of a sending transfer.
	 * 
	 * @param user
	 * @param fileName
	 * @param status
	 * @param progress
	 */
	void sendingUpdateProgress(User user, String fileName, String status,
			double progress);

}
