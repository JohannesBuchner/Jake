package com.jakeapp.violet.actions.global;

import javax.inject.Inject;
import javax.inject.Named;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.violet.di.IUserIdFactory;
import com.jakeapp.violet.model.User;

/**
 * <code>AvailableLaterObject</code> that creates an <code>Account</code> on an
 * IM-Provider
 */
public class CreateAccountAction extends AvailableLaterObject<Void> {

	private String pw;

	private UserId user;

	@Named("global ics")
	@Inject
	private ICService ics;

	@Inject
	private IUserIdFactory userids;


	public void setIcs(ICService ics) {
		this.ics = ics;
	}

	public void setUserids(IUserIdFactory userids) {
		this.userids = userids;
	}

	public CreateAccountAction(User user, String pw) {
		this.user = userids.get(user.getUserId());
		this.pw = pw;
	}

	@Override
	public Void calculate() throws NetworkException {
		ics.getStatusService().createAccount(this.user, this.pw);
		return null;
	}
}
