package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.gui.swing.worker.IJakeTask;

/**
 * Simple Interface that shows that there are tasks started.
 *
 * @author studpete
 */
public interface TaskChangedCallback {
	enum TaskOps {Started, Updated, Finished}

	public void taskStarted(IJakeTask task);
	public void taskUpdated(IJakeTask task);
	public void taskFinished(IJakeTask task);
}
