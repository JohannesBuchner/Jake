package com.jakeapp.jake.ics;


public class Counter {

	int v = 0;

	public synchronized void inc() {
		this.v++;
	}

	public synchronized void dec() {
		this.v--;
	}

	public synchronized int getValue() {
		return this.v;
	}

	public synchronized void setValue(int value) {
		this.v = value;
	}

}
