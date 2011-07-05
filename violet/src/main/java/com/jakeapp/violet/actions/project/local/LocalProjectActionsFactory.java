package com.jakeapp.violet.actions.project.local;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.jakeapp.violet.context.ProjectModel;
import com.jakeapp.violet.model.JakeObject;


public interface LocalProjectActionsFactory {

	DeleteFilesAction delete(ProjectModel model,
			Collection<JakeObject> toDelete, String why);

	FileInfoAction fileinfo(ProjectModel model, Collection<JakeObject> files);

	ImportFilesAction importFiles(ProjectModel model, List<File> files,
			String destFolderRelPath);

	LaunchFileAction launch(ProjectModel model, JakeObject jo);

	ChangePropertyAction setProperty(ProjectModel model, String key,
			String value);

	AllJakeObjectsViewAction viewFiles(ProjectModel model);

	GetAllLogEntriesAction viewlog(ProjectModel model);

	GetLogEntriesAction viewlog(ProjectModel model, JakeObject jo);

}
