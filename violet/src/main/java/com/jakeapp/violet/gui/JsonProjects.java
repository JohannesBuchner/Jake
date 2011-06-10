package com.jakeapp.violet.gui;

import java.io.File;

import com.jakeapp.jake.fss.ProjectDir;

/**
 * The string name refers to the ProjectDir
 */
public class JsonProjects extends Projects {
	private File f;

	public JsonProjects(File f) {
		this.f = f;
		load();
	}

	@Override
	protected void load() {
		// TODO Auto-generated method stub
		// TODO: JSON here, load json file f

		add(new ProjectDir("/home/user/jaketest"));
	}

	@Override
	protected void store() {
		// TODO Auto-generated method stub
		// TODO: JSON here, save json file f
	}
}
