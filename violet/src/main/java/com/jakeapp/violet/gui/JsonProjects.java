package com.jakeapp.violet.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import com.jakeapp.jake.fss.ProjectDir;

/**
 * The string name refers to the ProjectDir
 */
public class JsonProjects extends Projects {

	private File f;

	private ObjectMapper objectMapper = new ObjectMapper();

	public JsonProjects(File f) {
		super();
		if (f == null)
			throw new NullPointerException();
		this.f = f;
	}

	@Override
	protected void load() throws IOException {
		this.projects.clear();
		if (!f.exists())
			throw new FileNotFoundException(f.toString());
		List<String> r = (List<String>) objectMapper.readValue(f, List.class);
		for (String s : r) {
			this.projects.add(new ProjectDir(s));
		}
	}

	@Override
	protected void store() throws IOException {
		List<String> r = new ArrayList<String>();
		for (ProjectDir p : this.projects) {
			r.add(p.getAbsolutePath());
		}
		objectMapper.writeValue(f, r);
	}
}
