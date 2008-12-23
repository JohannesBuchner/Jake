package com.jakeapp.core.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * The representation of the jakeObject. A JakeObject is anything that
 * can be shared among clients.
 */

@Entity
@MappedSuperclass
public abstract class JakeObject implements ILogable {
	private static final long serialVersionUID = -3601181472574035652L;
	
	@Id
    private UUID uuid;
    private Project project;

    /**
     * Default ctor.
     */
    public JakeObject() {
    	// default ctor for hibernate
    }

    /**
     * Construct a new <code>JakeObject</code>.
     * @param uuid the <code>uuid</code> of the <code>JakeObject</code>
     * @param project the <code>Project</code> that is associated with the 
     * <code>JakeObject</code>
     * @throws IllegalArgumentException if uuid was not valid
     * @see JakeObject#setUuid(UUID)
     */
    protected JakeObject(UUID uuid, Project project) throws
            IllegalArgumentException {
        this.setUuid(uuid);
        this.setProject(project);
    }

    /**
     * Get the <code>uuid</code>.
     * @return the <code>uuid</code> of the <code>JakeObject</code>
     */
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * Get the associated <code>project</code>.
     * @return the project that is associated with the <code>JakeObject</code>
     * @throws IllegalArgumentException if null is supplied
     */
    public Project getProject() throws IllegalArgumentException {
        return this.project;
    }

    /**
     * Sets the UUID of this JakeObject to the given uuid object.
     * @param uuid the uuid to be set
     * @throws IllegalArgumentException if null is supplied for
     * <code>uuid</code>
     */
    protected void setUuid(UUID uuid) throws IllegalArgumentException {
    	if (uuid == null) {
    		throw new IllegalArgumentException();
    	}
        this.uuid = uuid;
    }

    protected void setProject(Project project) {
        this.project = project;
    }
}
