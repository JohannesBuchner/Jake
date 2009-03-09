package com.jakeapp.gui.swing.actions.users;

import com.jakeapp.core.domain.TrustState;
import com.jakeapp.gui.swing.JakeMainView;

import javax.swing.*;

public class TrustNoUsersAction extends AbstractTrustUsersAction {
	//private static final Logger log = Logger.getLogger(TrustFullUsersAction.class);
	public TrustNoUsersAction(JList list) {
		super(list, TrustState.NO_TRUST);

		String actionStr = JakeMainView.getMainView().getResourceMap().
						getString("notTrustedPeopleMenuItem.text");

		putValue(Action.NAME, actionStr);

		updateAction();
	}
}