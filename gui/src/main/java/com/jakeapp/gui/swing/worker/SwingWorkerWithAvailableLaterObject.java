/**
 *
 */
package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailibilityListener;
import com.jakeapp.core.util.availablelater.StatusUpdate;

import javax.swing.*;
import java.util.concurrent.Semaphore;

/**
 * So you wanted to make a SwingWorker, and your inner method gets back a
 * {@link com.jakeapp.core.util.availablelater.AvailableLaterObject}. This resolves this double-Thread waiting
 * problem. You only have to implement {@link #calculateFunction()} (instead of
 * doInBackground) and {@link #done()} as usual.
 *
 * @author johannes
 * @param <T>
 */
public abstract class SwingWorkerWithAvailableLaterObject<T> extends
		  SwingWorker<T, StatusUpdate> implements AvailibilityListener {

	private Semaphore s = new Semaphore(0);

	private AvailableLaterObject<T> value;

	private Exception error;

	@Override
	protected T doInBackground() throws Exception {
		try {
			this.value = calculateFunction();
		} catch (Exception e) {
			this.error = e;
		}
		s.acquire();
		if (error != null)
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