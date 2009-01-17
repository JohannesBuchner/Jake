package com.jakeapp.core.util.availablelater;

import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailabilityListener;

public class AvailableLaterObjectExample {

	public static AvailableLaterObject<String> calculateSomething(
			AvailabilityListener l) {
		AvailableLaterObject<String> result = new AvailableLaterObject<String>(
				l) {

			@Override
			public void run() {
				if (false)
					this.listener.error("foo");
				this.set("Hello");
				this.listener.statusUpdate(0.5, "bla");
				this.listener.finished();
			}
		};
		new Thread(result).start();
		return result;
	}

	public void callingFunction() {
		Object result = calculateSomething(new AvailabilityListener() {

			@Override
			public void error(Exception t) {
				// TODO Auto-generated method stub

			}

			@Override
			public void error(String reason) {
				// TODO Auto-generated method stub

			}

			@Override
			public void finished() {
				// TODO Auto-generated method stub

			}

			@Override
			public void statusUpdate(double progress, String status) {
				// TODO Auto-generated method stub

			}

		});
	}
}
