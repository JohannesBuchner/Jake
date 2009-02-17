package com.jakeapp.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.UUID;

/**
 * The <code>FileObject</code> represents a file in the application.
 * It contains a relative path (i.e the relpath) that points to the file,
 * relative to the project folder.
 * <p/>
 * If you are looking for the absolute path, use <code>sync.getFile(FileObject)</code>
 *
 * @author Simon, christopher
 */
@Entity(name = "fileobject")
public class FileObject extends JakeObject {
	private static final long serialVersionUID = 3865742844467013647L;

	private String relPath;
	private String checksum;
	private transient boolean locallyModified = false;
	private long filesize;

	/**
	 * Default ctor.
	 */
	public FileObject() {
		super();
		//default ctor for hibernate
	}

	/**
	 * Construct a new <code>FileObject</code> with the given parameters.
	 *
	 * @param uuid	 the <code>uuid</code> of the <code>FileObject</code>
	 * @param project the associated <code>project</code>
	 * @param relPath the relative path that leads to the
	 *                <code>FileObject</code>
	 */
	public FileObject(UUID uuid, Project project, String relPath) {
		super(uuid, project);
		this.setRelPath(relPath);
		this.setChecksum("");

		/*
					* locallyModified is always initialized with false. After
					* While the application was not up and running, changes may have
					* happened. The File-System-Service detects the changes that happened
					* before starting the application and sets the locallyModified flag
					* accordingly.
					*/
		this.setLocallyModified(false);
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

	/**
	 * Returns the stored Checksum of this file.
	 *
	 * @return the stored checksum, null if not calculated
	 */
	public String getChecksum() {
		return this.checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	/**
	 * Sets the value of <code>locallyModified</code>.
	 *
	 * @param locallyModified <code>true</code> when a local change is detected.
	 *                        <code>false</code> if local changes are announced and a LogEntry reflecting
	 *                        the modifications is created.
	 */
	public void setLocallyModified(boolean locallyModified) {
		this.locallyModified = locallyModified;
	}

	/**
	 * @return true, if the file was locally modified.
	 */
	public boolean isLocallyModified() {
		return locallyModified;
	}

	@Column(name = "filesize")
	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}

	@Override
	public String toString() {
		return "File [" + super.toString() + "]:" + getRelPath() + " size: " + getFilesize()
				  + " checksum: " + getChecksum();
	}
}
