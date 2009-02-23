package com.jakeapp.core.dao;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import com.jakeapp.core.DarkMagic;

/**
 * This class allows submitting tasks to the thread for later execution within
 * the thread.
 * 
 * @author johannes
 */
@DarkMagic
public class ThreadBroker implements Runnable {

	private static final Logger log = Logger.getLogger(ThreadBroker.class);

	private List<InjectableTask<?>> tasks = new LinkedList<InjectableTask<?>>();

	private Semaphore s = new Semaphore(0);

	private boolean running = true;

	/**
	 * stop the Execution
	 */
	protected void cancel() {
		this.running = false;
	}

	/**
	 * submit a task to the main thread for execution. This method blocks until
	 * a result is calculated. Any occurring Exception is thrown. 
	 * 
	 * @param <T>
	 *            return value type
	 * @param task
	 *            the task to execute
	 * @return the return value of task.calculate
	 * @throws Exception
	 *             any Exception that task.calculate throws
	 */
	public <T> T doTask(InjectableTask<T> task) throws Exception {
		log.debug("submitting task");
		this.tasks.add(task);
		this.s.release();
		return task.getResult();
	}

	public void run() {
		log.debug("running");
		while (this.running) {
			try {
				this.s.acquire();
			} catch (InterruptedException e) {
				continue;
			}
			if (!this.running || this.tasks.isEmpty())
				continue;
			log.debug("handling next task");
			try {
				this.tasks.remove(0).run();
			} catch (Exception e) {
				log.error("task died!", e);
			}
			log.debug("task done");
		}
	}

}
