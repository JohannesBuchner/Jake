package com.jakeapp.violet.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.jake.fss.exceptions.NotADirectoryException;
import com.jakeapp.violet.actions.Actions;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.Context;
import com.jakeapp.violet.model.ProjectModel;

/**
 * The string name refers to the ProjectDir
 * 
 * This should be in the GUI. The core should only be concerned with the
 * individual Context
 */
@Deprecated
public class ProjectContexts {
	private Map<ProjectDir, Context> loadedProjects = new HashMap<ProjectDir, Context>();
	private Projects projects;

	public ProjectContexts(Projects projects) {
		this.projects = projects;
	}

	public void load(ProjectDir dir) throws IOException, NotADirectoryException {
		loadedProjects.put(dir, create(dir));
	}

	private Context create(ProjectDir name) throws IOException,
			NotADirectoryException {
		// create c
		Context c = null;
		return loadedProjects.put(name, c);
	}

	public Context get(ProjectDir dir) {
		return loadedProjects.get(dir);
	}

	public boolean isLoaded(ProjectDir dir) {
		return loadedProjects.containsKey(dir);
	}
}
