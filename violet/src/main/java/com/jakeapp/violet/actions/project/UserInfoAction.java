package com.jakeapp.violet.actions.project;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.status.IStatusService;
import com.jakeapp.jake.ics.users.IUsersService;
import com.jakeapp.violet.actions.global.LoginView;
import com.jakeapp.violet.actions.global.UsersView;
import com.jakeapp.violet.model.ProjectModel;

/**
 * Announces a <code>List</code> of <code>JakeObject</code>s.
 */
public class UserInfoAction extends AvailableLaterObject<UserInfo> {

	private static final Logger log = Logger.getLogger(UserInfoAction.class);

	private ProjectModel model;

	private UserId userid;

	public UserInfoAction(ProjectModel model, UserId userid) {
		this.model = model;
		this.userid = userid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserInfo calculate() throws Exception {
		IUsersService users = model.getIcs().getUsersService();
		IStatusService status = model.getIcs().getStatusService();
		UserInfo info = new UserInfo();
		info.setNickName(users.getNickName(userid));
		info.setFirstName(status.getFirstname(userid));
		info.setLastName(status.getLastname(userid));
		info.setOnline(status.isLoggedIn(userid));
		return info;
	}
}