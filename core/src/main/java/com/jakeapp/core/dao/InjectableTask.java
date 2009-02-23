/**
 * 
 */
package com.jakeapp.core.dao;

import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

public abstract class InjectableTask<T> implements Runnable {

	private static final Logger log = Logger.getLogger(InjectableTask.class);

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
	 */
	public abstract T calculate() throws Exception;

	/**
	 * called by main thread
	 */
	public void run() {
		log.debug("Running task " + name);
		try {
			result = calculate();
		} catch (Exception e) {
			exception = e;
		}
		log.debug("Task " + name + " done");
		s.release();
	}

	/**
	 * await result
	 * 
	 * @return
	 * @throws Exception
	 */
	public T getResult() throws Exception {
		boolean done = false;
		while (true) {
			try {
				s.acquire();
			} catch (InterruptedException e) {
				continue;
				// what shall we do with the drunken sailor?
			}
			log.debug("returning result of task " + name);
			if (exception != null)
				throw exception;
			else
				return result;
		}
	}
}