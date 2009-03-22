package com.jakeapp.core.services;

import java.util.Map;
import java.util.HashMap;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.FSService;
import com.jakeapp.jake.fss.exceptions.NotADirectoryException;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import org.apache.log4j.Logger;


/**
 * Implementation of the <code>IProjectsFileServices</code> Interface, providing <code>IFSService</code>s
 */
public class ProjectsFileServicesImpl implements IProjectsFileServices {

	private static Logger log = Logger.getLogger(ProjectsFileServicesImpl.class);


	private Map<String, IFSService> fileServices = new HashMap<String, IFSService>();

	/**
	 * Default Constructor used to create the component.
	 */
	public ProjectsFileServicesImpl() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFSService startForProject(Project project) throws IOException,
			NotADirectoryException {
		if (this.fileServices.containsKey(project.getProjectId()))
			return this.fileServices.get(project.getProjectId());

		log.info("Starting fss for project " + project);
		IFSService fss;
		try {
			fss = new FSService();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Not running a supported Desktop, eh?", e);
		}
		fss.setRootPath(project.getRootPath());
		fileServices.put(project.getProjectId(), fss);

		return fss;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFSService getProjectFSService(Project project) throws ProjectNotLoadedException {
		if (fileServices.containsKey(project.getProjectId())) {
			return fileServices.get(project.getProjectId());
		}
		throw new ProjectNotLoadedException("Project with uuid " + project.getProjectId()
				+ " has no fss");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopForProject(Project project) {
		if(project == null)
			return;

		if(!fileServices.containsKey(project.getProjectId()))
			return;

		fileServices.get(project.getProjectId()).unsetRootPath();
		fileServices.remove(project.getProjectId());
	}
}
