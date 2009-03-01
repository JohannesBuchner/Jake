package com.jakeapp.core.services.futures;

import java.util.List;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.synchronization.ISyncService;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;

public class PullFuture extends AvailableLaterObject<Void> {
	private ISyncService iss;
	private List<JakeObject> jakeObjects;

	public PullFuture(ISyncService iss, List<JakeObject> jakeObjects) {
		this.iss = iss;
		this.jakeObjects = jakeObjects;
	}

	@Override
	public Void calculate() throws Exception {
		for (JakeObject jakeObject : this.jakeObjects) {
			try {
				this.iss.pullObject(jakeObject);
			} catch (Exception ignored) {
				// ignore the exception, and skip this object
				// oh how I hate it...
			}
		}
		//TODO: maybe, sometime add progress and status features...
		return null;
	}
}