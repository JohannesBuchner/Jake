package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.jake.ics.UserId;

/**
 * This interface defines the <b> internal contract </b> a syncservice has to fulfill, so that other
 * <b>internal components</b> can interact with them.
 */
public interface IInternalSyncService extends ISyncService {

	public Project getProjectById(String projectId);

	public ChangeListener getProjectChangeListener();

	void sendLogs(Project p, UserId userid);
}
