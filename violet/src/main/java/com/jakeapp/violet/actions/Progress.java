package com.jakeapp.violet.actions;

import java.util.Observable;


public class Progress extends Observable {

	public Progress(ActionState state, Double progress, String step) {
		super();
		update(state, progress, step);
	}

	protected Double progress;

	protected String step;

	protected ActionState state;

	public Double getProgress() {
		return progress;
	}

	public String getStep() {
		return step;
	}

	public ActionState getState() {
		return state;
	}

	public void update(ActionState state, Double progress, String step) {
		setChanged();
		this.progress = progress;
		this.step = step;
		this.state = state;
	}
}
