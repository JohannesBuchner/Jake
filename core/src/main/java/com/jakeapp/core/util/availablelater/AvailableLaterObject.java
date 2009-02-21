package com.jakeapp.core.util.availablelater;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * provides a method of providing the result later. The
 * {@link AvailableLaterObject} is returned immediately.
 * <p/>
 * The supplied listener tells you when the result is done or had an error.
 * <p/>
 * In {@link #run()}, implement the method that takes time. call
 * {@link #set(Object)}() when your done or the methods of
 * {@link AvailabilityListener} to notify the progress.
 * 
 * @author johannes
 * @param <T>
 *            result type
 */
public abstract class AvailableLaterObject<T> implements Runnable {

	protected T innercontent;

	protected AvailabilityListener<T> listener;

	protected Semaphore s = new Semaphore(0);
	
	private AtomicBoolean alreadyStarted = new AtomicBoolean(false);

	/* server functions */
	protected void set(T o) {
		this.innercontent = o;
		getListener().finished(o);
	}

	public AvailableLaterObject() {
	}
/*
	@Deprecated
	public AvailableLaterObject(AvailabilityListener listener) {
		setListener(listener);
	}*/

	public abstract T calculate() throws Exception;
	
	public void run() {
		try {
			this.set(this.calculate());
		} catch (Exception e) {
			getListener().error(e);
		}
	}
	
	/**
	 * client function: get the result when done.
	 */
	public T get() {
		return innercontent;
	}
	
	/**
	 * client function: What should be called when done?
	 * @param listener
	 */
	public void setListener(AvailabilityListener<T> listener) {
		this.listener = listener;
		s.release();
	}

	/**
	 * waits until a listener is set
	 */
	private void blockForListener() {
		if (listener == null) {
			try {
				s.acquire();
			} catch (InterruptedException e) {
				blockForListener();
			}
		}
	}
	
	/**
	 * server function: access to the listener
	 * @return
	 */
	protected AvailabilityListener<T> getListener() {
		blockForListener();
		return listener;
	}

	/**
	 * server function: Starts the Thread and returns the object itself.
	 * 
	 * @return
	 */
	public AvailableLaterObject<T> start() {
		if (!this.setAlreadyStarted())
			new Thread(this).start();
			
		return this;
	}

	private boolean setAlreadyStarted() {
		return this.alreadyStarted.getAndSet(true);
	}

	protected boolean isAlreadyStarted() {
		return this.alreadyStarted.get();
	}
}
