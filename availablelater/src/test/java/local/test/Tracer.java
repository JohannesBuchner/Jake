package local.test;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;


public class Tracer {

	private static Logger log = Logger.getLogger(Tracer.class);

	private List<String> trace = new LinkedList<String>();

	private Semaphore s = new Semaphore(0);

	public void step(String step) {
		log.debug("'" + step + "' reached.");
		synchronized (trace) {
			this.trace.add(step);
		}
		this.s.release();
	}

	public Iterable<String> getTrace() {
		return this.trace;
	}

	/**
	 * checks that no steps are left
	 * @param time
	 * @param unit
	 * @return
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
	 * @return
	 */
	public boolean isDone() {
		log.debug("Checking if done ...");
		if (this.trace.size() != 0) {
			StringBuilder steps = new StringBuilder("more steps left: ");
			for (String step : getTrace()) {
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
	 * @return
	 */
	public boolean awaitStep(String value, long time, TimeUnit unit) {
		while (true) {
			try {
				log.debug("Waiting for '" + value + "' ... ");
				if (this.s.tryAcquire(time, unit)) {
					if (this.trace.remove(value)) {
						log.debug("'" + value + "' awaited.");
						return true;
					}
				} else {
					log.debug("Waiting failed: Current " + toString());
					if (this.trace.remove(value)) {
						log.debug("But '" + value + "' is magically here.");
						return true;
					}
					return false;
				}
			} catch (InterruptedException e) {
				//
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Trace[");
		synchronized (trace) {
			for (String step : getTrace()) {
				sb.append(step);
				sb.append(",");
			}
			sb.append("]");
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
	 * @return
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
	 * @return
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
