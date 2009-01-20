package com.jakeapp.core.util.availablelater;

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
 * result type
 */
public abstract class AvailableLaterObject<T> implements Runnable {

	private T innercontent;

	protected AvailabilityListener listener;

	protected void set(T o) {
		this.innercontent = o;
		this.listener.finished();
	}

	public AvailableLaterObject(AvailabilityListener listener) {
		this.listener = listener;
	}

	public T get() {
		return innercontent;
	}

	/**
	 * Starts the Thread and returns the object itself.
	 *
	 * @return
	 */
	public AvailableLaterObject<T> start() {
		new Thread(this).start();
		return this;
	}
}
