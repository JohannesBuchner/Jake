package com.jakeapp.violet.actions.global;

import java.util.UUID;

import javax.inject.Inject;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.violet.context.Context;
import com.jakeapp.violet.context.ContextFactory;
import com.jakeapp.violet.context.ProjectActions;
import com.jakeapp.violet.context.ProjectModel;
import com.jakeapp.violet.di.ICSFactory;
import com.jakeapp.violet.di.ILogFactory;
import com.jakeapp.violet.di.IProjectPreferencesFactory;
import com.jakeapp.violet.di.IUserIdFactory;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.ProjectPreferences;
import com.jakeapp.violet.model.User;

/**
 * <code>AvailableLaterObject</code> which is responsible for starting or
 * stoping a given <code>Project</code>.
 */
public class StartProjectAction extends AvailableLaterObject<Context> {

	private final ProjectDir dir;

	@Inject
	private IUserIdFactory userids;

	@Inject
	private IFSService fss;

	@Inject
	private IProjectPreferencesFactory projectPreferencesFactory;

	@Inject
	private ICSFactory icsFactory;

	@Inject
	private ILogFactory logFactory;

	@Inject
	private ContextFactory contextFactory;


	public StartProjectAction(ProjectDir dir) {
		this.dir = dir;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Context calculate() throws Exception {
		fss.setRootPath(dir);
		ProjectPreferences prefs = projectPreferencesFactory.get(dir);
		UUID projectid = UUID.fromString(prefs
				.get(ProjectModel.PROJECT_ID_PROPERTY_KEY));
		User userId = new User(prefs.get(ProjectModel.USERID_PROPERTY_KEY));
		ICService ics = icsFactory.getICS(projectid);

		IFileTransferService transfer = icsFactory.getFileTransferService(
				ics.getMsgService(), userids.get(userId.getUserId()), ics);

		Log db = logFactory.getLog(dir);

		ProjectModel model = contextFactory.createProjectModel(fss, db, prefs,
				ics, transfer);
		ProjectActions actions = contextFactory.createProjectActions();
		return contextFactory.createContext(model, actions);
	}
}