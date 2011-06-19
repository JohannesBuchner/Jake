package com.jakeapp.violet.model;

/**
 * Objects listening for log modifications have to implement this.
 * 
 * @author johannes
 */
public interface ILogModificationListener {

	/**
	 * Actions that can occur for a logentry in the log.
	 */
	public enum ModifyActions {
		CREATED, DELETED, MODIFIED
	}

	/**
	 * method to be implemented by an modification listener to get notified of
	 * changes
	 * 
	 * @param file
	 *            the FileObject changed
	 * @param action
	 *            the action that happend (created, deleted, modified..)
	 */
	void logModified(JakeObject jo, ModifyActions action);

}
