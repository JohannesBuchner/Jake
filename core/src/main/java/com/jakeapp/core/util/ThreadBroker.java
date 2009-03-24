package com.jakeapp.core.util;

import com.jakeapp.core.DarkMagic;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

/**
 * This class allows submitting tasks to the thread for later execution within
 * the thread.
 *
 * @author johannes
 * @see InjectableTask
 */
@DarkMagic
public class ThreadBroker implements Runnable {
	private static final Logger log = Logger.getLogger(ThreadBroker.class);
	private final List<InjectableTask<?>> tasks =
					new CopyOnWriteArrayList<InjectableTask<?>>();
	private Semaphore s = new Semaphore(0);
	private boolean running = true;

	/**
	 * stop the execution of tasks
	 */
	protected void cancel() {
		this.running = false;
	}

	/**
	 * submit a task to the main thread for execution. This method blocks until
	 * a result is calculated. Any occurring Exception is thrown.
	 *
	 * @param <T>  return value type
	 * @param task the task to execute
	 * @return the return value of task.calculate
	 * @throws Exception any Exception that task.calculate throws
	 */
	public <T> T doTask(InjectableTask<T> task) throws Exception {
		log.debug("submitting task");
		this.tasks.add(task);
		this.s.release();
		return task.getResult();
	}

	public void run() {
		log.trace("running");
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
				// FIXME: this shouldn't be needed!!!?
				// why this keeps throwing NoSuchElementExceptions?
				synchronized (this.tasks) {
					if (this.tasks.isEmpty()) {
						log.warn("Tried to run Task but no Task available!!");
					} else {
						InjectableTask<?> task = this.tasks.remove(0);
						runTask(task);
					}
				}
			} catch (Exception e) {
				log.error("task died!", e);
			}
			log.debug("task done");
		}
		log.trace("quitting broker");
	}

	protected void runTask(InjectableTask<?> task) {
		task.run();
	}
}