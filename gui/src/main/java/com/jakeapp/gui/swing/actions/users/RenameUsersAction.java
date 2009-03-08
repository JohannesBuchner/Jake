package com.jakeapp.gui.swing.actions.users;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.UserAction;
import com.jakeapp.gui.swing.controls.JListMutable;
import com.jakeapp.gui.swing.helpers.UserHelper;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * The Invite people action.
 * Opens a Dialog that let you add people to the project.
 * They get an invitation and can join/refuse the project.
 */
public class RenameUsersAction extends UserAction {
	private static final Logger log = Logger.getLogger(RenameUsersAction.class);

	public RenameUsersAction(JListMutable mutable) {
		super(mutable);

		String actionStr = JakeMainView.getMainView().getResourceMap().
						getString("renamePeopleMenuItem.text");

		putValue(Action.NAME, actionStr);

		updateAction();
	}

	private JListMutable getMutable() {
		return (JListMutable) getList();
	}


	public void actionPerformed(ActionEvent actionEvent) {
		log.info("Rename ProjectMember " + getMutable() + " from" + getProject());

		// ensure that users panel is visible
		JakeMainView.getMainView()
						.setContextViewPanel(JakeMainView.ContextPanelEnum.Project);

		new JListMutable.StartEditingAction()
						.actionPerformed(new ActionEvent(getMutable(), 0, ""));
	}


	@Override
	public void updateAction() {
		super.updateAction();
		setEnabled(this.isEnabled() && hasSelectedUser() && !UserHelper
						.isCurrentProjectMember(getSelectedUser().getUser()));

		// fixme: possible to set nick??
	}
}