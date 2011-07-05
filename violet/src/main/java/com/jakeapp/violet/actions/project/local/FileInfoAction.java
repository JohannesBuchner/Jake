package com.jakeapp.violet.actions.project.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.StatusUpdate;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.violet.context.ProjectModel;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.attributes.Attributed;

/**
 * <code>AvailableLaterObject</code> fetching information about some
 * <code>FileObjects</code>
 */
public class FileInfoAction extends AvailableLaterObject<List<Attributed>> {

	private static final Logger log = Logger.getLogger(FileInfoAction.class);

	private final Collection<JakeObject> files;

	private final ProjectModel model;

	private final int totalSteps;

	private int stepsDone;

	public FileInfoAction(ProjectModel model, Collection<JakeObject> files) {
		this.model = model;
		this.files = files;
		stepsDone = 1;
		totalSteps = files.size() + 1;
		setStatus(new StatusUpdate(totalSteps * 1. / stepsDone, "init"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Attributed> calculate() throws Exception {
		IFSService fss = model.getFss();
		Log log = model.getLog();
		List<Attributed> attributed = new ArrayList<Attributed>();

		for (JakeObject fo : files) {
			attributed.add(AttributedCalculator.calculateAttributed(fss, log,
					fo));

			stepsDone++;
			setStatus(new StatusUpdate(totalSteps * 1. / stepsDone, "working"));
		}

		return attributed;
	}
}
