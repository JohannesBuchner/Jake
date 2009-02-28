package com.jakeapp.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.UUID;

/**
 * The <code>FileObject</code> represents a file in the application. It contains
 * a relative path (i.e the relpath) that points to the file, relative to the
 * project folder. <p/> If you are looking for the absolute path, use
 * <code>sync.getFile(FileObject)</code>
 * 
 * @author Simon, christopher
 */
@Entity(name = "fileobject")
public class FileObject extends JakeObject {

	private static final long serialVersionUID = 3865742844467013647L;

	private String relPath;

	public FileObject() {
		super();
		// default ctor for hibernate
	}

	/**
	 * Construct a new <code>FileObject</code> with the given parameters.
	 * No UUID is set. It is completed on persisting.
	 * 
	 * @param project
	 *            the associated <code>project</code>
	 * @param relPath
	 *            the relative path that leads to the <code>FileObject</code>
	 */
	public FileObject(Project project, String relPath) {
		this.setProject(project);
		this.setRelPath(relPath);
	}
	
	/**
	 * Construct a new <code>FileObject</code> with the given parameters.
	 * 
	 * @param uuid
	 * @param project
	 *            the associated <code>project</code>
	 * @param relPath
	 *            the relative path that leads to the <code>FileObject</code>
	 */
	public FileObject(UUID uuid, Project project, String relPath) {
		super(uuid, project);
		this.setRelPath(relPath);
	}

	/**
	 * Returns the path of this file object, relative to the
	 * <code>Project</code>'s root folder.
	 * 
	 * @return the <code>relPath</code> to the file
	 */
	@Column(name = "relPath", nullable = false)
	public String getRelPath() {
		return this.relPath;
	}

	private void setRelPath(String relPath) {
		this.relPath = relPath;
	}

	@Override
	public String toString() {
		return "File [" + super.toString() + "]:" + getRelPath();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof FileObject))
			return false;
		if (!super.equals(o))
			return false;

		FileObject that = (FileObject) o;

		if (relPath != null ? !relPath.equals(that.relPath) : that.relPath != null)
			return false;

		return super.equals(o);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (relPath != null ? relPath.hashCode() : 0);
		return result;
	}
}
