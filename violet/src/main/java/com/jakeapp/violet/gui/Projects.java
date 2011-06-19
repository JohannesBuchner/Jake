package com.jakeapp.violet.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.violet.model.Model;

/**
 * The string name refers to the ProjectDir
 */
public abstract class Projects extends Observable implements Model {

	protected Collection<ProjectDir> projects = new ArrayList<ProjectDir>();

	private final static Logger log = Logger.getLogger(Projects.class);

	public Projects() {
		try {
			load();
		} catch (IOException e) {
			log.warn(e);
		}
	}

	protected abstract void load() throws IOException;

	protected abstract void store() throws JsonGenerationException,
			JsonMappingException, IOException;

	public void add(ProjectDir dir) throws IOException {
		projects.add(dir);
		store();
		setChanged();
	}

	public void remove(ProjectDir dir) throws IOException {
		projects.remove(dir);
		store();
		setChanged();
	}

	public Collection<ProjectDir> getAll() {
		return Collections.unmodifiableCollection(projects);
	}

}
