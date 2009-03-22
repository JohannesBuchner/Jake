package com.jakeapp.availablelater;

import java.util.concurrent.Semaphore;


public class AvailableLaterWaiter<T>  {

	public static <TT> TT await(AvailableLaterObject<TT> avl) throws Exception {
		return new AvailableLaterWaiter<TT>(avl).get();
	}

	private Semaphore s = new Semaphore(0);
	private Exception exception = null;
	private T result = null;
	private AvailableLaterObject<T> avl;

	public AvailableLaterWaiter(AvailableLaterObject<T> avl) {
		this.avl = avl;
		avl.start();
		
		avl.setListener(new AvailabilityListener<T>() {

			@Override
			public void error(Exception t) {
				AvailableLaterWaiter.this.exception = t;
				s.release();
			}

			@Override
			public void finished(T o) {
				AvailableLaterWaiter.this.result = o;
				s.release();
			}

			@Override
			public void statusUpdate(double progress, String status) {
			}

		});
		while (true) {
			try {
				s.acquire();
				break;
			} catch (InterruptedException e) {
			}
		}
	}

	public T get() throws Exception {
		if(this.exception != null)
			throw this.exception;
		return avl.get();
	}
}
