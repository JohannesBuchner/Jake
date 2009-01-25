package com.jakeapp.core.services.futures;

import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.exceptions.NetworkException;

public class CreateAccountFuture extends AvailableLaterObject<Void> {

	private MsgService svc;
	
	public CreateAccountFuture(MsgService svc) {
		this.svc = svc;
	}

	@Override
	public Void calculate() {
		if (svc != null) {
			try {
				svc.createAccount();
			} catch (NetworkException e1) {
				getListener().error(e1);
			}
			getListener().statusUpdate(0.7D, "");
			
			try {
				svc.logout();
				getListener().statusUpdate(1.0D, "");
			} catch (Exception e) {
				getListener().error(e);
			}
		}
		return null;
	}
}
