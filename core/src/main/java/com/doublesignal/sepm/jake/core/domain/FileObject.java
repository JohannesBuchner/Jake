package com.doublesignal.sepm.jake.core.domain;

/**
 * The <code>FileObject</code> represents a file in the application.
 * It contains of a <code>relpath</code> that points to the file.
 *
 * @author Simon
 */
public class FileObject extends JakeObject {
	/**
	 * Create a new <code>FileObject</code> with a given <code>relpath</code>
	 *
	 * @param relpath relative path to the file
	 */
	public FileObject(String relpath) {
		super(relpath);
	}

	/**
	 * Get the relative path to the file.
	 * <p/>
	 * TODO: I left this in here in case any of you still use it. We should get rid of it ASAP.
	 *
	 * @return relative path
	 */
	@Deprecated
	public String getRelpath() {
		return this.getName();
	}

	/**
	 * Test if two <code>FileObjects</code>are equal.
	 *
	 * @return <code>true</code> if the two <code>relpath</code>s are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && this.getClass().equals(obj.getClass())) {
			FileObject that = (FileObject) obj;
			return (this.getName().equals(that.getName()));
		}
		return false;
	}
}
