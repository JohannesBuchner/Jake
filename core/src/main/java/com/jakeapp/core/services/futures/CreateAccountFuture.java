package com.jakeapp.core.services.futures;

import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.exceptions.NetworkException;

public class CreateAccountFuture extends AvailableLaterObject<Void> {

	private MsgService svc;
	
	public CreateAccountFuture(MsgService svc, AvailabilityListener listener) {
		super(listener);
		this.svc = svc;
	}

	@Override
	public void run() {
		if (svc != null) {
			try {
				svc.createAccount();
			} catch (NetworkException e1) {
				listener.error(e1);
			}
			listener.statusUpdate(0.7D, "");
			
			try {
				svc.logout();
				listener.statusUpdate(1.0D, "");
			} catch (Exception e) {
				listener.error(e);
			}
		}
	}
}
