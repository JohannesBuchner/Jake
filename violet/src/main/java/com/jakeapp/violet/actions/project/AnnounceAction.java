package com.jakeapp.violet.actions.project;

import java.sql.Timestamp;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.ProjectModel;

/**
 * Announces a <code>List</code> of <code>JakeObject</code>s.
 */
public class AnnounceAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger.getLogger(AnnounceAction.class);
	private String why;
	private JakeObject what;
	private ProjectModel model;
	private boolean delete;

	public AnnounceAction(ProjectModel model, JakeObject what, String why,
			boolean delete) {
		this.why = why;
		this.what = what;
		this.model = model;
		this.delete = delete;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		java.util.Date today = new java.util.Date();
		Timestamp now = new java.sql.Timestamp(today.getTime());
		String hash = null;
		if (!delete) {
			hash = model.getFss().calculateHashOverFile(what.getRelPath());
		}
		LogEntry le = new LogEntry(UUID.randomUUID(), now, model.getUser(),
				what, why, hash, true);
		this.model.getLog().add(le);
		return null;
	}
}