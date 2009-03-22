package com.jakeapp.gui.swing.worker.tasks;

/**
 * @author studpete
 */
// FIXME: add with data later
public interface IJakeTask extends Runnable {

	/**
	 * JakeTasks implement a hashCode, so we can identify them as they run
	 * @return
	 */
	public int hashCode();
	
	/**
	 * get the exception if the task failed, null if it was successful
	 * @return
	 */
	public Exception getException();

}
