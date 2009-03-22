package com.jakeapp.core.services;

import java.io.IOException;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.NotADirectoryException;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;

/**
 * Interface for components managing <code>IFSService</code>s.
 */
public interface IProjectsFileServices {

	/**
	 * Starts the <code>IFSService</code> for the specified <code>Project</code>. If the <code>Project</code>
	 * is already started, it will just return the saved <code>IFSService</code> instance.
	 *
	 * @param project the <code>Project</code> to be started.
	 * @return the <code>IFSService</code> managing the directory structure behind this <code>Project</code>
	 * @throws NotADirectoryException gets thrown, if the {@link Project#rootPath} is not a vaild directory.
	 * @throws IOException			gets thrown, if some other FileSystemError occured.
	 */
	public IFSService startForProject(Project project) throws IOException, NotADirectoryException;

	/**
	 * Returns the already started instance of an <code>IFSService</code> for the given <code>Project</code>.
	 *
	 * @param project the <code>Project</code> for which we want the <code>IFSService</code>
	 * @return the FSS for the requested <code>Project</code>
	 * @throws ProjectNotLoadedException If the <code>IFSService</code> for the given <code>Project</code> was
	 *                                   not started.
	 */
	public IFSService getProjectFSService(Project project) throws ProjectNotLoadedException;

	/**
	 * This stops the <code>IFSService</code>s for a given <code>Project</code>
	 * @param project the <code>Project</code> for which the <code>IFSService</code>s should be stopped.
	 */
	public void stopForProject(Project project);
}
