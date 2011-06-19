package com.jakeapp.violet.actions.project;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.users.IUsersService;
import com.jakeapp.violet.actions.global.LoginView;
import com.jakeapp.violet.actions.global.UsersView;
import com.jakeapp.violet.model.ProjectModel;

/**
 * Announces a <code>List</code> of <code>JakeObject</code>s.
 */
public class ListUsersViewAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger
			.getLogger(ListUsersViewAction.class);

	private ProjectModel model;

	private UsersView view;

	public ListUsersViewAction(ProjectModel model, UsersView view) {
		this.model = model;
		this.view = view;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		IUsersService users = model.getIcs().getUsersService();
		users.registerOnlineStatusListener(view);
		for (UserId u : users.getUsers()) {
			view.onlineStatusChanged(u);
		}
		return null;
	}
}