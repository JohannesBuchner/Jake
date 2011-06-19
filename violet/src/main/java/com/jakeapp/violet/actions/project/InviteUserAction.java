package com.jakeapp.violet.actions.project;

import java.sql.Timestamp;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.protocol.invites.ProjectInvitationHandler;

/**
 * Announces a <code>List</code> of <code>JakeObject</code>s.
 */
public class InviteUserAction extends AvailableLaterObject<Boolean> {

	private static final Logger log = Logger.getLogger(InviteUserAction.class);

	private ProjectModel model;

	public InviteUserAction(ProjectModel model) {
		this.model = model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean calculate() throws Exception {
		ICService ics = model.getIcs();
		UserId userId = DI.getUserId(model.getUserid());

		String msg = ProjectInvitationHandler.createInviteMessage(
				model.getProjectname(), model.getProjectid());
		ics.getUsersService().addUser(userId, userId.getUserId());

		return ics.getMsgService().sendMessage(userId, msg);
	}

}