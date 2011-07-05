package com.jakeapp.violet.di;

import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.violet.model.Log;


public interface ILogFactory {

	Log getLog(ProjectDir dir);

}
