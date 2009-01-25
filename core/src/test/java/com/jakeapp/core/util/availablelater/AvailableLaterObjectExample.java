package com.jakeapp.core.util.availablelater;

import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailabilityListener;

public class AvailableLaterObjectExample {

	public static AvailableLaterObject<String> calculateSomething() {
		AvailableLaterObject<String> result = new AvailableLaterObject<String>() {

			@Override
			public String calculate() throws Exception {
				if (false)
					throw new Exception("foo");
				this.set("Hello");
				getListener().statusUpdate(0.5, "bla");
				return "World";
			}
		};
		new Thread(result).start();
		return result;
	}

	public void callingFunction() {
		AvailableLaterObject<String> result = calculateSomething();
		result.setListener(new AvailabilityListener<String>() {

			@Override
			public void error(Exception t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void finished(String o) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void statusUpdate(double progress, String status) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
}
