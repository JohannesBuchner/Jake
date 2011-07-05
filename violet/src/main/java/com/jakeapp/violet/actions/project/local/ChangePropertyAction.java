package com.jakeapp.violet.actions.project.local;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.violet.context.ProjectModel;

/**
 * Modify a property of the project
 */
public class ChangePropertyAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger
			.getLogger(ChangePropertyAction.class);

	public static String getBooleanString(Boolean b) {
		if (b) {
			return "true";
		} else {
			return "false";
		}
	}

	public static String getProjectIdKey() {
		return "id";
	}

	public static String getProjectNameKey() {
		return "name";
	}

	private final ProjectModel model;

	private final String value;

	private final String key;

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
		log.info("updating property: " + key + " = " + value);
		model.getPreferences().set(key, value);
		return null;
	}
}