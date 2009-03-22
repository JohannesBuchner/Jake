package com.jakeapp.core.services.futures;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.jake.ics.exceptions.NetworkException;

/**
 * <code>AvailableLaterObject</code> that creates an <code>Account</code> on an IM-Provider
 */
public class CreateAccountFuture extends AvailableLaterObject<Void> {

	private MsgService msgService;

	public CreateAccountFuture(MsgService msgService) {
		this.msgService = msgService;
	}

	@Override
	public Void calculate() {
		if (msgService != null) {
			try {
				msgService.createAccount();
			} catch (NetworkException e1) {
				getListener().error(e1);
			}
			getListener().statusUpdate(0.7D, "");
			
			try {
				msgService.logout();
				getListener().statusUpdate(1.0D, "");
			} catch (Exception e) {
				getListener().error(e);
			}
		}
		return null;
	}
}
