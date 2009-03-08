package com.jakeapp.gui.swing.actions.users;

import com.jakeapp.core.domain.TrustState;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.UserAction;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * The Invite people action.
 * Opens a Dialog that let you add people to the project.
 * They get an invitation and can join/refuse the project.
 */
public class TrustNoUsersAction extends UserAction {
	private static final Logger log = Logger.getLogger(TrustNoUsersAction.class);
	private static final TrustState actionTrustState = TrustState.NO_TRUST;

	public TrustNoUsersAction(JList list) {
		super(list);

		String actionStr = JakeMainView.getMainView().getResourceMap().
						getString("notTrustedPeopleMenuItem.text");

		putValue(Action.NAME, actionStr);

		// update state
		updateAction();
	}


	public void actionPerformed(ActionEvent actionEvent) {
		log.info("Don't trust ProjectMember " + getList() + " from" + getProject());
		setUserTrustState(actionTrustState);
	}

	@Override
	public void updateAction() {
		super.updateAction();

		// update state
		putValue(Action.SELECTED_KEY, checkUserStatus(actionTrustState));
	}
}