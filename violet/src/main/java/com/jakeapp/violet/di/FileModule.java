package com.jakeapp.violet.di;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.violet.model.JsonProjectPreferences;
import com.jakeapp.violet.model.ProjectPreferences;


public class FileModule extends AbstractModule {

	@Override
	protected void configure() {
		bindConstant().annotatedWith(Names.named("projects json filename")).to(
				"jake.projects.json");
		bindConstant().annotatedWith(Names.named("project log filename")).to(
				".jakelog.db");
		bindConstant().annotatedWith(
				Names.named("project preferences filename")).to(".jake.config");
		bind(IProjectPreferencesFactory.class).to(JsonProjectPreferencesFactory.class);
	}

	@Provides
	@Named("Global Settings Dir")
	File provideFile() {
		File path;
		if (System.getenv("APPDATA") == null) {
			path = new File(System.getenv("HOME"), "Jake");
		} else {
			path = new File(System.getenv("APPDATA"), ".Jake");
		}
		path.mkdirs();
		return path;
	}
}
