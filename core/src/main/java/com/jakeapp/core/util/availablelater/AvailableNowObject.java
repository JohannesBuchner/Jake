package com.jakeapp.core.util.availablelater;

public abstract class AvailableNowObject<T> extends AvailableLaterObject<T>{

	public AvailableNowObject(AvailibilityListener listener, T content) {
		super(listener);
		this.set(content);
	}

}
