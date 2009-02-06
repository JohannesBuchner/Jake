package com.jakeapp.core.util.availablelater;

/**
 * A AvailableLater Object that is not really async but available
 * instantaneously
 *
 * @author johannes
 * @param <T>
 */
public class AvailableNowObject<T> extends AvailableLaterObject<T> {

	/* server functions */
	protected void set(T o) {
		this.innercontent = o;
	}

	public void run() {
	}
	
	protected AvailabilityListener<T> getListener() {
		return listener;
	}

	public AvailableNowObject(T content) {
		this.set(content);
	}

	@Override
	public T calculate() {
		return this.get();
	}
	
	public void setListener(AvailabilityListener<T> listener) {
		listener.finished(this.get());
	}

	@Override
	public AvailableLaterObject<T> start() {
		return this;
	}
}
