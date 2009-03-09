package com.jakeapp.gui.swing.actions.users;

import com.jakeapp.core.domain.TrustState;
import com.jakeapp.gui.swing.JakeMainView;

import javax.swing.*;

public class TrustUsersAction extends AbstractTrustUsersAction {
	//private static final Logger log = Logger.getLogger(TrustFullUsersAction.class);
	public TrustUsersAction(JList list) {
		super(list, TrustState.TRUST);

		String actionStr = JakeMainView.getMainView().getResourceMap().
						getString("trustedPeopleMenuItem.text");

		putValue(Action.NAME, actionStr);

		updateAction();
	}
}