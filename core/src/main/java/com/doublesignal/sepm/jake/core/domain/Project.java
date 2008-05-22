package com.doublesignal.sepm.jake.core.domain;

import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidProjectIdException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidProjectNameException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidRootPathException;
import com.doublesignal.sepm.jake.core.domain.exceptions.ProjectNotConfiguredException;

import java.io.File;

/**
 * The Project consists of a <code>rootpath</code>, a <code>name</code> and a 
 * <code>projectId</code>.
 */
public class Project {

	private File rootPath;
	private String name;
	private String projectId;

	/**
	 * Constructs a new Project with the given params. See setter Methods for 
	 * constraints and exceptions.
	 * 
	 * @param rootPath The root path of the project
	 * @param name The name of the project
	 * @param projectId The Id of the project
	 * 
	 * @throws InvalidProjectNameException 
	 * @throws InvalidProjectIdException
	 * @throws InvalidRootPathException
	 */
	public Project(File rootPath, String name, String projectId)
			  throws InvalidProjectNameException, InvalidProjectIdException, InvalidRootPathException {
		setRootPath(rootPath);
		setName(name);
		setProjectId(projectId);
	}

	public File getRootPath() {
		return rootPath;
	}

	/**
	 * Set the root path of the Project. The root path must be an existing
	 * directory.
	 * 
	 * @param rootPath The root path of the Project, may not be <code>null</code>.
	 * 
	 * @throws InvalidRootPathException An Exceptions is thrown iff the 
	 * <code>rootpath</code> does not exist or is no valid <code>directory</code>.
	 * 
	 * @see java.io.File
	 */
	public void setRootPath(File rootPath) throws InvalidRootPathException {
		
		if (!rootPath.isDirectory()) throw new InvalidRootPathException("root path must be a directory");
		if (!rootPath.exists()) throw new InvalidRootPathException("root path does not exist");
		this.rootPath = rootPath;
	}


	public String getName() {
		return name;
	}

	/**
	 * Set the name of the Project. The name must be between 2 and 50 characters
	 * long.
	 * @param name of the Project, may not be <code>null</code>;
	 * @throws InvalidProjectNameException iff <code>!(2 <= name.length() <= 50)</code>
	 */
	public void setName(String name) throws InvalidProjectNameException {
		if (name.length() < 2) throw new InvalidProjectNameException("project name must be at least 2 characters long");
		if(name.length() > 50) throw new InvalidProjectNameException("project name may only be 50 characters long");
		this.name = name;
	}

	public String getProjectId() {
		return projectId;
	}

	/**
	 * Set the project id.
	 * @param projectId must be exactly 8 characters long, may not be <code>null</code>;
	 * @throws InvalidProjectIdException iff <code>projectId.length != 8</code>
	 */
	public void setProjectId(String projectId) throws InvalidProjectIdException {
		if(projectId.length() != 8) throw new InvalidProjectIdException("projectId must be exactly 8 characters long");
		this.projectId = projectId;
	}

	/**
	 * Tests if two Projects are equal.
	 * @return <code>true</code> iff all fields are equal.
	 */
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

	/**
	 * Returns the hash code of the Project.
	 * @return hash code
	 */
	public int hashCode() {
		int result;
		result = (rootPath != null ? rootPath.hashCode() : 0);
		result = 36 * result + (name != null ? name.hashCode() : 0);
		result = 36 * result + (projectId != null ? projectId.hashCode() : 0);
		return result;
	}

	//TODO implement other methods mentioned in class diagram

}
