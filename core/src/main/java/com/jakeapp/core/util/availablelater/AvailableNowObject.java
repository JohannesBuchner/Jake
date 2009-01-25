package com.jakeapp.core.util.availablelater;

/**
 * A AvailableLater Object that is not really async but available
 * instantaneously
 *
 * @author johannes
 * @param <T>
 */
public class AvailableNowObject<T> extends AvailableLaterObject<T> {

	private T tempcontent;
	
	public AvailableNowObject(T content) {
		this.tempcontent = content;
	}

	@Override
	public T calculate() {
		return tempcontent;
	}
	
	@Override
	public AvailableLaterObject<T> start() {
		this.run();
		return this;
	}
}
