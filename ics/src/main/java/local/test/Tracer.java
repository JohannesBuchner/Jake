package local.test;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;


public class Tracer {

	private static Logger log = Logger.getLogger(Tracer.class);

	private Queue<String> trace = new SynchronousQueue<String>();

	private Semaphore s = new Semaphore(0);

	public synchronized void step(String step) {
		this.trace.add(step);
		this.s.release();
	}

	public synchronized Queue<String> getTrace() {
		return this.trace;
	}

	/**
	 * checks that no steps are left
	 */
	public boolean isDone(long time, TimeUnit unit) {
		while (true) {
			try {
				if (!this.s.tryAcquire(time, unit))
					break;
			} catch (InterruptedException e) {
				//
			}
		}
	
		return isDone();
	}

	/**
	 * checks that no steps are left
	 */
	public boolean isDone() {
		if (getTrace().size() != 0) {
			StringBuilder steps = new StringBuilder("more steps left: ");
			for(String step : getTrace()) {
				steps.append(step);
				steps.append(", ");
			}
			return false;
		}
		return true;
	}

	/**
	 * waits at most time/unit long for a change that reaches the expected value
	 * removes the reached steps
	 * 
	 * @param value
	 * @param time
	 * @param unit
	 */
	public boolean awaitStep(String value, long time, TimeUnit unit) {
		while (true) {
			try {
				if (this.s.tryAcquire(time, unit)) {
					if (this.trace.remove(value)) {
						return true;
					}
				} else {
					log.debug(toString());
					return false;
				}
			} catch (InterruptedException e) {
				//
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Trace: ");
		for(String step: trace) {
			sb.append(step);
		}
		return sb.toString();
	}

	/**
	 * waits at most time/unit long for a change that reaches each expected
	 * value. Removes the steps found
	 * 
	 * @param value
	 * @param time
	 * @param unit
	 */
	public boolean await(List<String> value, long time, TimeUnit unit) {
		for (String state : value) {
			if (!await(state, time, unit)) {
				log.info("step " + state + " not reached!");
				log.debug(toString());
				return false;
			}
		}
		return true;
	}

	/**
	 * waits at most time/unit long for a change that reaches each expected
	 * value
	 * 
	 * @param value
	 *            comma (,) separated steps
	 * @param time
	 * @param unit
	 */
	public boolean await(String value, long time, TimeUnit unit) {
		for (String state : value.split(",")) {
			if (!awaitStep(state.trim(), time, unit)) {
				return false;
			}
		}
		return true;
	}
}
