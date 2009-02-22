package com.jakeapp.core.services;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.NotADirectoryException;
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
	 * @throws NotADirectoryException 
	 * @throws IOException 
	 */
    public IFSService startForProject(Project project) throws IOException, NotADirectoryException;

    /**
     * @param project
     * @return the FSS for the project
     * @throws ProjectNotLoadedException if none loaded
     */
    public IFSService getProjectFSService(Project project);

    /**
     * this should be called to stop running threads
     * @param project
     */
    public void stopForProject(Project project);
}
