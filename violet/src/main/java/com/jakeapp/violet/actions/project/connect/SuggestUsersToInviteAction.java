package com.jakeapp.violet.actions.project.connect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.status.IStatusService;
import com.jakeapp.jake.ics.users.IUsersService;
import com.jakeapp.violet.context.ProjectModel;

/**
 * Announces a <code>List</code> of <code>JakeObject</code>s.
 */
public class SuggestUsersToInviteAction extends
		AvailableLaterObject<Collection<UserInfo>> {

	private static final Logger log = Logger
			.getLogger(SuggestUsersToInviteAction.class);


	private ProjectModel model;

	public SuggestUsersToInviteAction(ProjectModel model) {
		this.model = model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<UserInfo> calculate() throws Exception {
		ICService ics = model.getIcs();
		IUsersService users = ics.getUsersService();
		IStatusService status = ics.getStatusService();
		List<UserInfo> infos = new ArrayList<UserInfo>();
		for (UserId u : users.getUsers()) {
			UserInfo info = new UserInfo();
			info.setNickName(users.getNickName(u));
			info.setFirstName(status.getFirstname(u));
			info.setLastName(status.getLastname(u));
			info.setOnline(status.isLoggedIn(u));
			infos.add(info);
		}
		return infos;
	}
}