package com.jakeapp.gui.swing.actions.users;

import com.jakeapp.core.domain.TrustState;
import com.jakeapp.gui.swing.JakeMainView;

import javax.swing.*;

public class TrustFullUsersAction extends AbstractTrustUsersAction {
	//private static final Logger log = Logger.getLogger(TrustFullUsersAction.class);
	public TrustFullUsersAction(JList list) {
		super(list, TrustState.AUTO_ADD_REMOVE);

		String actionStr = JakeMainView.getMainView().getResourceMap().
						getString("trustedAddPeoplePeopleMenuItem.text");

		putValue(Action.NAME, actionStr);

		updateAction();
	}
}