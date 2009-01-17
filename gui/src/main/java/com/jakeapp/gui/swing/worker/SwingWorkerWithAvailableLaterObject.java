/**
 *
 */
package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.core.util.availablelater.StatusUpdate;
import com.jakeapp.gui.swing.JakeStatusBar;

import javax.swing.*;

import org.apache.log4j.Logger;

import java.util.concurrent.ExecutionException;
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
		  SwingWorker<T, StatusUpdate> implements AvailabilityListener {
	
	private static final Logger log = Logger.getLogger(SwingWorkerWithAvailableLaterObject.class);

	private Semaphore s = new Semaphore(0);

	private AvailableLaterObject<T> value;

	private Exception error;

	@Override
	protected T doInBackground() throws Exception {
		try {
			this.value = calculateFunction();
		} catch (Exception e) {
			error(e);
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
		s.release();
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
	
	protected void handleInterruption(InterruptedException e) {
		log.warn("Swingworker has been interrupted: " + e.getMessage());
	}
	
	protected void handleExecutionError(ExecutionException e) {
		log.warn("Swingworker execution failed: " + e.getMessage());
	}
}