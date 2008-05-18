package com.doublesignal.sepm.jake.core.domain;

/**
 * The <code>FileObject</code> represents a file in the application.
 * It contains of a <code>relpath</code> that points to the file.
 * @author Simon
 */
public class FileObject extends JakeObject {
	
	private String relpath;
	
	/**
	 * Create a new <code>FileObject</code> with a given <code>relpath</code>
	 * @param relpath relative path to the file
	 */
	public FileObject(String relpath) {
		this.relpath = relpath;
	}
	
	/**
	 * Get the relative path to the file.
	 * @return relative path
	 */
    public String getRelpath() {
		return relpath;
	}

    /**
     * Set the relative path to the file.
     * @param relpath relative path to the file
     */
	public void setRelpath(String relpath) {
		this.relpath = relpath;
	}

	/**
	 * Test if two <code>FileObjects</code>are equal.
	 * @return <code>true</code> iff the two <code>relpath</code>s are equal.
	 */
	@Override
	public boolean equals(Object obj) {
        if (obj != null && this.getClass().equals(obj.getClass()))
        {
        	FileObject that = (FileObject)obj;
        	return (this.relpath.equals(that.getRelpath()));
        }
        return false;
    }
}
