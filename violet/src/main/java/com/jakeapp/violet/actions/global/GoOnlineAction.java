package com.jakeapp.violet.actions.global;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.violet.di.IUserIdFactory;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.invites.ProjectInvitationHandler;

/**
 * Announces a <code>List</code> of <code>JakeObject</code>s.
 */
public class GoOnlineAction extends AvailableLaterObject<LoginView> {

	private static final Logger log = Logger.getLogger(GoOnlineAction.class);

	private User user;

	private String pw;

	private String host;

	private boolean offline;

	private long port;

	private LoginView view;

	@Inject
	private IUserIdFactory userids;

	// this is the general ics
	@Named("global ics")
	@Inject
	private ICService ics;

	public GoOnlineAction(User user, String pw, String host, long port,
			boolean offline, LoginView view) {
		this.pw = pw;
		this.user = user;
		this.offline = offline;
		this.host = host;
		this.port = port;
		this.view = view;
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


			ics.getStatusService().login(userids.get(user.getUserId()), pw,
					host, port);
		}
		return view;
	}
}