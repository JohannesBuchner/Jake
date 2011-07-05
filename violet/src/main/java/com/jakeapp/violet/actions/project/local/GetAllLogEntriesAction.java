package com.jakeapp.violet.actions.project.local;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.violet.context.ProjectModel;
import com.jakeapp.violet.model.LogEntry;

/**
 * Gets the full log
 * 
 * @author johannes
 */
public class GetAllLogEntriesAction extends
		AvailableLaterObject<Collection<LogEntry>> {

	private static Logger log = Logger.getLogger(GetAllLogEntriesAction.class);

	private final ProjectModel model;

	public GetAllLogEntriesAction(ProjectModel model) {
		this.model = model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<LogEntry> calculate() {
		return model.getLog().getAll(true);
	}
}
