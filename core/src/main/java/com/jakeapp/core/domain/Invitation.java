package com.jakeapp.core.domain;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.persistence.Id;
import java.util.UUID;
import java.util.Date;
import java.io.File;

/**
 * This class is used to persist received invitations in the global database.
 */
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

	/**
	 * Constructor to re-create an <code>Invitation</code> with the specified values
	 * @param projectUUID the <code>UUID</code> of the <code>Project</code> belonging to the <code>Invitation</code>
	 * @param projectName the name of the <code>Project</code> belonging to the <code>Invitation</code>
	 * @param creation the <code>Date</code> the <code>Invitation</code> was issued
	 * @param invitedOn the <code>User</code>Id which received the <code>Invitation</code>.
	 * @param inviter the <code>User</code> which sent the <code>Invitation</code>
	 * @param message a message added to the invitation. 
	 */
	public Invitation(UUID projectUUID, String projectName, Date creation, User invitedOn, User inviter, String message) {
		this.projectUUID = projectUUID;
		this.projectName = projectName;
		this.creation = creation;
		this.invitedOn = invitedOn;
		this.inviter = inviter;
		this.message = message;
	}

	/**
	 * Constructor to create a new <code>Invitation</code> to the specific <code>Project</code>
	 * @param project The <code>Project</code> belonging to the <code>Invitation</code>
	 * @param inviter The <code>User</code> who sent the <code>Invitation</code>
	 */
	public Invitation(Project project, User inviter)
	{
		this.projectUUID = UUID.fromString(project.getProjectId());
		this.projectName = project.getName();
		this.inviter = inviter;
		this.invitedOn = project.getUserId();
		this.creation = new Date();	
	}

	/**
	 * Constructor to create the <code>Invitation</code> to the specific <code>Project</code> and a given
	 * <code>rootPath</code>
	 * @param project The <code>Project</code> belonging to the <code>Invitation</code>
	 * @param inviter The <code>User</code> who sent the <code>Invitation</code>
	 * @param rootPath The <code>rootPath</code> where the <code>Project</code> should be created.
	 */
	public Invitation(Project project, User inviter, File rootPath)
	{
		this(project,  inviter);
		setRootPath(rootPath);
	}

	private Invitation() {

	}

	/**
	 * Gets the <code>UUID</code> of the <code>Project</code>.
	 * @return the <code>UUID</code> of the <code>Project</code>.
	 */
	public UUID getProjectUUID() {
		return projectUUID;
	}

	public void setProjectUUID(UUID projectUUID) {
		this.projectUUID = projectUUID;
	}

	/**
	 * Get the name of the <code>Project</code>.
	 * @return returns the name of the <code>Project</code>.
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * sets the name of the <code>Project</code>
	 * @param projectName the name of the <code>Project</code>
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the <code>Date</code> this <code>Invitation</code> was issued.
	 */
	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	/**
	 * @return the <code>User</code>Id which received the <code>Invitation</code>. 
	 */
	public User getInvitedOn() {
		return invitedOn;
	}

	public void setInvitedOn(User invitedOn) {
		this.invitedOn = invitedOn;
	}

	/**
	 * @return the <code>User</code> which sent the <code>Invitation</code>.
	 */
	public User getInviter() {
		return inviter;
	}

	public void setInviter(User inviter) {
		this.inviter = inviter;
	}

	/**
	 * @return the Message specified by the <code>User</code> who sent the <code>Invitation</code>
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Getting the <code>rootPath</code> set for this <code>Project</code>s <code>Invitation</code>
	 * <em>This is not persisted to the database!</em>
	 * @return a <code>File</code> representing the <code>rootPath</code> of the <code>Invitation</code>
	 */
	@Transient
	public File getRootPath() {
		return rootPath;
	}

	public void setRootPath(File rootPath) {
		this.rootPath = rootPath;
	}

	/**
	 * Convinience method to get a <code>Project</code> out of an <code>Invitation</code>
	 * <em>Attention: The <code>MsgService</code> does not get set!</em>
	 * @return the <code>Project</code> to be created
	 */
	@Transient
	public Project createProject()
	{
		return new Project(projectName, projectUUID, null, rootPath);
	}


	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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

	@Override
	public String toString() {
		return "Invitation{" +
				"projectUUID=" + projectUUID +
				", projectName='" + projectName + '\'' +
				", creation=" + creation +
				", invitedOn=" + invitedOn +
				", inviter=" + inviter +
				", message='" + message + '\'' +
				", rootPath=" + rootPath +
				'}';
	}
}
