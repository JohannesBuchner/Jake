package com.jakeapp.gui.swing.helpers;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: studpete
 */
public class JakeExecutor extends ThreadPoolExecutor {

	private static JakeExecutor instance;

	public static JakeExecutor getInstance() {
		if (instance == null) {
			instance = new JakeExecutor();
		}

		return instance;
	}

	/**
	 * Calls ThreadPoolExecutor.execute.
	 *
	 * @param command
	 */
	public static void exec(Runnable command) {
		getInstance().execute(command);
	}

	// private for singleton
	private JakeExecutor() {
		super(4, 15, 5, TimeUnit.SECONDS, new SynchronousQueue());
	}
}
