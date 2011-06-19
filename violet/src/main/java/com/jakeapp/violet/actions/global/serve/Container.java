package com.jakeapp.violet.actions.global.serve;


public class Container<T> {

	private T value;

	public void setValue(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}
}
