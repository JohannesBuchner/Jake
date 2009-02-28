package com.jakeapp.core.services.futures;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;

import java.io.FileNotFoundException;

public class StartProjectFuture extends AvailableLaterObject<Void> {

	private Project project;
	private IProjectsManagingService pms;

	public StartProjectFuture(IProjectsManagingService pms, Project project) {
		this.pms = pms;
		this.project = project;
	}

	@Override
	public Void calculate() {
		try {
			this.pms.startProject(project);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ProjectException e) {
			e.printStackTrace();
		}
		return null;
	}
}