package com.jakeapp.violet.di;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.violet.model.JsonProjectPreferences;
import com.jakeapp.violet.model.ProjectPreferences;


public class JsonProjectPreferencesFactory implements IProjectPreferencesFactory {

	@Named("project preferences filename")
	@Inject
	String projectPreferencesFilename;

	public JsonProjectPreferencesFactory(
			@Named("project preferences filename") String projectPreferencesFilename) {
		this.projectPreferencesFilename = projectPreferencesFilename;
	}

	@Override
	public ProjectPreferences get(ProjectDir d) {
		return new JsonProjectPreferences(new File(d,
				projectPreferencesFilename));
	}
}
