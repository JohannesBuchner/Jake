/**
 * 
 */
package com.jakeapp.core.util.availablelater;

import java.util.concurrent.Semaphore;

import javax.swing.SwingWorker;


public abstract class SwingWorkerWithAvailableLaterObject<T> extends
		SwingWorker<T, StatusUpdate> implements AvailibilityListener {

	private Semaphore s = new Semaphore(0);

	private AvailableLaterObject<T> value;

	private Exception error;

	@Override
	protected T doInBackground() throws Exception {
		this.value = calculateFunction();
		s.acquire();
		if(error != null)
			throw error;
		return this.value.get();
	}

	abstract protected AvailableLaterObject<T> calculateFunction();

	@Override
	public void error(Exception t) {
		this.error = t;
	}

	@Override
	public void error(String reason) {
		this.error = new Exception(reason);
		s.release();
	}

	@Override
	public void finished() {
		s.release();
	}

	@Override
	public void statusUpdate(double progress, String status) {
		publish(new StatusUpdate(progress, status));
	}

}