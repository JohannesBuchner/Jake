package com.jakeapp.core.util.availablelater;

public abstract class AvailableLaterObject<T> implements Runnable {

	public T innercontent;

	public AvailibilityListener listener;

	public void set(T o) {
		this.innercontent = o;
		this.listener.finished();
	}

	public AvailableLaterObject(AvailibilityListener listener) {
		this.listener = listener;
	}

	public T get() {
		return innercontent;
	}
}
