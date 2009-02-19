package com.jakeapp.core.services;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;

/**
 * holds all FSS
 * 
 * @author johannes
 */
public interface IProjectsFileServices {

	/**
	 * gets the FSS or starts it if none is available yet.
	 * @param project
	 * @return
	 */
    public IFSService startProject(Project project);

    /**
     * @param project
     * @return the FSS for the project
     * @throws ProjectNotLoadedException if none loaded
     */
    public IFSService getProjectFSService(Project project) throws ProjectNotLoadedException;

    /**
     * 
     * @param uuid
     * @return the FSS for the project
     * @throws ProjectNotLoadedException if none loaded
     */
    public IFSService getProjectFSServiceByUUID(String uuid) throws ProjectNotLoadedException;

    /**
     * this should be called to stop running threads
     * @param project
     */
    public void stopProject(Project project);
}
