package com.jakeapp.core.domain;

import java.io.File;
import java.util.UUID;

import javax.persistence.Entity;

/**
 * The <code>FileObject</code> represents a file in the application.
 * It contains a relative path (i.e the relpath) that points to the file,
 * relative to the project folder.
 *
 * @author Simon,christopher
 */
@Entity
public class FileObject extends JakeObject {
	private static final long serialVersionUID = 3865742844467013647L;

    private String relPath;
    private String checksum;
    private File absolutePath;


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
     * @param uuid    the <code>uuid</code> of the <code>FileObject</code>
     * @param project the associated <code>project</code>
     * @param relPath the relative path that leads to the
     * <code>FileObject</code>
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
    public String getRelPath() {
        return this.relPath;
    }

    /**
     * Get the absolute path.
     *
     * @return the absolute path to the file
     */
    public File getAbsolutePath() {
        return this.absolutePath;
    }

    // TODO: are there any constraints on the path, like forward- or backslash?
    private void setRelPath(String relPath) {
        this.relPath = relPath;
        this.setAbsolutePath(new File(this.getProject().getRootPath(),
                relPath));
    }

    private void setAbsolutePath(File absolutePath) {
        this.absolutePath = absolutePath;
    }

    /**
     * Returns the stored CheckSum of this file.
     *
     * @return the stored checksum, null if not calculated
     */
    public String getChecksum() {
        return this.checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
