package com.jakeapp.violet.actions.project.interact.pull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.violet.actions.project.interact.UserOrderStrategy;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.User;

/**
 * <code>AvailableLaterObject</code> downloading (<code>Pull</code>ing) a
 * <code>List</code> of <code>JakeObject</code>s with the given
 * <code>ISyncService</code>
 */
public class DownloadAction extends AvailableLaterObject<File> {

	private static final Logger log = Logger.getLogger(DownloadAction.class);

	private ProjectModel model;

	private JakeObject jakeObject;

	private UserOrderStrategy strategy;

	public DownloadAction(ProjectModel model, JakeObject jakeObject,
			UserOrderStrategy strategy) {
		this.model = model;
		this.jakeObject = jakeObject;
		this.strategy = strategy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File calculate() throws Exception {
		return AvailableLaterWaiter.await(new FailoverFileRequestAction(model,
				jakeObject, strategy, false));
	}
}
