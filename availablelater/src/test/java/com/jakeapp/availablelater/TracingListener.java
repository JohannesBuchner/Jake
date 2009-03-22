/**
 * 
 */
package com.jakeapp.availablelater;

import local.test.Tracer;

class TracingListener<V> implements AvailabilityListener<V> {

	private Tracer tracer;

	public TracingListener(Tracer tracer) {
		super();
		this.tracer = tracer;
	}

	@Override
	public void error(Exception t) {
		tracer.step("error: " + t.getMessage());
	}

	@Override
	public void finished(V o) {
		tracer.step("done: " + o.toString());
	}

	@Override
	public void statusUpdate(double progress, String status) {
		// tracer.step("status update");
		// always nice, but never required
	}
}