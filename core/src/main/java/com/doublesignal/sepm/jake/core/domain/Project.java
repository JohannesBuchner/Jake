package com.doublesignal.sepm.jake.core.domain;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
/**
 * The Project consists of a <code>rootpath</code>, a <code>name</code> and a 
 * <code>projectId</code>.
 */
public class Project {


	private File rootPath;
	private String name;
	private Boolean autoPush = false;
	private Boolean autoPull = false;
	private Integer autoSyncInterval = 0;
	private List<ProjectMember> members = new ArrayList<ProjectMember>();
	
	public Project(File rootPath, String name) {
		setRootPath(rootPath);
		setName(name);
	}

	/**
	 * Add Project Member to a Project
	 * @param member the ProjectMember to be added
	 */
	public void addMember(ProjectMember member) {
		members.add(member);
	}
	
	/**
	 * Get ArrayList of all Project Members of a Project
	 * @return <code>ArrayList<ProjectMembers></code> 
	 */
	public List<ProjectMember> getMembers() {
		return members;
	}
	
	/**
	 * Remove Project Member from a Project
	 * @param member the ProjectMember to be removed 
	 */
	public void removeMember(ProjectMember member) {
		members.remove(member);
	}
	
	public File getRootPath() {
		return rootPath;
	}
	
	/**
	 * Set the rootpath.
	 * @param rootPath The root path of the project
	 */
	public void setRootPath(File rootPath){
		this.rootPath = rootPath;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name of the project. One should use a unique project name for
	 * intersecting user groups
	 * @param name Name of the project 
	 */
	public void setName(String name){
		this.name = name;
	}
	
	public Boolean getAutoPush() {
		return autoPush;
	}
	
	/**
	 * Set the autoPush value for the project. 
	 * @param autoPush if true, file modifications trigger a push instantly.
	 */
	public void setAutoPush(Boolean autoPush) {
		this.autoPush = autoPush;
	}

	public Boolean getAutoPull() {
		return autoPull;
	}
	
	
	/**
	 * Set the autoPull value for the project.
	 * @param autoPull if true, after a sync, modified files are pulled automatically. 
	 */
	public void setAutoPull(Boolean autoPull) {
		this.autoPull = autoPull;
	}

	public Integer getAutoSyncInterval() {
		return autoSyncInterval;
	}
	
	/**
	 * Set the autosync interval in seconds.
	 * @param autoSyncInterval if the new interval is < 0, autoSyncInterval is set to 0
	 */
	public void setAutoSyncInterval(Integer autoSyncInterval) {
		if (autoSyncInterval < 0) 
			autoSyncInterval = 0;
		this.autoSyncInterval = autoSyncInterval;
	}

	/**
	 * Tests if two Projects are equal.
	 * @return <code>true</code> if all fields are equal.
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
		return result;
	}
}
