/**
 * 
 */
package com.jakeapp.core.util;

import org.apache.log4j.Logger;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * A task than can be scheduled for execution within another thread.
 * 
 * @author johannes
 * @param <T> return value type
 */
public abstract class InjectableTask<T> implements Runnable {

	private static final Logger log = Logger.getLogger(InjectableTask.class);

	private StackTraceElement[] callerStackTrace = new Throwable().getStackTrace();
	
	private String name;
	
	/**
	 * perefer {@link InjectableTask#InjectableTask(String)}
	 */
	public InjectableTask() {
		this(getParentName());
	}

	private static String getParentName() {
		try {
			throw new Exception();
		} catch (Exception e) {
			return e.getStackTrace()[3].getMethodName();
		}
	}

	/**
	 * give the task a name for debugging purposes
	 * @param name
	 */
	public InjectableTask(String name) {
		this.name = name;
		log.debug("Scheduling task " + this.name);
	}

	private Semaphore s = new Semaphore(0);

	private T result;

	private Exception exception = null;

	/**
	 * override this to do your work
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract T calculate() throws Exception;

	/**
	 * called by main thread
	 */
	public void run() {
		log.debug("Running task " + this.name);
		try {
			this.result = calculate();
		} catch (Exception e) {
			this.exception = e;
		}
		log.debug("Task " + this.name + " done");
		this.s.release();
	}

	/**
	 * await result
	 * 
	 * @return
	 * @throws Exception
	 */
	public T getResult() throws Exception {
		while (true) {
			try {
				// HACK TODO EVIL Timeout !?
				this.s.tryAcquire(30, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				continue;
				// what shall we do with the drunken sailor?
			}
			if (this.exception != null) {
				log.debug("returning exception of task " + this.name);
				throw this.exception;
			} else {
				log.debug("returning result of task " + this.name);
				return this.result;
			}
		}
	}
}