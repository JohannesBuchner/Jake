package com.jakeapp.violet.actions.project;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.User;

/**
 * <code>AvailableLaterObject</code> deleting some <code>FileObject</code>
 */
public class LogSyncAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger.getLogger(LogSyncAction.class);
	private Collection<JakeObject> toDelete;
	private ProjectModel model;
	private String why;

	public LogSyncAction(ProjectModel model, User user) {
		this.model = model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		//TODO
		return null;
	}

}
