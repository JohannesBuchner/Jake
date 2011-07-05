package com.jakeapp.violet.context;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.ProjectPreferences;


/**
 * implementation so that the data structures can actually be set
 */
public interface IContextFactory {

	Context createContext(ProjectModel model, ProjectActions actions);

	ProjectActions createProjectActions();

	ProjectModel createProjectModel(IFSService fss, Log log,
			ProjectPreferences preferences, ICService ics,
			IFileTransferService transfer);

}
