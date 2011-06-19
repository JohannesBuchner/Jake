package com.jakeapp.violet.actions.global;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.User;

/**
 * <code>AvailableLaterObject</code> that creates an <code>Account</code> on an
 * IM-Provider
 */
public class CreateAccountAction extends AvailableLaterObject<Void> {

	private String pw;

	private UserId user;

	private ICService ics = DI.getImpl(ICService.class);

	public CreateAccountAction(User user, String pw) {
		this.user = DI.getUserId(user.getUserId());
		this.pw = pw;
	}

	@Override
	public Void calculate() throws NetworkException {
		ics.getStatusService().createAccount(this.user, this.pw);
		return null;
	}
}
