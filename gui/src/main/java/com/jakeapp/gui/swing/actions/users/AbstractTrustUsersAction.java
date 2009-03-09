package com.jakeapp.gui.swing.actions.users;

import com.jakeapp.core.domain.TrustState;
import com.jakeapp.gui.swing.actions.abstracts.UserAction;
import com.jakeapp.gui.swing.helpers.UserHelper;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author studpete
 */
public class AbstractTrustUsersAction extends UserAction {
	private static final Logger log = Logger.getLogger(AbstractTrustUsersAction.class);
	private TrustState actionTrustState;

	public AbstractTrustUsersAction(JList list, TrustState actionTrustState) {
		super(list);
		this.actionTrustState = actionTrustState;
	}

	public void actionPerformed(ActionEvent actionEvent) {
		log.debug(
						"Set Trust ProjectMember to " + actionTrustState + " for " + getList() + " from" + getProject());

		setUserTrustState(actionTrustState);
	}

	@Override
	public void updateAction() {
		super.updateAction();
		setEnabled(this.isEnabled() && hasSelectedUser() && !UserHelper
						.isCurrentProjectMember(getSelectedUser().getUser()));

		// update state
		putValue(Action.SELECTED_KEY, checkUserStatus(actionTrustState));
	}
}