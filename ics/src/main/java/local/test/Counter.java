package local.test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class Counter {

	private int v = 0;

	private Semaphore s = new Semaphore(0);

	public void inc() {
		setValue(this.v + 1);
	}

	public void dec() {
		setValue(this.v - 1);
	}

	public synchronized int getValue() {
		return this.v;
	}

	public synchronized void setValue(int value) {
		this.v = value;
		this.s.release();
	}

	/**
	 * waits at most time/unit long for a change that reaches the expected value
	 * 
	 * @param value
	 * @param time
	 * @param unit
	 * @return
	 */
	public boolean await(int value, long time, TimeUnit unit) {
		long timeleft = time;

		while (true) {
			try {
				if (this.s.tryAcquire(timeleft, unit)) {
					if (this.getValue() == value) {
						return true;
					}
				} else {
					return false;
				}
			} catch (InterruptedException e) {
			}
		}
	}
}
