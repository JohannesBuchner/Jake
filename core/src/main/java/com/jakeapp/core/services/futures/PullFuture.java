package com.jakeapp.core.services.futures;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.synchronization.ISyncService;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;

public class PullFuture extends AvailableLaterObject<Void> {
	private ISyncService iss;
	private JakeObject jo;

	public PullFuture(ISyncService iss, JakeObject jo) {
		this.iss = iss;
		this.jo = jo;
	}

	@Override
	public Void calculate() throws Exception {
		this.iss.pullObject(jo);
		return null;
	}
}