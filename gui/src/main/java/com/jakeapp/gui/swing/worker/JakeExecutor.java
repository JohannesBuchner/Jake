package com.jakeapp.gui.swing.worker;

import com.jakeapp.gui.swing.dialogs.debugging.ActiveTasks;
import org.apache.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * JakeExecutor - Main Thread Pool to start Tasks to the Core.
 *
 * @author studpete
 */
public class JakeExecutor extends ThreadPoolExecutor {
	private static final Logger log = Logger.getLogger(JakeExecutor.class);
	private static JakeExecutor instance;

	private LinkedHashMap<String, Runnable> runningTasks =
					new LinkedHashMap<String, Runnable>();

	public static JakeExecutor getInstance() {
		if (instance == null) {
			instance = new JakeExecutor();
		}

		return instance;
	}

	/**
	 * Calls ThreadPoolExecutor.execute.
	 *
	 * @param command Commaand that should be executed.
	 */
	public static void exec(Runnable command) {
		getInstance().execute(command);
		getInstance().addRunningTask(command);
	}

	private void addRunningTask(Runnable command) {
		log.debug("Register Task: " + command.getClass().getSimpleName());
		runningTasks.put(command.getClass().getSimpleName(), command);
		fireTasksChanged();
	}

	// private for singleton
	private JakeExecutor() {
		super(4, 15, 5, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}

	/**
	 * Checks if a Task is currently running.
	 *
	 * @param str
	 * @return
	 */
	public static boolean isTaskRunning(String str) {
		log.debug("IsTaskRunning? " + str + ": " + getInstance().runningTasks.get(str));
		log.debug("Tasks running: " + getInstance().runningTasks.size());
		return getInstance().runningTasks.get(str) != null;
	}

	public static void removeTask(String str) {
		getInstance().runningTasks.remove(str);
		fireTasksChanged();
	}

	private static void fireTasksChanged() {ActiveTasks.tasksUpdated();}

	public static void removeTask(Class aclass) {
		getInstance().runningTasks.remove(aclass.getSimpleName());
	}

	public static LinkedHashMap<String, Runnable> getTasks() {
		return getInstance().runningTasks;
	}
}
