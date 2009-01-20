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
	
	public AvailableNowObject(AvailabilityListener listener, T content) {
		super(listener);
		this.tempcontent = content;
		//this.set(content);
	}

	@Override
	public void run() {
		//empty implementation
	}
	
	@Override
	public AvailableLaterObject<T> start() {
		this.run();
		if (this.get()==null) this.set(tempcontent);
		return this;
	}
}
