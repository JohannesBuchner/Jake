package com.jakeapp.violet.actions;

import java.util.Observable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public abstract class ProgressingRunnable<V> extends Observable implements
		Runnable {

	protected Progress progress = new Progress(ActionState.INITIALIZING, 0.,
			"init");

	protected Exception exception = null;

	protected V value;

	protected Semaphore s = new Semaphore(0);

	@Override
	public void run() {
		try {
			value = doRun();
		} catch (Exception e) {
			this.exception = e;
			this.progress.update(ActionState.FINISHED_FAIL, 1., e.toString());
		} finally {
			s.release();
		}
	}

	protected abstract V doRun();

	public void waitForCompletion() throws InterruptedException {
		s.acquire();
		s.release();
	}

	public boolean isComplete() throws InterruptedException {
		return s.tryAcquire(10, TimeUnit.MILLISECONDS);
	}

	public V getValue() throws Exception {
		s.acquire();
		s.release();
		if (exception != null)
			throw exception;
		return value;
	}

	public Progress getProgress() {
		return progress;
	}
}
