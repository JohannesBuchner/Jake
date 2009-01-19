package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.core.domain.Project;

/**
 * The global ProjectChanged-Callback Interface.
 * Fires events when projects change.
 * Every registrant get all events.
 * Project is saved as event source.
 */
public interface ProjectChanged {

	/**
	 * Inner class that saves project & change reason
	 */
	public class ProjectChangedEvent {
		private Project project;
		private ProjectChangedReason reason;

		public ProjectChangedEvent(Project project, ProjectChangedReason reason) {
			this.project = project;
			this.reason = reason;
		}

		public Project getProject() {
			return this.project;
		}

		public ProjectChangedReason getReason() {
			return this.reason;
		}

		public enum ProjectChangedReason {
			Created, State, Name, Deleted, Joined, Rejected, People, Invited, Sync;
		}
	}

	public void projectChanged(final ProjectChangedEvent ev);
}
