package com.jakeapp.violet.actions.project;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.violet.actions.global.LoginView;
import com.jakeapp.violet.actions.global.serve.ISyncListener;
import com.jakeapp.violet.actions.global.serve.ProjectMessageListener;
import com.jakeapp.violet.actions.global.serve.ProjectRequestListener;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.di.KnownProperty;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.invites.ProjectInvitationHandler;

/**
 * Logs the project in
 */
public class LoginAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger.getLogger(LoginAction.class);

	private ProjectModel model;

	private User user;

	private String pw;

	private LoginView view;

	private long port;

	private ISyncListener requests;

	public LoginAction(User user, String pw, long port, LoginView view,
			ISyncListener requests) {
		this.pw = pw;
		this.user = user;
		this.port = port;
		this.view = view;
		this.requests = requests;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		ICService ics = model.getIcs();
		ProjectInvitationHandler invites = new ProjectInvitationHandler();
		invites.registerInvitationListener(view);
		ics.getMsgService().registerReceiveMessageListener(invites);

		ics.getStatusService().addLoginStateListener(view);
		ics.getUsersService().registerOnlineStatusListener(view);

		ProjectRequestListener prl = new ProjectRequestListener(model, requests);
		ProjectMessageListener pml = new ProjectMessageListener(model, requests);
		model.getIcs().getMsgService().registerReceiveMessageListener(pml);
		model.getTransfer().startServing(prl, prl);

		ics.getStatusService().login(
				DI.getUserId(user.getUserId()),
				pw,
				DI.getProperty(KnownProperty.ICS_RESOURCE_PROJECT_PREFIX)
						+ model.getProjectid().toString(), port);
		return null;
	}
}