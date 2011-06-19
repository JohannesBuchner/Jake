package com.jakeapp.violet.model;

import com.jakeapp.violet.actions.Actions;

/**
 * MVC-like pattern.
 * 
 * The model is the data structure behind everything. All should point to it.
 * 
 * The user can perform data modifications using actions. Actions operate on the
 * model.
 * 
 * The user can view data using views. First, a action to request a view is
 * called, which creates the view. The view observes the model.
 * 
 * <pre>
 *           Model
 *           ^   ^
 *          /     \
 *         /       \
 *    Actions ---> View
 * </pre>
 * 
 * Furthermore, a view can provoke an action call. Never is the view allowed to
 * modify the model.
 * 
 * Actions are NOT blocking calls with a return value. They are stateful
 * operations that can be observed.
 * 
 */
public class Context {

	private final ProjectModel model;

	private final Actions actions;

	public Context(ProjectModel model, Actions actions) {
		if (model == null)
			throw new NullPointerException();
		if (actions == null)
			throw new NullPointerException();
		this.model = model;
		this.actions = actions;
	}

	public ProjectModel getModel() {
		return model;
	}

	public Actions getActions() {
		return actions;
	}
}
