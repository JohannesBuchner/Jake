package com.doublesignal.sepm.jake.core.domain;

/**
 * The <code>FileObject</code> represents a file in the application.
 * It contains of a <code>name</code> (i.e the relpath) that points to the file.
 *
 * @author Simon
 */
public class FileObject extends JakeObject {
	/**
	 * Create a new <code>FileObject</code> with a given <code>name</code> i.e
	 * relative path to the file.
	 *
	 * @param name relative path to the file
	 */
	public FileObject(String name) {
		super(name);
	}

	/**
	 * Test if two <code>FileObjects</code>are equal.
	 *
	 * @return <code>true</code> if the two <code>name</code>s are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && this.getClass().equals(obj.getClass())) {
			FileObject that = (FileObject) obj;
			if(this.getName() != null) {
				return (this.getName().equals(that.getName()));
			} else if(that.getName() == null) return true;
		}
		return false;
	}
}
