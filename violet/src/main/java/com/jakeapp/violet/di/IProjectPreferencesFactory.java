package com.jakeapp.violet.di;

import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.violet.model.ProjectPreferences;


public interface IProjectPreferencesFactory {
	ProjectPreferences get(ProjectDir d);
}
