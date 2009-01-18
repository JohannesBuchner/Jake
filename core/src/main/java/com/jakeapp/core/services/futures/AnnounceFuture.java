package com.jakeapp.core.services.futures;

import java.io.FileNotFoundException;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.synchronization.ISyncService;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;

public class AnnounceFuture extends AvailableLaterObject<Void> {
	private ISyncService iss;
	private JakeObject jo;
	private LogEntry<JakeObject> action;
	
	public AnnounceFuture(AvailabilityListener listener, ISyncService iss, JakeObject jo, LogEntry<JakeObject> action) {
		super(listener);
		this.iss = iss;
		this.jo = jo;
		this.action = action;
	}

	@Override
	public void run() {
		try {
			this.iss.announce(jo, action , "");
		} catch (Exception e) {
			this.listener.error(e);
		}
		
		this.set(null);
	}

}
