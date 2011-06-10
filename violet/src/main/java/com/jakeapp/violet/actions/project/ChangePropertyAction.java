package com.jakeapp.violet.actions.project;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.violet.model.ProjectModel;

/**
 * Notifies another user that we have changes she might want to pull.
 */
public class ChangePropertyAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger
			.getLogger(ChangePropertyAction.class);
	private ProjectModel model;
	private String value;
	private String key;

	public String getProjectNameKey() {
		return "name";
	}

	public String getProjectIdKey() {
		return "id";
	}

	public String getBool(Boolean b) {
		if (b)
			return "true";
		else
			return "false";
	}

	/**
	 * 
	 * @param model
	 * @param key
	 * @param value
	 *            use "true" for booleans, everything else will be seen as false
	 */
	public ChangePropertyAction(ProjectModel model, String key, String value) {
		this.model = model;
		this.key = key;
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		this.model.getPreferences().set(key, value);
		return null;
	}
}