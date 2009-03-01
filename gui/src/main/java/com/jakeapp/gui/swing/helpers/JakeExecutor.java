package com.jakeapp.gui.swing.helpers;

import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * JakeExecutor - Main Thread Pool to start Tasks to the Core.
 * @author studpete
 */
public class JakeExecutor extends ThreadPoolExecutor {
	private static JakeExecutor instance;

	private HashMap<Class, Runnable> runningTasks = new HashMap<Class, Runnable>();

	public static JakeExecutor getInstance() {
		if (instance == null) {
			instance = new JakeExecutor();
		}

		return instance;
	}

	/**
	 * Calls ThreadPoolExecutor.execute.
	 *
	 * @param command	Commaand that should be executed.
	 */
	public static void exec(Runnable command) {

		getInstance().execute(command);
		getInstance().addRunningTask(command);
	}

	private void addRunningTask(Runnable command) {
		runningTasks.put(command.getClass(), command);
	}

	// private for singleton
	private JakeExecutor() {
		super(4, 15, 5, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}

	/**
	 * Checks if a Task is currently running.
	 * @param aclass
	 * @return
	 */
	public static boolean isTaskRunning(Class aclass) {
		return getInstance().runningTasks.get(aclass) != null;
	}

	public static void removeTask(Class aclass) {
		getInstance().runningTasks.remove(aclass);
	}
}
