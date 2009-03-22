/**
 *
 */
package com.jakeapp.gui.swing.worker.tasks;

import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.StatusUpdate;
import com.jakeapp.gui.swing.dialogs.debugging.ActiveTasks;
import com.jakeapp.gui.swing.worker.JakeExecutor;
import org.apache.log4j.Logger;

import javax.swing.*;
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
public abstract class AbstractTask<T> extends
		  SwingWorker<T, StatusUpdate> implements AvailabilityListener<T>, IJakeTask {

	private static final Logger log = Logger.getLogger(AbstractTask.class);

	private Semaphore s = new Semaphore(0);

	private AvailableLaterObject<T> value;

	private Exception exception;

	private double progress;
	private String status;
	
	private StackTraceElement[] callerStackTrace = new Throwable().getStackTrace();

	@Override
	final protected T doInBackground() throws Exception {
		try {
			this.value = calculateFunction();
		} catch (Exception e) {
			error(e);
		}
		this.value.setListener(this);
		
		this.value.start();
		
		s.acquire();
		if (exception != null)
			throw exception;
		return this.value.get();
	}

	abstract protected AvailableLaterObject<T> calculateFunction() throws RuntimeException;

	@Override
	final public void error(Exception t) {
		try {
			this.exception = t;
			s.release();
		}
		finally {
			JakeExecutor.removeTask(this);
		}
	}
	
	@Override
	final public void finished(T o) {
		s.release();
	}

	@Override
	final protected void done() {
		try {
			onDone();
		}
		finally {
			JakeExecutor.removeTask(this);
		}	
	}

	protected void onDone() {
		
	}

	@Override
	final public void statusUpdate(double progress, String status) {
		this.progress = progress;
		this.status = status;
		publish(new StatusUpdate(progress, status));

		// HACK to show tasks
		ActiveTasks.tasksUpdated();
	}

	final protected void handleInterruption(InterruptedException e) {
		log.warn("Swingworker has been interrupted: " + e.getMessage(), e);
	}

	final protected void handleExecutionError(ExecutionException e) {
		log.warn("Swingworker execution failed: " + e.getMessage(), e);
	}

	@Override
	public String toString() {
		return (value != null ? value.toString() : "") + ": " + progress + " " + status;
	}

	@Override
	public int hashCode() {
		// Fixme: is this a good idea?
		return getClass().toString().hashCode();
	}
	
	@Override
	final public Exception getException() {
		return this.exception;
	}
}