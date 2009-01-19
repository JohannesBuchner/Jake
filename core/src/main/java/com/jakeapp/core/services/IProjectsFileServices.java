package com.jakeapp.core.services;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;


public interface IProjectsFileServices {

    public IFSService startProject(Project project);

    public IFSService getProjectFSService(Project project) throws ProjectNotLoadedException;

    public void stopProject(Project project);
}
