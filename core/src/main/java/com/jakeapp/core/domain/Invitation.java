package com.jakeapp.core.domain;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.persistence.Id;
import java.util.UUID;
import java.util.Date;
import java.io.File;

@Entity(name = "invitation")
public class Invitation {

	@Id
	private UUID projectUUID;
	private String projectName;
	private Date creation;
	private User invitedOn;
	private User inviter;
	private String message;
	transient private File rootPath;

	public Invitation(UUID projectUUID, String projectName, Date creation, User invitedOn, User inviter, String message) {
		this.projectUUID = projectUUID;
		this.projectName = projectName;
		this.creation = creation;
		this.invitedOn = invitedOn;
		this.inviter = inviter;
		this.message = message;
	}

	public Invitation(Project project, User inviter)
	{
		this.projectUUID = UUID.fromString(project.getProjectId());
		this.projectName = project.getName();
		this.inviter = inviter;
		this.invitedOn = project.getUserId();
//		this.invitedOn =
		this.creation = new Date();	
	}

	public Invitation(Project project, User inviter, File rootPath)
	{
		this(project,  inviter);
		setRootPath(rootPath);
	}

	public Invitation() {

	}


	public UUID getProjectUUID() {
		return projectUUID;
	}

	public void setProjectUUID(UUID projectUUID) {
		this.projectUUID = projectUUID;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public User getInvitedOn() {
		return invitedOn;
	}

	public void setInvitedOn(User invitedOn) {
		this.invitedOn = invitedOn;
	}

	public User getInviter() {
		return inviter;
	}

	public void setInviter(User inviter) {
		this.inviter = inviter;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Transient
	public File getRootPath() {
		return rootPath;
	}

	public void setRootPath(File rootPath) {
		this.rootPath = rootPath;
	}

	@Transient
	public Project createProject()
	{
		return new Project(projectName, projectUUID, null, null);
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Invitation that = (Invitation) o;

		if (creation != null ? !creation.equals(that.creation) : that.creation != null) return false;
		if (invitedOn != null ? !invitedOn.equals(that.invitedOn) : that.invitedOn != null) return false;
		if (inviter != null ? !inviter.equals(that.inviter) : that.inviter != null) return false;
		if (message != null ? !message.equals(that.message) : that.message != null) return false;
		if (projectName != null ? !projectName.equals(that.projectName) : that.projectName != null) return false;
		if (projectUUID != null ? !projectUUID.equals(that.projectUUID) : that.projectUUID != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = projectUUID != null ? projectUUID.hashCode() : 0;
		result = 31 * result + (projectName != null ? projectName.hashCode() : 0);
		result = 31 * result + (creation != null ? creation.hashCode() : 0);
		result = 31 * result + (invitedOn != null ? invitedOn.hashCode() : 0);
		result = 31 * result + (inviter != null ? inviter.hashCode() : 0);
		result = 31 * result + (message != null ? message.hashCode() : 0);
		return result;
	}
}
