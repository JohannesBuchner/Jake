package com.jakeapp.violet.actions.global;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.di.KnownProperty;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.invites.ProjectInvitationHandler;

/**
 * Announces a <code>List</code> of <code>JakeObject</code>s.
 */
public class GoOnlineAction extends AvailableLaterObject<LoginView> {

	private static final Logger log = Logger.getLogger(GoOnlineAction.class);

	private User user;

	private String pw;

	private boolean offline;

	private long port;

	private LoginView view;

	// this is the general ics
	private ICService ics;

	public GoOnlineAction(User user, String pw, long port, boolean offline,
			LoginView view) {
		this.pw = pw;
		this.user = user;
		this.offline = offline;
		this.port = port;
		this.view = view;
		this.ics = DI.getICService(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoginView calculate() throws Exception {
		if (offline) {
			ics.getStatusService().logout();
		} else {
			ProjectInvitationHandler invites = new ProjectInvitationHandler();
			invites.registerInvitationListener(view);
			ics.getMsgService().registerReceiveMessageListener(invites);

			ics.getStatusService().addLoginStateListener(view);
			ics.getUsersService().registerOnlineStatusListener(view);


			ics.getStatusService().login(DI.getUserId(user.getUserId()), pw,
					DI.getProperty(KnownProperty.ICS_RESOURCE_NAME), port);
		}
		return view;
	}
}