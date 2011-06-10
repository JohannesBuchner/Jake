package com.jakeapp.violet.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Observable;

import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.violet.model.Model;

/**
 * The string name refers to the ProjectDir
 */
public abstract class Projects extends Observable implements Model {
	private Collection<ProjectDir> projects = new ArrayList<ProjectDir>();

	public Projects() {
		load();
	}

	protected abstract void load();

	protected abstract void store();

	public void add(ProjectDir dir) {
		projects.add(dir);
		store();
		setChanged();
	}

	public void remove(ProjectDir dir) {
		projects.remove(dir);
		store();
		setChanged();
	}

	public Collection<ProjectDir> getAll() {
		return Collections.unmodifiableCollection(projects);
	}

}
