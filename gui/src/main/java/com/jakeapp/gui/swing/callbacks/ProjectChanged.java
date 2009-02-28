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
		private Reason reason;

		public ProjectChangedEvent(Project project, Reason reason) {
			this.project = project;
			this.reason = reason;
		}

		public Project getProject() {
			return this.project;
		}

		public Reason getReason() {
			return this.reason;
		}

		public enum Reason {
			Created, StartStopState, Name, Deleted, Joined, Rejected, People, Invited, Syncing,
			Files
		}
	}

	public void projectChanged(final ProjectChangedEvent ev);
}
