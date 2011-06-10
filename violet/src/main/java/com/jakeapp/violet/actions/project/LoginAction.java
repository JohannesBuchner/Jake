package com.jakeapp.violet.actions.project;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.violet.actions.global.LoginView;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.ProjectInvitationHandler;
import com.jakeapp.violet.synchronization.SyncServiceImpl;
import com.jakeapp.violet.synchronization.request.MessageMarshaller;
import com.jakeapp.violet.synchronization.request.ProjectRequestListener;

/**
 * Announces a <code>List</code> of <code>JakeObject</code>s.
 */
public class LoginAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger.getLogger(LoginAction.class);
	private ProjectModel model;
	private boolean logout;

	public LoginAction(ProjectModel model, boolean logout) {
		this.model = model;
		this.logout = logout;
	}

	private User user;
	private String pw;
	private boolean offline;
	private LoginView view;
	private long port;

	public LoginAction(User user, String pw, long port, boolean offline,
			LoginView view) {
		this.pw = pw;
		this.user = user;
		this.offline = offline;
		this.port = port;
		this.view = view;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		ICService ics = model.getIcs();
		if (offline) {
			ics.getStatusService().logout();
		} else {
			ProjectInvitationHandler invites = new ProjectInvitationHandler();
			invites.registerInvitationListener(view);
			ics.getMsgService().registerReceiveMessageListener(invites);

			ics.getStatusService().addLoginStateListener(view);
			ics.getUsersService().registerOnlineStatusListener(view);

			MessageMarshaller messageMarshaller = DI
					.getImpl(MessageMarshaller.class);
			SyncServiceImpl syncService = DI.getImplForProject(
					SyncServiceImpl.class, model.getProjectid());
			ProjectRequestListener prl = new ProjectRequestListener(model,
					syncService, messageMarshaller);
			model.getIcs().getMsgService().registerReceiveMessageListener(prl);

			ics.getStatusService().login(DI.getUserId(user.getUserId()), pw,
					DI.getProperty("ics.genericresource"), port);
		}
		return null;
	}
}