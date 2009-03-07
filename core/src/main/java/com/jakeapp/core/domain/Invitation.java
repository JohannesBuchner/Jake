package com.jakeapp.core.domain;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.persistence.Id;
import java.util.UUID;
import java.util.Date;

@Entity(name = "invitation")
public class Invitation {

	@Id
	private UUID projectUUID;
	private String projectName;
	private Date creation;
	private User invitedOn;
	private User inviter;
	private String message;

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
//		this.invitedOn =
		this.creation = new Date();	
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
	public Project createProject()
	{
		return new Project(projectName, projectUUID, null, null);
	}

}
