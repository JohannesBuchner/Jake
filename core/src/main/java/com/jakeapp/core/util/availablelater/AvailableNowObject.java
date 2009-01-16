package com.jakeapp.core.util.availablelater;

/**
 * A AvailableLater Object that is not really async but available
 * instantaneously
 * 
 * @author johannes
 * 
 * @param <T>
 */
public abstract class AvailableNowObject<T> extends AvailableLaterObject<T> {

	public AvailableNowObject(AvailibilityListener listener, T content) {
		super(listener);
		this.set(content);
	}

}
