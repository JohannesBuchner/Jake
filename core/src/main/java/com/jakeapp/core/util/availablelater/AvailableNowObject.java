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
	@Override
	protected void set(T o) {
		this.innercontent = o;
	}

	@Override
	public void run() {
	}
	
	@Override
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
	
	@Override
	public void setListener(AvailabilityListener<T> listener) {
		listener.finished(this.get());
	}

	@Override
	public AvailableLaterObject<T> start() {
		return this;
	}
}
