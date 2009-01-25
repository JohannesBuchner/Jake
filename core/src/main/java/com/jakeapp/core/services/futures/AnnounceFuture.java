package com.jakeapp.core.services.futures;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.synchronization.ISyncService;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;

public class AnnounceFuture extends AvailableLaterObject<Void> {
	private ISyncService iss;
	private JakeObject jo;
	private LogEntry<JakeObject> action;
	
	public AnnounceFuture(ISyncService iss, JakeObject jo, LogEntry<JakeObject> action) {
		this.iss = iss;
		this.jo = jo;
		this.action = action;
	}

	@Override
	public Void calculate() throws Exception {
		this.iss.announce(jo, action , "");
		return null;
	}

}
