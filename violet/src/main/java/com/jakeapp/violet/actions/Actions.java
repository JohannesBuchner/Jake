package com.jakeapp.violet.actions;

import com.jakeapp.violet.model.ProjectModel;

/**
 * It was originally thought that this would be a exhaustive list of actions
 * that the gui can perform.
 * 
 * But it isn't, to avoid duplicate code. Pick the actions from
 * com.jakeapp.violet.actions.project
 * 
 */
public class Actions {

	protected ProjectModel model;

	public Actions(ProjectModel model) {
		this.model = model;
	}

}
