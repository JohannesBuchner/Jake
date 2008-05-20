package com.doublesignal.sepm.jake.core.domain;

import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidProjectIdException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidProjectNameException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidRootPathException;
import com.doublesignal.sepm.jake.core.domain.exceptions.ProjectNotConfiguredException;

import java.io.File;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 8, 2008
 * Time: 11:04:55 PM
 */
public class Project {

	private File rootPath;
	private String name;
	private String projectId;


	public Project() {

	}

	public Project(File rootPath, String name, String projectId)
			  throws InvalidProjectNameException, InvalidProjectIdException, InvalidRootPathException {
		setRootPath(rootPath);
		setName(name);
		setProjectId(projectId);
	}

	public File getRootPath() throws ProjectNotConfiguredException {
		if(rootPath == null ) throw new ProjectNotConfiguredException("rootPath not configured");
		return rootPath;
	}

	public void setRootPath(File rootPath) throws InvalidRootPathException {
		if (rootPath == null) throw new InvalidRootPathException("rootPath must not be null");
		if (!rootPath.isDirectory()) throw new InvalidRootPathException("root path must be a directory");
		if (!rootPath.exists()) throw new InvalidRootPathException("root path must exist");
		this.rootPath = rootPath;
	}


	public String getName() throws ProjectNotConfiguredException {
		if(name == null) throw new ProjectNotConfiguredException("ProjectName not configured");
		return name;
	}

	public void setName(String name) throws InvalidProjectNameException {
		if (name == null) throw new InvalidProjectNameException("project name must not be null");
		if (name.length() < 2) throw new InvalidProjectNameException("project name must be at least 2 characters long");
		if(name.length() > 50) throw new InvalidProjectNameException("project name may only be 50 characters long");
		this.name = name;
	}

	public String getProjectId() throws ProjectNotConfiguredException {
		if(projectId == null) throw new ProjectNotConfiguredException("ProjectId not configured");
		return projectId;
	}

	public void setProjectId(String projectId) throws InvalidProjectIdException {
		if(projectId == null) throw new InvalidProjectIdException("projectId cannot be null");
		if(projectId.length() != 8) throw new InvalidProjectIdException("projectId must be exact 8 characters long");
		this.projectId = projectId;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Project project = (Project) o;

		if (name != null ? !name.equals(project.name) : project.name != null) {
			return false;
		}
		if (projectId != null ? !projectId.equals(project.projectId) : project.projectId != null) {
			return false;
		}
		if (rootPath != null ? !rootPath.equals(project.rootPath) : project.rootPath != null) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int result;
		result = (rootPath != null ? rootPath.hashCode() : 0);
		result = 36 * result + (name != null ? name.hashCode() : 0);
		result = 36 * result + (projectId != null ? projectId.hashCode() : 0);
		return result;
	}

	//TODO implement other methods mentioned in class diagram

}
