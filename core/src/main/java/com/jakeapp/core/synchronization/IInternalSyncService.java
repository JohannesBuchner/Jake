package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.jake.ics.UserId;

/**
 * This interface defines the <b> internal contract </b> a syncservice has to fulfill, so that other
 * <b>internal components</b> can interact with them.
 */
public interface IInternalSyncService extends ISyncService {

	/**
	 * Returns the stored instance of a <code>Project</code> by giving the <code>ProjectId</code>. This
	 * <code>Project</code> has all required fields, e.g. <code>{@link com.jakeapp.core.services.MsgService}</code> or
	 *  <code>{@link Project#rootPath}</code> set.
	 * @param projectId the <code>Id</code> (typically the String representation of a <code>UUID</code>)
	 * @return the <code>Project</code> found, null if nothing is found.
	 */
	public Project getProjectById(String projectId);

	/**
	 * Returns the <code>ChangeListener</code> for all managed <code>Project</code>s.
	 * @return returns the <code>ChangeListener</code>, null if not set.
	 */
	public ChangeListener getProjectChangeListener();


	/**
	 * Send our <code>LogEntry</code>s to the specified recipient.
	 * @param project the <code>Project</code> the <code>LogEntry</code>s belong to.
	 * @param recipient the <code>UserId</code> recipient, we should send our <code>LogEntry</code>s to.
	 */
	void sendLogs(Project project, UserId recipient);
}
