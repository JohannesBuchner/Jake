package com.jakeapp.violet.actions.global;

import javax.inject.Inject;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.violet.actions.Actions;
import com.jakeapp.violet.di.ICSFactory;
import com.jakeapp.violet.di.ILogFactory;
import com.jakeapp.violet.di.IProjectPreferencesFactory;
import com.jakeapp.violet.di.IUserIdFactory;
import com.jakeapp.violet.model.Context;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.ProjectModelImpl;
import com.jakeapp.violet.model.ProjectPreferences;

/**
 * <code>AvailableLaterObject</code> which is responsible for starting or
 * stoping a given <code>Project</code>.
 */
public class StartProjectAction extends AvailableLaterObject<Context> {

	private ProjectDir dir;

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


	public StartProjectAction(ProjectDir dir) {
		this.dir = dir;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Context calculate() throws Exception {
		ProjectModelImpl model = new ProjectModelImpl();

		fss.setRootPath(dir);
		model.setFss(fss);
		ProjectPreferences prefs = projectPreferencesFactory.get(dir);
		model.setPreferences(prefs);
		ICService ics = icsFactory.getICS(model.getProjectid());
		model.setIcs(ics);

		model.setTransfer(icsFactory.getFileTransferService(
				ics.getMsgService(), userids.get(model.getUserid()), ics));

		model.setLog(logFactory.getLog(dir));

		Actions actions = new Actions(model);

		return new Context(model, actions);
	}
}