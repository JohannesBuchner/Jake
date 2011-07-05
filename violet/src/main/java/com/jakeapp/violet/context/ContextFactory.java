package com.jakeapp.violet.context;

import javax.inject.Inject;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.violet.actions.project.connect.ConnectProjectActionsFactory;
import com.jakeapp.violet.actions.project.interact.InteractProjectActionsFactory;
import com.jakeapp.violet.actions.project.local.LocalProjectActionsFactory;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.ProjectPreferences;


public class ContextFactory implements IContextFactory {

	@Inject
	private LocalProjectActionsFactory localActions;

	@Inject
	private ConnectProjectActionsFactory connectActions;

	@Inject
	private InteractProjectActionsFactory interactActions;

	@Override
	public Context createContext(ProjectModel model, ProjectActions actions) {
		return new Context(model, actions);
	}

	@Override
	public ProjectActions createProjectActions() {
		return new ProjectActions(localActions, connectActions, interactActions);
	}


	@Override
	public ProjectModel createProjectModel(IFSService fss, Log log,
			ProjectPreferences preferences, ICService ics,
			IFileTransferService transfer) {
		return new ProjectModel(fss, log, preferences, ics, transfer);
	}

	public void setConnectActions(ConnectProjectActionsFactory connectActions) {
		this.connectActions = connectActions;
	}

	public void setInteractActions(InteractProjectActionsFactory interactActions) {
		this.interactActions = interactActions;
	}

	public void setLocalActions(LocalProjectActionsFactory localActions) {
		this.localActions = localActions;
	}
}
