package com.jakeapp.core.domain;

import com.jakeapp.core.services.MsgService;

import javax.persistence.*;
import java.io.File;
import java.util.UUID;

/**
 * The Project Entity holds general information about a project in Jake.
 * It is also used to find the correct data for a project in all DAOs.
 * A project belongs to exactly one <code>UserId</code> but has
 * multiple members.
 * <p/>
 * It contains of <ul>
 * <li>a <code>rootPath</code> to specify where the root folder of the project
 * lies on the local filesystem</li>
 * <li>a <code>name</code> specifying the name of the project (the user
 * can change this)</li>
 * <li>a <code>projectId</code> used to internally identify the project</li>
 * <li>a boolean <code>isStarted</code>, specifying if the project is
 * currently running</li>
 * <li>a boolean <code>isAutoAnnounceEnabled</code>, specifying if automatical
 * announcement of changes is enabled</li>
 * <li>a boolean <code>isAutoPullEnabled</code>, specifying if automatical
 * pull of new changes (files) is enabled</li>
 * <li>a <code>UserId</code>-Object to which the project is bound. This
 * object also specifies on which instant
 * messaging network this project operates</li>
 * </ul>
 */
@Entity
@Table(name = "project")
//@UniqueConstraint(columnNames = {"projectId"} )
public class Project implements ILogable {
	private static final long serialVersionUID = 4634971877310089896L;
	private String name;
	private UUID projectId;

	private transient MsgService messageService;
	private File rootPath;
	private transient boolean started;
	private transient boolean open;
	private transient boolean autoAnnounceEnabled = true;
	private transient boolean autoPullEnabled = true;
	private transient boolean autologin;
	private UserId userId;
	private ServiceCredentials credentials;
	private transient InvitationState invitationState = InvitationState.ACCEPTED;

	/**
	 * Construct a Project. Freshly constructed projects are always stopped.
	 *
	 * @param name		 the name of the project
	 * @param projectId  the unique projectId
	 * @param msgService the <code>msgService</code> to be used
	 * @param rootPath	the root path of the project, i.e.
	 *                   the  path of the project folder.
	 */
	public Project(String name, UUID projectId,
						MsgService msgService,
						File rootPath) {
		this.setName(name);
		this.setProjectId(projectId);
		this.setMessageService(msgService);
		this.setRootPath(rootPath);
		this.setCredentials(null);
	}


	public void setProjectId(UUID projectId) {
		this.projectId = projectId;
	}

	public void setProjectId(String projectId) {
		setProjectId(UUID.fromString(projectId));
	}

	/**
	 * A public ctor with no arguments is needed for hibernate.
	 */
	public Project() {

	}

	/**
	 * Get the project id.
	 *
	 * @return the unique <code>projectId</code> of the project
	 */
	@Id
	@Column(name = "UUID", nullable = false, unique = true)
	public String getProjectId() {
		return (this.projectId == null) ? null : this.projectId.toString();
	}

	/**
	 * Get the name.
	 *
	 * @return the name of the project
	 */
	@Column(name = "NAME", nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the message service.
	 *
	 * @return the message service of the project
	 */
	@Column(name = "PROTOCOL", nullable = false)
	@Transient
	public MsgService getMessageService() {
		return this.messageService;
	}

	public void setMessageService(MsgService messageService) {
		this.messageService = messageService;
	}

	/**
	 * Get the root path.
	 *
	 * @return the root path of the project, i.e. the path of the project folder
	 */
	@Column(name = "ROOTPATH", nullable = false)
	public String getRootPath() {
		return this.rootPath.toString();
	}

	public void setRootPath(final String rootPath) {
		this.rootPath = new File(rootPath);
	}

	public void setRootPath(final File rootPath) {
		this.rootPath = rootPath;
	}

	/**
	 * Returns <code>true</code> iff the project is started.
	 *
	 * @return <code>true</code> iff the project is started
	 */
	@Column(name = "STARTED", nullable = false)
	public boolean isStarted() {
		return this.started;
	}

	/**
	 * Set the <code>started</code> flag.
	 *
	 * @param started The new value for started -
	 *                set this whenever you start or stop the proejct.
	 */
	public void setStarted(boolean started) {
		this.started = started;
	}

	/**
	 * Returns <code>true</code> iff the <code>autoAnnounce</code>
	 * feature is enabled.
	 *
	 * @return <code>true</code> iff the <code>autoAnnounce</code>
	 *         feature is enabled
	 */
	@Column(name = "AUTOANNOUNCE", nullable = false)
	public boolean isAutoAnnounceEnabled() {
		return this.autoAnnounceEnabled;
	}

	/**
	 * Set the <code>autoAnnounceEnabled</code> flag.
	 *
	 * @param enabled
	 */
	public void setAutoAnnounceEnabled(boolean enabled) {
		this.autoAnnounceEnabled = enabled;
	}

	/**
	 * Returns <code>true</code> iff the <code>autoPull</code>
	 * feature is enabled.
	 *
	 * @return <code>true</code> iff the <code>autoPull</code>
	 *         feature is enabled
	 */
	@Column(name = "AUTOPULL", nullable = false)
	public boolean isAutoPullEnabled() {
		return this.autoPullEnabled;
	}

	/**
	 * Set the <code>autoPullEnabled</code> flag.
	 *
	 * @param enabled
	 */
	public void setAutoPullEnabled(boolean enabled) {
		this.autoPullEnabled = enabled;
	}

	/**
	 * Get the userId.
	 *
	 * @return the userId that is associated with the project.
	 */
	@Transient
	public UserId getUserId() {
		return this.userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	/**
	 * AutoLogin determines, if the project should automatically
	 * try to login the user if
	 * the project is started and the user credentials are given.
	 *
	 * @return true, if the project automatically logs in the associated user
	 */
	@Column(name = "AUTOLOGIN", nullable = false)
	public boolean isAutologinEnabled() {
		return this.autologin;
	}

	/**
	 * @param open the open to set
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}


	/**
	 * @return the open
	 */
	@Column(name = "OPENED", nullable = false)
	public boolean isOpen() {
		return open;
	}


	public void setAutologinEnabled(boolean enabled) {
		this.autologin = enabled;
	}


	/**
	 * @param credentials the credentials to set
	 */
	public void setCredentials(ServiceCredentials credentials) {
		this.credentials = credentials;
	}

	/**
	 * @return the credentials
	 */
	//@Column(name="USERID", nullable = false)
	@ManyToOne(targetEntity = ServiceCredentials.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "userid", nullable = true)
	public ServiceCredentials getCredentials() {
		return credentials;
	}

	/**
	 * @param invitationState the invitationState to set
	 */
	public void setInvitationState(InvitationState invitationState) {
		this.invitationState = invitationState;
	}


	/**
	 * @return the invitationState
	 */
//    @Transient // TODO change here to save invitation state
	@Column(name = "invitationstate")
	public InvitationState getInvitationState() {
		return invitationState;
	}

	/**
	 * Convenicence Methode for getInvitationState.
	 *
	 * @return: true if project is invited only.
	 */
	@Transient
	public boolean isInvitation() {
		return getInvitationState() == InvitationState.INVITED;
	}

	/**
	 * The toString-Representation for debugging.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "Project " + getName() + "(" + getProjectId() + "), " +
				  getRootPath() + " started: " + isStarted();
	}

	/**
	 * Tests if to <code>Projects</code> are equal.
	 *
	 * @param obj The <code>Object</code> to compare this object to.
	 * @return <code>true</code> iff the <code>name, projectId </code> and
	 *         <code> rootPath</code> are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Project other = (Project) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.projectId == null) {
			if (other.projectId != null) {
				return false;
			}
		} else if (!this.projectId.equals(other.projectId)) {
			return false;
		}
		if (this.rootPath == null) {
			if (other.rootPath != null) {
				return false;
			}
		} else if (!this.rootPath.equals(other.rootPath)) {
			return false;
		}
		return true;
	}

	/**
	 * Generate the hash code using <code>name, projectId, rootPath</code>.
	 *
	 * @return hashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0
				  : this.name.hashCode());
		result = prime * result
				  + ((this.projectId == null) ? 0 : this.projectId.hashCode());
		result = prime * result
				  + ((this.rootPath == null) ? 0 : this.rootPath.hashCode());
		return result;
	}


}
