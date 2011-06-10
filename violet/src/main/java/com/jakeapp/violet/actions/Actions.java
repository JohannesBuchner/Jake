package com.jakeapp.violet.actions;

import java.util.List;

import com.jakeapp.availablelater.AvailableErrorObject;
import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.AvailableNowObject;
import com.jakeapp.violet.model.ProjectModel;

/**
 * Actions a user can perform -- the 
 */
public class Actions {

	protected ProjectModel model;

	public Actions(ProjectModel model) {
		this.model = model;
	}

	public AvailableLaterObject<Void> launchFile(String filename) {
		try {
			model.getFss().launchFile(filename);
			return new AvailableNowObject<Void>(null);
		} catch (Exception e) {
			return new AvailableErrorObject<Void>(e);
		}
	}

	public AvailableLaterObject<List<String>> listAllFiles() {
		return new AvailableLaterObject<List<String>>() {
			@Override
			public List<String> calculate() throws Exception {
				return model.getFss().recursiveListFiles();
			}
		};
	}

}
