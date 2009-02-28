package com.jakeapp.core.services.futures;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.synchronization.exceptions.ProjectException;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;

import java.io.FileNotFoundException;

public class StartStopProjectFuture extends AvailableLaterObject<Void> {

	private Project project;
	private IProjectsManagingService pms;
	private boolean start;

	public StartStopProjectFuture(IProjectsManagingService pms, Project project,
					boolean start) {
		this.pms = pms;
		this.project = project;
		this.start = start;
	}

	@Override
	public Void calculate() {
		try {
			if (start) {
				this.pms.startProject(project);
			} else {
				this.pms.stopProject(project);
			}
		} catch (FileNotFoundException e) {
			// FIXME: 
			e.printStackTrace();
		} catch (ProjectException e) {
			e.printStackTrace();
		} catch (NoSuchProjectException e) {
			e.printStackTrace();
		}
		return null;
	}
}