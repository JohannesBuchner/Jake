package com.jakeapp.violet.model;


/**
 * Objects listening for log modifications have to implement this.
 * 
 * @author johannes
 */
public interface IJakeObjectModificationListener {

	/**
	 * method to be implemented by an modification listener to get notified of
	 * changes
	 * 
	 * @param file
	 *            the FileObject changed
	 */
	void modified(JakeObject jo);

}
