package com.jakeapp.gui.swing.actions.project;

import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;

import com.jakeapp.gui.swing.actions.project.CompoundProjectAction;

/**
 * @author: studpete
 */
public class DeleteOrRejectProjectAction extends CompoundProjectAction {
	private final DeleteProjectAction deleteAction = new DeleteProjectAction();
	private final RejectInvitationAction rejectAction = new RejectInvitationAction();
	private static final Logger log = Logger.getLogger(DeleteOrRejectProjectAction.class);


	public DeleteOrRejectProjectAction() {
		super();

		// link updates
		deleteAction.addPropertyChangeListener(up);
		rejectAction.addPropertyChangeListener(up);

		updateAction();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// use either on of the two actions within
		if (isNormalAction()) {
			deleteAction.actionPerformed(e);
		} else {
			rejectAction.actionPerformed(e);
		}
	}

	@Override
	public void updateAction() {
		log.debug("updating combined action with proj " + getProject());

		// wait for full initialize
		if (deleteAction == null) return;

		// use either on of the two actions within
		if (isNormalAction()) {
			internalActivateAction(deleteAction);
		} else {
			internalActivateAction(rejectAction);
		}
	}

	private boolean isNormalAction() {
		return getProject() == null;
	}
}