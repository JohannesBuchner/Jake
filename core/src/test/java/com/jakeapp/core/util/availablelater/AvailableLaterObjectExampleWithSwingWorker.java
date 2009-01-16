package com.jakeapp.core.util.availablelater;

import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.SwingWorkerWithAvailableLaterObject;


public class AvailableLaterObjectExampleWithSwingWorker {


	public void callingFunction() {
		
		SwingWorkerWithAvailableLaterObject<String> worker = new SwingWorkerWithAvailableLaterObject<String>() {

			@Override
			protected AvailableLaterObject<String> calculateFunction() {
				return AvailableLaterObjectExample.calculateSomething(this);
			}

			@Override
			protected void done() {
				try {
					String result = this.get();
					// do something
				} catch (Exception e) {
					// handle error
				}
			}
		};

	};

}
