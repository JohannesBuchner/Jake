package com.jakeapp.gui.swing.worker;

import com.jakeapp.gui.swing.callbacks.TaskChangedCallback;
import com.jakeapp.gui.swing.dialogs.debugging.ActiveTasks;
import com.jakeapp.gui.swing.worker.tasks.IJakeTask;
import com.jakeapp.gui.swing.xcore.EventCore;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

	private final ConcurrentHashMap<Integer, IJakeTask> runningTasks =
					new ConcurrentHashMap<Integer, IJakeTask>();

	public static JakeExecutor getInstance() {
		if (instance == null) {
			instance = new JakeExecutor();
		}

		return instance;
	}

	/**
	 * Calls ThreadPoolExecutor.execute.
	 *
	 * @param task Command that should be executed.
	 */
	public static void exec(IJakeTask task) {
		getInstance().execute(task);
		getInstance().addRunningTask(task);
	}

	private void addRunningTask(IJakeTask task) {
		log.debug("Register Task: " + task.getClass().getSimpleName());
		runningTasks.put(task.hashCode(), task);
		fireTasksChanged(task, TaskChangedCallback.TaskOps.Started);
	}

	// private for singleton
	private JakeExecutor() {
		super(4, 15, 5, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}

	public static void removeTask(IJakeTask task) {
		getInstance().runningTasks.remove(task.hashCode());
		fireTasksChanged(task, TaskChangedCallback.TaskOps.Finished);
	}

	private static void fireTasksChanged(final IJakeTask task,
					final TaskChangedCallback.TaskOps op) {
		// make call threadsave!
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				EventCore.get().fireTasksChangedListener(task, op);

				// FIXME: use new interface TaskChanged
				ActiveTasks.tasksUpdated();
			}
		});
	}

	public static Map<Integer, IJakeTask> getTasks() {
		return getInstance().runningTasks;
	}

	public static boolean isTaskRunning(Class aclass) {
		for (IJakeTask task : getInstance().runningTasks.values()) {
			if (task.getClass().equals(aclass)) {
				return true;
			}
		}
		//return getInstance().runningTasks.containsKey(aclass.toString().hashCode());
		return false;
	}

	public static boolean hasTasksRunning() {
		return countTasksRunning() > 0;
	}

	/**
	 * Get Task on top of executor
	 *
	 * @return
	 */
	public static IJakeTask getLatestTask() {

		// HACK: make this less ugly!
		//synchronized (getInstance().runningTasks) {
			return (IJakeTask) getInstance().runningTasks.values().toArray()[getInstance()
							.runningTasks.size() - 1];
		//}
	}

	public static int countTasksRunning() {
		return getInstance().runningTasks.size();
	}
}
