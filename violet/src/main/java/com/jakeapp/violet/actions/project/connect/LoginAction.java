package com.jakeapp.violet.actions.project.connect;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.violet.actions.global.LoginView;
import com.jakeapp.violet.actions.global.serve.ISyncListener;
import com.jakeapp.violet.actions.global.serve.ProjectMessageListener;
import com.jakeapp.violet.actions.global.serve.ProjectRequestListener;
import com.jakeapp.violet.context.ProjectModel;
import com.jakeapp.violet.di.IUserIdFactory;
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

	private String host;

	private LoginView view;

	private long port;

	private ISyncListener requests;

	@Inject
	private IUserIdFactory userids;

	public void setUserids(IUserIdFactory userids) {
		this.userids = userids;
	}

	public LoginAction(User user, String pw, String host, long port,
			LoginView view, ISyncListener requests) {
		this.pw = pw;
		this.user = user;
		this.port = port;
		this.host = host;
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

		ics.getStatusService().login(userids.get(user.getUserId()), pw, host,
				port);
		return null;
	}
}