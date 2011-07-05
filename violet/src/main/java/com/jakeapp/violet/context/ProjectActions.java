package com.jakeapp.violet.context;

import javax.inject.Inject;

import com.jakeapp.violet.actions.project.connect.ConnectProjectActionsFactory;
import com.jakeapp.violet.actions.project.interact.InteractProjectActionsFactory;
import com.jakeapp.violet.actions.project.local.LocalProjectActionsFactory;

/**
 * It was originally thought that this would be a exhaustive list of actions
 * that the gui can perform.
 * 
 * But it isn't, to avoid duplicate code. Pick the actions from
 * com.jakeapp.violet.actions.project
 * 
 */
public class ProjectActions {

	@Inject
	private final LocalProjectActionsFactory localActions;

	@Inject
	private final ConnectProjectActionsFactory connectActions;

	@Inject
	private final InteractProjectActionsFactory interactActions;

	ProjectActions(LocalProjectActionsFactory localActions,
			ConnectProjectActionsFactory connectActions,
			InteractProjectActionsFactory interactActions) {
		this.localActions = localActions;
		this.connectActions = connectActions;
		this.interactActions = interactActions;
	}

	public ConnectProjectActionsFactory getConnectActions() {
		return connectActions;
	}

	public InteractProjectActionsFactory getInteractActions() {
		return interactActions;
	}

	public LocalProjectActionsFactory getLocalActions() {
		return localActions;
	}


}
