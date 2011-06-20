package com.jakeapp.violet.actions.project.local;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.ProjectModel;

/**
 * Gets the log of this {@link JakeObject}
 * 
 * @author johannes
 */
public class GetLogEntriesAction extends
		AvailableLaterObject<Collection<LogEntry>> {

	private static Logger log = Logger.getLogger(GetLogEntriesAction.class);

	private ProjectModel model;

	private JakeObject jo;

	public GetLogEntriesAction(ProjectModel model, JakeObject jo) {
		this.model = model;
		this.jo = jo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<LogEntry> calculate() {
		return model.getLog().getAllOfJakeObject(jo, true);
	}
}
