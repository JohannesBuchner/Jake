package com.jakeapp.violet.di;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.LogImpl;


public class LogFactory implements ILogFactory {

	@Named("project log filename")
	@Inject
	String logFilename;


	public LogFactory(@Named("project log filename") String logFilename) {
		this.logFilename = logFilename;
	}

	@Override
	public Log getLog(ProjectDir dir) {
		return new LogImpl(new File(dir, logFilename));
	}

}
